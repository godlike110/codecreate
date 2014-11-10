package com.codegen.jade;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.codegen.db.model.TableInfo;
import com.codegen.utils.NamingUtils;

public class JadeCodegenService {

    private String modelPkg;
    private String daoPkg;
    private String srcFolder;
    
    private String baseObjectClass;
    private String pkClass;
    //private String genericDaoClass;
    
    public JadeCodegenService(String model,String dao,String src) { 
        this.modelPkg = model;
        this.daoPkg = dao;
        this.srcFolder = src;
    }
    
    public void setBaseObjectClass(String baseObjectClass) {
        this.baseObjectClass = baseObjectClass;
    }
    
    public void setPkClass(String pkClass) {
        this.pkClass = pkClass;
    }
    
//    public void setGenericDaoClass(String genericDaoClass) {
//        this.genericDaoClass = genericDaoClass;
//    }
    
    public void createFile(List<TableInfo> list, Log log) throws Exception {
        
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(Velocity.RESOURCE_LOADER, "class");
        ve.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.setProperty("input.encoding", "UTF-8");
        ve.setProperty("output.encoding", "UTF-8");

        ve.init();

        VelocityContext context = new VelocityContext();
        
        context.put("modelPkg", modelPkg);
        context.put("daoPkg", daoPkg);
        
        context.put("baseObjectClass", this.baseObjectClass);
        context.put("pkClass", this.pkClass);
        //context.put("genericDaoClass", this.genericDaoClass);
        
//        String genericDaoClassShort = genericDaoClass.substring(genericDaoClass.lastIndexOf(".")+1);
//        context.put("genericDaoClassShort", genericDaoClassShort);
        
        String prefixPath = srcFolder + File.separator;
        
        for (TableInfo table : list) { 
            
            context.put("table", table);
            
            log.info("processing table :" + table.getTableName());
            
            //String pascalName = NamingUtils.getPascalName(table.getTableName());
            String pascalName = NamingUtils.getTabletPascalName(table.getTableName());
            
            log.info("generate className: " + pascalName);
            
            String modelPath = getRealPath(prefixPath,modelPkg);
            String modelFileName = modelPath + pascalName + ".java";
            doMerge(modelFileName,ve,context,"template/jade/model.vm"); //jade
            
            log.info("generate: " + modelFileName);
            
            
            String daoPath = getRealPath(prefixPath,daoPkg);
            String daoFileName = daoPath + pascalName + "DAO.java";
            doMerge(daoFileName,ve,context,"template/jade/dao.vm");
            
            log.info("generate: " + daoFileName);
            
        }
    }
    
    private static void doMerge(String fileName , VelocityEngine ve , VelocityContext context,String template) throws Exception { 
        File modelFile = new File(fileName);
        PrintWriter pw = new PrintWriter(modelFile);
        Template t = ve.getTemplate(template);
        t.merge(context, pw);
        pw.close();        
    }
    
    private static String getRealPath(String srcPrefix , String pkgName) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(srcPrefix);
        sb.append(StringUtils.replace(pkgName, ".", File.separator));
        sb.append(File.separator);
        String realPath = sb.toString();
        File f = new File(realPath);
        if(f.isDirectory() && !f.exists()){
            f.mkdirs();
        }
        return realPath;
    }

}
