package com.codegen.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

public class NamingUtils {
    
    private NamingUtils() { 
    }
    
    /**
     * 获取大驼峰规则名
     * 比如：user_space --> UserSpace 
     */
    public static String getPascalName(String name) {
        StringBuilder sb = new StringBuilder();
        String[] strList = StringUtils.split(name,"_");
        for(String word : strList) { 
            sb.append(WordUtils.capitalizeFully(word));
        }
        return sb.toString();
    }
    
    /**
     * 获取小驼峰规则名
     * 比如：user_space --> userSpace 
     */
    public static String getCamelName(String name) {
        StringBuilder sb = new StringBuilder(getPascalName(name));
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }
    /**
     *  获取大驼峰规则名,如果末位有数字，把数字去掉，主要处理闪表
     *  比如：user_space_39 --> UserSpace 
     * @param tableName
     * @return
     */
    public static String getTabletPascalName(String tableName){
        
        StringBuilder sb = new StringBuilder();
        String[] strList = StringUtils.split(tableName,"_");
        for(int i=0; i<strList.length -1; i++){
            sb.append(WordUtils.capitalizeFully(strList[i]));
        }
        if(!strList[strList.length-1].matches("\\d*")){
            sb.append(WordUtils.capitalizeFully(strList[strList.length-1]));
        }

        return sb.toString();
   }
    

}
