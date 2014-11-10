package com.codegen.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.codegen.utils.NamingUtils;

/**
 * 表示数据库 某个表的名字
 */
public class TableInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表名称 : 带数据库名 db.tablename
     */
    private String tableName;
    /**
     * 是否是闪表
     */
    private boolean isFlashTable;
    /**
     * 表字段集合
     */
    private List<FieldInfo> fields;
    
    public String getPascalName() { 
        return NamingUtils.getTabletPascalName(tableName);
    }
    
    public String getLowerName() { 
        return NamingUtils.getTabletPascalName(tableName).toLowerCase();
    }

    public String getVMTableName() {
        String[] strList = StringUtils.split(tableName,"_");
        if(strList[strList.length-1].matches("\\d*")){ //表名_数字
            return "$"+NamingUtils.getTabletPascalName(tableName).toLowerCase();
        }
        return tableName;
    }
    
    public String getTableName() {
        if(isFlashTable){
            return NamingUtils.getTabletPascalName(tableName).toLowerCase();
        }
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setFields(List<FieldInfo> fields) {
        this.fields = fields;
    }

    public List<FieldInfo> getFields() {
        return fields == null ? new ArrayList<FieldInfo>() : fields;
    }

    
    public boolean getIsFlashTable() {
        String[] strList = StringUtils.split(tableName,"_");
        
        if(strList[strList.length-1].matches("\\d*")){ //表名_数字
            return true;
        }
        return false;
    }

    public void setFlashTable(boolean isFlashTable) {
        this.isFlashTable = isFlashTable;
    }

    public FieldInfo getPriKey() {
        for (FieldInfo info : this.getFields()) {
            if (info.getIsPriKey()) {
                return info;
            }
        }
        // 若此表没有主键，则已第一列的为准
        return this.getFields().get(0);
    }
    
    public String getSelectBody() {
        StringBuffer sb = new StringBuffer();
        for (FieldInfo info : this.getFields()) {
            sb.append(" ").append(info.getName()).append(",");
        }
        String body = sb.toString();
        return StringUtils.chomp(body , ",");
    }

    public String getInsertBody() {
        StringBuffer sb = new StringBuffer();
        for (FieldInfo info : this.getFields()) {
            if (!info.getIsAutoIncrement()) {
                sb.append(" ").append(info.getName()).append(",");
            }
        }
        String body = sb.toString();
        return StringUtils.chomp(body , ",");
    }

    public String getInsertJadeBody() {
        StringBuffer sb = new StringBuffer();
        for (FieldInfo info : this.getFields()) {
            if (!info.getIsAutoIncrement()) {
                sb.append(" :1.").append(NamingUtils.getCamelName(info.getName())).append(",");
            }
        }
        String body = sb.toString();
        return StringUtils.chomp(body , ",");
    }
    
    public String getInsertValueBody() {
        StringBuffer sb = new StringBuffer();
        for (FieldInfo info : this.getFields()) {
            if (!info.getIsAutoIncrement()) {
                sb.append(" #").append(info.getCamelName()).append("#").append(",");
            }
        }
        String body = sb.toString();
        return StringUtils.chomp(body , ",");
    }


}
