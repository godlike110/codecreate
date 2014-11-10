package com.codegen;

import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.codegen.db.model.TableInfo;
import com.codegen.db.service.DBInfoService;
import com.codegen.jade.JadeCodegenService;

@Mojo(name="db2code")
public class Db2Code extends AbstractMojo {
    
    @Parameter(defaultValue="com.mysql.jdbc.Driver")
    private String driverClass;
    
    @Parameter(required=true)
    private String dbUrl;
    
    @Parameter(defaultValue="root")
    private String dbUser;
    
    @Parameter(defaultValue="")
    private String dbPassword;
    
    @Parameter(defaultValue="%")
    private String tbNamePattern;

    @Parameter(required=true)
    private String modelPkg;
    
    @Parameter(required=true)
    private String daoPkg;
    
//    @Parameter(required=true)
//    private String xmlPkg;
    
    @Parameter(required=true,defaultValue="${project.build.sourceDirectory}")
    private String srcFolder;
    
    @Parameter(required=true)
    private String baseObjectClass;
    
    @Parameter(required=true)
    private String pkClass;
    
//    @Parameter(required=true)
//    private String genericDaoClass;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            DBInfoService dbService = new DBInfoService("mysql.properties");
            List<TableInfo> list = dbService.getTableList(driverClass,dbUrl,dbUser,dbPassword,tbNamePattern);
        //    IBatisCodegenService codegenService = new IBatisCodegenService(modelPkg, daoPkg, xmlPkg, srcFolder);
            
            JadeCodegenService codegenService = new JadeCodegenService(modelPkg, daoPkg, srcFolder);
            
            codegenService.setBaseObjectClass(baseObjectClass);
            codegenService.setPkClass(pkClass);
          //  codegenService.setGenericDaoClass(genericDaoClass);
            
            codegenService.createFile(list,getLog());
            
        } catch (Exception e) {
            throw new MojoExecutionException("", e);
        }
    }
}
