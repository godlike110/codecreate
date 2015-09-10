package com.codegen.db.service;

import com.codegen.db.model.FieldInfo;
import com.codegen.db.model.TableInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public class DBInfoService {
	private String columnTypeProperties;

	public DBInfoService(String columnTypeProperties) {
		this.columnTypeProperties = columnTypeProperties;
	}

	public List<TableInfo> getTableList(String driverClass, String url, String userName,
			String password, String tablesStr) throws SQLException {
		Connection conn = getJbdcConnection(driverClass, url, userName, password);
		List<TableInfo> list = new ArrayList();
		if (conn == null) {
			return list;
		}
		Map<String, String> typeMap = getJavaType(this.columnTypeProperties);
		try {
			DatabaseMetaData meta = conn.getMetaData();
			List<String> tables = getAllTables(meta, url, tablesStr);
			for (String tableName : tables) {
				TableInfo table = getTableInfo(meta, tableName, typeMap);
				if (table != null) {
					list.add(table);
				}
			}
		} catch (SQLException e) {
			DatabaseMetaData meta;
			throw e;
		} finally {
			closeConn(conn);
		}
		return list;
	}

	public static void main(String[] args) throws SQLException {
		DBInfoService dd = new DBInfoService("mysql.properties");
		String dburl = "jdbc:mysql://10.10.90.156:3306/newfshouse";
		String dbUser = "develop";
		String dbPassword = "p3m12d";
		String tbNamePattern = "%";
		List<TableInfo> list = dd.getTableList("com.mysql.jdbc.Driver", dburl, dbUser,
				dbPassword, tbNamePattern);
		System.out.println("*********************=  " + list.size());
	}

	private List<String> getAllTables(DatabaseMetaData meta, String url, String tablesStr)
			throws SQLException {
		String[] tables = StringUtils.split(tablesStr, ",");
		List<String> result = new ArrayList();
		String dbName = getDbNameByUrl(url);
		for (String str : tables) {
			String[] arr = StringUtils.split(str, ".");
			if (arr.length != 0) {
				List<String> list = showTables(meta, arr.length == 2 ? arr[0] : dbName,
						arr.length == 2 ? arr[1] : arr[0]);
				result.addAll(list);
			}
		}
		return result;
	}

	private TableInfo getTableInfo(DatabaseMetaData meta, String tableName,
			Map<String, String> typeMap) throws SQLException {
		String[] arr = StringUtils.split(tableName, ".");
		if (arr.length != 2) {
			return null;
		}
		TableInfo table = new TableInfo();
		table.setTableName(arr[1]);
		table.setFields(getColumns(meta, arr[0], arr[1], typeMap));
		return table;
	}

	private List<String> showTables(DatabaseMetaData meta, String dbName,
			String tableNamePattern) throws SQLException {
		List<String> list = new ArrayList();
		ResultSet rsTable = null;
		try {
			rsTable = meta.getTables(dbName, null, tableNamePattern, new String[] {
					"TABLE", "VIEW" });
			while (rsTable.next()) {
				list.add(dbName + "." + rsTable.getString(3));
			}
			if (list.size() == 0) {
				throw new SQLException("未找到与  " + dbName + "." + tableNamePattern
						+ "匹配的数据库表！请确认输入参数是否正确!");
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			closeRs(rsTable);
		}
		return list;
	}

	private List<String> getPrimaryKeys(DatabaseMetaData meta, String dbName,
			String tableName) throws SQLException {
		List<String> keyList = new ArrayList();
		ResultSet rs = null;
		try {
			rs = meta.getPrimaryKeys(dbName, null, tableName);
			while (rs.next()) {
				keyList.add(rs.getString(4));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			closeRs(rs);
		}
		return keyList;
	}

	private List<FieldInfo> getColumns(DatabaseMetaData meta, String dbName,
			String tableName, Map<String, String> typeMap) throws SQLException {
		List<FieldInfo> list = new ArrayList();
		List<String> priKeys = getPrimaryKeys(meta, dbName, tableName);
		ResultSet rs = null;
		try {
			rs = meta.getColumns(dbName, null, tableName, null);
			ResultSet rsIndex = meta.getIndexInfo(dbName, null, tableName, true, false);
			Map<String, String> colIndexMap = new HashMap();
			while (rsIndex.next()) {
				colIndexMap.put(rsIndex.getString("COLUMN_NAME"),
						rsIndex.getString("INDEX_NAME"));
			}
			while (rs.next()) {
				FieldInfo info = new FieldInfo();
				info.setName(rs.getString(4));
				info.setType(getJavaTypeByField(typeMap, rs.getString(6)));
				info.setIsAutoIncrement("yes".equalsIgnoreCase(rs.getString(23)));
				info.setIsPriKey(priKeys.contains(info.getName()));
				info.setIsNull("0".equals(rs.getString("NULLABLE")) ? "NO" : "YES");
				info.setDefaultVal(rs.getString("COLUMN_DEF"));
				info.setDbType(rs.getString("TYPE_NAME"));
				info.setLength(rs.getString("COLUMN_SIZE"));

				info.setDesc(rs.getString("REMARKS"));
				info.setKey(colIndexMap.get(rs.getString(4)) == null ? ""
						: (String) colIndexMap.get(rs.getString(4)));
				list.add(info);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			closeRs(rs);
		}
		return list;
	}

	private String getRightCharSetString(String str) {
		try {
			return new String(str.getBytes("iso8859-1"), "gbk");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Map<String, String> getJavaType(String fileName) {
		Map<String, String> map = new HashMap();
		InputStream is = DBInfoService.class.getClassLoader().getResourceAsStream(
				fileName);
		try {
			Properties p = new Properties();
			p.load(is);
			Iterator<Map.Entry<Object, Object>> it = p.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Object, Object> entry = (Map.Entry) it.next();
				map.put(entry.getKey().toString().toLowerCase(), entry.getValue()
						.toString());
			}
		} catch (IOException e) {
		}
		return map;
	}

	private String getJavaTypeByField(Map<String, String> typeMap, String fieldType) {
		int index = fieldType.indexOf(" ");
		fieldType = index > 0 ? fieldType.substring(0, index) : fieldType;
		String javaType = (String) typeMap.get(fieldType.toLowerCase());
		return javaType == null ? "String" : javaType;
	}

	private String getDbNameByUrl(String url) {
		int index = url.indexOf("?");
		url = index > 0 ? url.substring(0, index) : url;
		return url.substring(url.lastIndexOf("/") + 1);
	}

	private static Connection getJbdcConnection(String driverClassName, String url,
			String userName, String password) throws SQLException {
		try {
			Class.forName(driverClassName).newInstance();
		} catch (Exception e) {
			return null;
		}
		try {
			return DriverManager.getConnection(url, userName, password);
		} catch (SQLException e) {
			throw e;
		}
	}

	private void closeRs(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
		}
	}

	private void closeConn(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
		}
	}
}
