package com.codegen.db.service;

import com.codegen.db.model.FieldInfo;
import com.codegen.db.model.TableInfo;

import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.lang.StringUtils;

public class DBInfoService {
    
    private String columnTypeProperties;
    
    public DBInfoService(String columnTypeProperties) { 
        this.columnTypeProperties = columnTypeProperties;
    }

    public List<TableInfo> getTableList(String driverClass,String url, String userName, String password, String tablesStr)
            throws SQLException {
        Connection conn = getJbdcConnection(driverClass, url, userName, password);
        List<TableInfo> list = new ArrayList<TableInfo>();
        if (conn == null) {
            return list;
        }
        Map<String, String> typeMap = getJavaType(columnTypeProperties);
        
        try {
            DatabaseMetaData meta = conn.getMetaData();
            List<String> tables = this.getAllTables(meta, url, tablesStr);
            for (String tableName : tables) {
                TableInfo table = this.getTableInfo(meta, tableName, typeMap);
                if (table != null) {
                    list.add(table);
                }
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            closeConn(conn);
        }
        return list;
    }

    /**
     * 获取符合条件的数据库表集合 格式：dbName.tableName
     */
    private List<String> getAllTables(DatabaseMetaData meta, String url, String tablesStr) throws SQLException {
        String[] tables = StringUtils.split(tablesStr, ",");
        List<String> result = new ArrayList<String>();
        String dbName = getDbNameByUrl(url);
        for (String str : tables) {
            String[] arr = StringUtils.split(str, ".");
            if (arr.length == 0) {
                continue;
            }
            List<String> list = showTables(meta, arr.length == 2 ? arr[0] : dbName, arr.length == 2 ? arr[1] : arr[0]);
            result.addAll(list);
        }
        return result;
    }

    private TableInfo getTableInfo(DatabaseMetaData meta, String tableName, Map<String, String> typeMap)
            throws SQLException {
        String[] arr = StringUtils.split(tableName, ".");
        if (arr.length != 2) {
            return null;
        }
        TableInfo table = new TableInfo();
        table.setTableName(arr[1]);
        table.setFields(this.getColumns(meta, arr[0], arr[1], typeMap));
        return table;
    }

    /**
     * 得到指定连接数据库中表列表
     */
    private List<String> showTables(DatabaseMetaData meta, String dbName, String tableNamePattern) throws SQLException {
        List<String> list = new ArrayList<String>();
        ResultSet rsTable = null;
        try {
            rsTable = meta.getTables(dbName, null, tableNamePattern, new String[] { "TABLE", "VIEW" });
            while (rsTable.next()) {
                list.add(dbName + "." + rsTable.getString(3)); // 第三列就是表名称
                
                System.out.println("*************** table_"+rsTable.getString(3));
            }
            if (list.size() == 0) {
                throw new SQLException("未找到与 " + dbName + "." + tableNamePattern + "匹配的数据库表！请确认输入的参数是否正确！");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            closeRs(rsTable);
        }
        return list;
    }

    /**
     * 获得主键信息
     */
    private List<String> getPrimaryKeys(DatabaseMetaData meta, String dbName, String tableName) throws SQLException {
        List<String> keyList = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = meta.getPrimaryKeys(dbName, null, tableName);
            while (rs.next()) {
                keyList.add(rs.getString(4));// 第4列就是主键名
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            closeRs(rs);
        }
        return keyList;
    }

    /**
     * 获得表中列信息
     */
    private List<FieldInfo> getColumns(DatabaseMetaData meta, String dbName, String tableName,
            Map<String, String> typeMap) throws SQLException {
        List<FieldInfo> list = new ArrayList<FieldInfo>();
        List<String> priKeys = getPrimaryKeys(meta, dbName, tableName);
        ResultSet rs = null;
        try {
            rs = meta.getColumns(dbName, null, tableName, null);
            while (rs.next()) {
                FieldInfo info = new FieldInfo();
                info.setName(rs.getString(4));
                info.setType(getJavaTypeByField(typeMap, rs.getString(6)));
                info.setIsAutoIncrement("yes".equalsIgnoreCase(rs.getString(23)) ? true : false);
                info.setIsPriKey(priKeys.contains(info.getName()) ? true : false);
                list.add(info);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            closeRs(rs);
        }
        return list;
    }

    private static Map<String, String> getJavaType(String fileName) {
        Map<String, String> map = new HashMap<String, String>();
        InputStream is = DBInfoService.class.getClassLoader().getResourceAsStream(fileName);
        try {
            Properties p = new Properties();
            p.load(is);
            Iterator<Entry<Object, Object>> it = p.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Object, Object> entry = it.next();
                map.put(entry.getKey().toString().toLowerCase(), entry.getValue().toString());
            }
        } catch (IOException e) {
            //
        }
        return map;
    }

    private String getJavaTypeByField(Map<String, String> typeMap, String fieldType) {
        int index = fieldType.indexOf(" ");
        fieldType = (index > 0 ? fieldType.substring(0, index) : fieldType);
        String javaType = typeMap.get(fieldType.toLowerCase());
        return javaType == null ? "String" : javaType;
    }

    /**
     * 根据数据库连接，获取连接的数据库的名称
     */
    private String getDbNameByUrl(String url) {
        int index = url.indexOf("?");
        url = (index > 0 ? url.substring(0, index) : url);
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private static Connection getJbdcConnection(String driverClassName, String url, String userName, String password)
            throws SQLException {
        try {
            Class.forName(driverClassName).newInstance();
        } catch (Exception e) {
            return null;
        }
        // 建立连接
        try {
            Connection conn = DriverManager.getConnection(url, userName, password);
            return conn;
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
            //
        }
    }

    private void closeConn(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            //
        }
    }

}
