package com.codegen.jade;

import com.codegen.db.model.TableInfo;
import com.codegen.utils.NamingUtils;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class JadeCodegenService {
	private String modelPkg;
	private String daoPkg;
	private String srcFolder;
	private String wikiPkg;
	private String baseObjectClass;
	private String pkClass;
	private String dataBaseName;

	public JadeCodegenService(String model, String dao, String src, String wikiPkg,
			String dataBaseName) {
		this.modelPkg = model;
		this.daoPkg = dao;
		this.srcFolder = src;
		this.wikiPkg = wikiPkg;
		this.dataBaseName = dataBaseName;
	}

	public void setBaseObjectClass(String baseObjectClass) {
		this.baseObjectClass = baseObjectClass;
	}

	public void setPkClass(String pkClass) {
		this.pkClass = pkClass;
	}

	public void createJadeFile(List<TableInfo> list, Log log) throws Exception {
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

		String prefixPath = this.srcFolder + File.separator;
		for (TableInfo table : list) {
			context.put("table", table);

			log.info("processing table :" + table.getTableName());

			String pascalName = NamingUtils.getTabletPascalName(table.getTableName());

			log.info("generate className: " + pascalName);

			String modelPath = getRealPath(prefixPath, this.modelPkg);
			String modelFileName = modelPath + pascalName + ".java";
			doMerge(modelFileName, ve, context, "template/jade/model.vm");

			log.info("generate: " + modelFileName);

			String daoPath = getRealPath(prefixPath, this.daoPkg);
			String daoFileName = daoPath + pascalName + "DAO.java";
			doMerge(daoFileName, ve, context, "template/jade/dao.vm");

			log.info("generate: " + daoFileName);
		}
	}

	public void createWikiDBFile(List<TableInfo> list, Log log) throws Exception {
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty("resource.loader", "class");
		ve.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		ve.setProperty("input.encoding", "UTF-8");
		ve.setProperty("output.encoding", "UTF-8");

		ve.init();

		VelocityContext context = new VelocityContext();

		context.put("modelPkg", this.modelPkg);
		context.put("daoPkg", this.wikiPkg);

		context.put("baseObjectClass", this.baseObjectClass);
		context.put("pkClass", this.pkClass);

		String prefixPath = this.srcFolder + File.separator;

		List<TableInfo> tables = new ArrayList();
		for (TableInfo table : list) {
			if (table.getIsFlashTable()) {
				String[] arr = table.getRealTableName().split("_");
				if (arr[(arr.length - 1)].equals("1")) {
					tables.add(table);
				}
			} else {
				tables.add(table);
			}
		}
		context.put("tables", tables);
		String wikiPath = getRealPath(prefixPath, this.wikiPkg);
		String wikiFileName = wikiPath + this.dataBaseName + ".txt";
		doMerge(wikiFileName, ve, context, "template/jade/wikidb.vm");

		String docdbPath = getRealPath(prefixPath, this.wikiPkg);
		String docdbFileName = docdbPath + this.dataBaseName + ".xml";
		doMerge(docdbFileName, ve, context, "template/text/docdb.vm");
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
