package com.codegen.ibatis;

import com.codegen.db.model.TableInfo;
import com.codegen.utils.NamingUtils;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class IBatisCodegenService {
	private String modelPkg;
	private String daoPkg;
	private String srcFolder;
	private String xmlPkg;
	private String baseObjectClass;
	private String pkClass;
	private String genericDaoClass;

	public IBatisCodegenService(String model, String dao, String xml, String src) {
		this.modelPkg = model;
		this.daoPkg = dao;
		this.srcFolder = src;
		this.xmlPkg = xml;
	}

	public void setBaseObjectClass(String baseObjectClass) {
		this.baseObjectClass = baseObjectClass;
	}

	public void setPkClass(String pkClass) {
		this.pkClass = pkClass;
	}

	public void setGenericDaoClass(String genericDaoClass) {
		this.genericDaoClass = genericDaoClass;
	}

	public void createFile(List<TableInfo> list, Log log) throws Exception {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty("resource.loader", "class");
		ve.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		ve.setProperty("input.encoding", "UTF-8");
		ve.setProperty("output.encoding", "UTF-8");

		ve.init();

		VelocityContext context = new VelocityContext();

		context.put("modelPkg", this.modelPkg);
		context.put("daoPkg", this.daoPkg);

		context.put("baseObjectClass", this.baseObjectClass);
		context.put("pkClass", this.pkClass);
		context.put("genericDaoClass", this.genericDaoClass);

		String genericDaoClassShort = this.genericDaoClass.substring(this.genericDaoClass
				.lastIndexOf(".") + 1);
		context.put("genericDaoClassShort", genericDaoClassShort);

		String prefixPath = this.srcFolder + File.separator;
		for (TableInfo table : list) {
			context.put("table", table);

			log.info("processing table :" + table.getTableName());

			String pascalName = NamingUtils.getPascalName(table.getTableName());

			String modelPath = getRealPath(prefixPath, this.modelPkg);
			String modelFileName = modelPath + pascalName + ".java";
			doMerge(modelFileName, ve, context, "template/ibatis/model.vm");

			log.info("generate: " + modelFileName);

			String daoPath = getRealPath(prefixPath, this.daoPkg);
			String daoFileName = daoPath + pascalName + "Dao.java";
			doMerge(daoFileName, ve, context, "template/ibatis/dao.vm");

			log.info("generate: " + daoFileName);

			String xmlPath = getRealPath(prefixPath, this.xmlPkg);
			String xmlFileName = xmlPath + pascalName + "SQL.xml";
			doMerge(xmlFileName, ve, context, "template/ibatis/ibatis.vm");

			log.info("generate: " + xmlFileName);
		}
	}

	private static void doMerge(String fileName, VelocityEngine ve,
			VelocityContext context, String template) throws Exception {
		File modelFile = new File(fileName);
		PrintWriter pw = new PrintWriter(modelFile);
		Template t = ve.getTemplate(template);
		t.merge(context, pw);
		pw.close();
	}

	private static String getRealPath(String srcPrefix, String pkgName) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(srcPrefix);
		sb.append(StringUtils.replace(pkgName, ".", File.separator));
		sb.append(File.separator);
		String realPath = sb.toString();
		File f = new File(realPath);
		if ((f.isDirectory()) && (!f.exists())) {
			f.mkdirs();
		}
		return realPath;
	}
}
