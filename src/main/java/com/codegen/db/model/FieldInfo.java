package com.codegen.db.model;

import java.io.Serializable;

import com.codegen.utils.NamingUtils;

/**
 * 表示数据库表 某字段信息
 */
public class FieldInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段类型
     */
    private String type = "String";

    /**
     *  是否主键 
     */
    private boolean isPriKey;

    /**
     *  是否自增
     */
    private boolean isAutoIncrement;
    
    public String getCamelName(){
        return NamingUtils.getCamelName(name);
    }
    
    public String getPascalName() { 
        return NamingUtils.getPascalName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsPriKey() {
        return isPriKey;
    }

    public void setIsPriKey(boolean isPriKey) {
        this.isPriKey = isPriKey;
    }

    public void setIsAutoIncrement(boolean isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }

    public boolean getIsAutoIncrement() {
        return isAutoIncrement;
    }

}
