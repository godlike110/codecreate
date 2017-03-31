package com.codegen.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

public class NamingUtils {

	public static boolean needToSwap(String name) {
		if(StringUtils.isNotEmpty(name)) {
			if(StringUtils.split(name,"_").length>=2) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		String a = "path_enabled";
		System.out.println(needToSwap(a));
	}

	public static String getPascalName(String name) {
		StringBuilder sb = new StringBuilder();
		String[] strList = StringUtils.split(name, "_");
		for (String word : strList) {
			sb.append(WordUtils.capitalizeFully(word));
		}
		return sb.toString();
	}

	public static String getCamelName(String name) {
		StringBuilder sb = new StringBuilder(getPascalName(name));
		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
		return sb.toString();
	}

	public static String getTabletPascalName(String tableName) {
		StringBuilder sb = new StringBuilder();
		String[] strList = StringUtils.split(tableName, "_");
		for (int i = 0; i < strList.length - 1; i++) {
			sb.append(WordUtils.capitalizeFully(strList[i]));
		}
		if (!strList[(strList.length - 1)].matches("\\d*")) {
			sb.append(WordUtils.capitalizeFully(strList[(strList.length - 1)]));
		}
		return sb.toString();
	}
}
