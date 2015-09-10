package com.codegen.db.model;

import com.codegen.utils.NamingUtils;
import java.io.Serializable;

public class FieldInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String type = "String";
	private boolean isPriKey;
	private boolean isAutoIncrement;
	private String isNull;
	private String defaultVal;
	private String dbType;
	private String length;
	private String desc;
	private String key;

	public String getCamelName() {
		return NamingUtils.getCamelName(this.name);
	}

	public String getPascalName() {
		return NamingUtils.getPascalName(this.name);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getIsPriKey() {
		return this.isPriKey;
	}

	public void setIsPriKey(boolean isPriKey) {
		this.isPriKey = isPriKey;
	}

	public void setIsAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}

	public boolean getIsAutoIncrement() {
		return this.isAutoIncrement;
	}

	public String getIsNull() {
		return this.isNull;
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

	public String getDefaultVal() {
		return this.defaultVal == null ? "null" : this.defaultVal;
	}

	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}

	public String getDbType() {
		return this.dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getLength() {
		return this.length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
