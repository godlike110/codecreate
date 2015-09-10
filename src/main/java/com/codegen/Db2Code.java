package com.codegen;

import com.codegen.db.model.TableInfo;
import com.codegen.db.service.DBInfoService;
import com.codegen.jade.JadeCodegenService;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "db2code")
public class Db2Code extends AbstractMojo {
	@Parameter(defaultValue = "com.mysql.jdbc.Driver")
	private String driverClass;
	@Parameter(required = true)
	private String dbUrl;
	@Parameter(defaultValue = "root")
	private String dbUser;
	@Parameter(defaultValue = "")
	private String dbPassword;
	@Parameter(defaultValue = "%")
	private String tbNamePattern;
	@Parameter(required = true)
	private String modelPkg;
	@Parameter(required = true)
	private String daoPkg;
	@Parameter(required = true)
	private String wikiPkg;
	@Parameter(required = true)
	private int genType;
	@Parameter(required = true, defaultValue = "${project.build.sourceDirectory}")
	private String srcFolder;
	@Parameter(required = true)
	private String baseObjectClass;
	@Parameter(required = true)
	private String pkClass;
	@Parameter(defaultValue = "dbName")
	private String dataBaseName;
	private static final int GEN_TYPE_JADE = 1;
	private static final int GEN_TYPE_WIKIDB = 2;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			DBInfoService dbService = new DBInfoService("mysql.properties");
			List<TableInfo> list = dbService.getTableList(this.driverClass, this.dbUrl,
					this.dbUser, this.dbPassword, this.tbNamePattern);

			JadeCodegenService codegenService = new JadeCodegenService(this.modelPkg,
					this.daoPkg, this.srcFolder, this.wikiPkg, this.dataBaseName);
			codegenService.setBaseObjectClass(this.baseObjectClass);
			codegenService.setPkClass(this.pkClass);
			switch (this.genType) {
			case 1:
				codegenService.createJadeFile(list, getLog());
				break;
			case 2:
				codegenService.createWikiDBFile(list, getLog());
				break;
			default:
				throw new MojoExecutionException("没有此种类型！！！！");
			}
		} catch (Exception e) {
			throw new MojoExecutionException("", e);
		}
	}
}
