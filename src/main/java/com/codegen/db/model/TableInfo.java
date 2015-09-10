package com.codegen.db.model;

import com.codegen.utils.NamingUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class TableInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String tableName;
	private boolean isFlashTable;
	private List<FieldInfo> fields;

	public String getPascalName() {
		return NamingUtils.getTabletPascalName(this.tableName);
	}

	public String getLowerName() {
		return NamingUtils.getTabletPascalName(this.tableName).toLowerCase();
	}

	public String getVMTableName() {
		String[] strList = StringUtils.split(this.tableName, "_");
		if (strList[(strList.length - 1)].matches("\\d*")) {
			return "$" + NamingUtils.getTabletPascalName(this.tableName).toLowerCase();
		}
		return this.tableName;
	}

	public String getTableName() {
		if (this.isFlashTable) {
			return NamingUtils.getTabletPascalName(this.tableName).toLowerCase();
		}
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setFields(List<FieldInfo> fields) {
		this.fields = fields;
	}

	public List<FieldInfo> getFields() {
		return this.fields == null ? new ArrayList() : this.fields;
	}

	public boolean getIsFlashTable() {
		String[] strList = StringUtils.split(this.tableName, "_");
		if (strList[(strList.length - 1)].matches("\\d*")) {
			return true;
		}
		return false;
	}

	public void setFlashTable(boolean isFlashTable) {
		this.isFlashTable = isFlashTable;
	}

	public FieldInfo getPriKey() {
		for (FieldInfo info : getFields()) {
			if (info.getIsPriKey()) {
				return info;
			}
		}
		return (FieldInfo) getFields().get(0);
	}

	public String getSelectBody() {
		StringBuffer sb = new StringBuffer();
		for (FieldInfo info : getFields()) {
			sb.append(" ").append(info.getName()).append(",");
		}
		String body = sb.toString();
		return StringUtils.chomp(body, ",");
	}

	public String getParamBody() {
		StringBuffer sb = new StringBuffer();
		for (FieldInfo info : getFields()) {
			sb.append(info.getType()).append(" ").append(info.getCamelName()).append(",");
		}
		String body = sb.toString();
		return StringUtils.chomp(body, ",");
	}

	public String getInsertBody() {
		StringBuffer sb = new StringBuffer();
		for (FieldInfo info : getFields()) {
			sb.append(" ").append(info.getName()).append(",");
		}
		String body = sb.toString();
		return StringUtils.chomp(body, ",");
	}

	public String getInsertJadeBody() {
		StringBuffer sb = new StringBuffer();
		for (FieldInfo info : getFields()) {
			if (!info.getIsAutoIncrement()) {
				sb.append(" :1.").append(NamingUtils.getCamelName(info.getName()))
						.append(",");
			}
		}
		String body = sb.toString();
		return StringUtils.chomp(body, ",");
	}

	public String getInsertValueBody() {
		StringBuffer sb = new StringBuffer();
		for (FieldInfo info : getFields()) {
			if (!info.getIsAutoIncrement()) {
				sb.append(" #").append(info.getCamelName()).append("#").append(",");
			}
		}
		String body = sb.toString();
		return StringUtils.chomp(body, ",");
	}

	public String getRealTableName() {
		return this.tableName;
	}
}
