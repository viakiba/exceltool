package org.viakiba.exceltool.genjava;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import org.viakiba.exceltool.ReadExcelUtil;
import org.viakiba.exceltool.bean.ClassBean;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Element;
import org.viakiba.exceltool.genlua.GenLuaFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.viakiba.exceltool.genlua.GenLuaFile.getSplitSize;

@Slf4j
public class GenJavaFile {
    public static String projectRootPath;
    public static String templatesName;
    public static String templatesPath;
    public static String excelRootPath;
    public static String prototypeConfig;
    public static String genBeanPath;
    public static String packageName;
    public static String afterReadDoPackage;
    public static String convertPackage;

    public static int headNum = 1;
    public static int dataDesc = 1;
    public static int dataType = 2;
    public static String genOver = "0";//此次需要生成 不为此致就不生成

    private static List<ClassBean> classBeanList =  new ArrayList<>();
    public static void main(String[] args) throws Exception {
        initConfig();
        initExcel(classBeanList);
        for(ClassBean classBean : classBeanList){
            genSingleCodeExcel(classBean);
        }
    }
    private static void initConfig() throws MalformedURLException {
        String property = System.getProperty("gen.config.configPath");
        if(StringUtils.isEmpty(property)){
            throw new RuntimeException("请使用vm参数 -Dgen.config.configPath=/Users/dd/Documents/excelTool/src/main/resources/excel2java.xml 设置生成代码的配置路径,配置样例在源代码 resources/config 里面");
        }
        Element root = ReadExcelUtil.getRootElemet(new File(property).toURL());
        prototypeConfig = property;
        templatesName = root.getAttributeValue("templatesName");

        templatesPath = root.getAttributeValue("templatesPath");
        excelRootPath = root.getAttributeValue("excelRootPath");
        genBeanPath = root.getAttributeValue("genBeanPath");

        packageName = root.getAttributeValue("packageName");
        afterReadDoPackage = root.getAttributeValue("afterReadDoPackage");
        convertPackage = root.getAttributeValue("convertPackage");
    }

    public static void initExcel(List<ClassBean> classBeanList) throws MalformedURLException {
        Element root = ReadExcelUtil.getRootElemet(new File(prototypeConfig).toURL());
        List<Element> children = root.getChildren();
        for(Element element:children){
            String fileName = element.getAttributeValue("name");
            List<Element> sheetElements = element.getChildren();
            for (int sheetIndex = 0; sheetIndex < sheetElements.size(); sheetIndex++) {
                Element sheet = sheetElements.get(sheetIndex);
                String className = sheet.getAttributeValue("class");
                String isOver = sheet.getAttributeValue("genOver");
                if(!StringUtils.isEmpty(className)){
                    if(StringUtils.isEmpty(genOver)){
                        throw new RuntimeException("请设置 sheet标签的 genOver 属性");
                    }
                    if(!genOver.equals(isOver.trim())){
                        continue;
                    }
                    //read excel convert to classBean
                    log.info("fileName="+fileName+" className="+className + " classNameAbs="+className +" sheetIndex="+sheetIndex);
                    String classNameAbs = className.substring(className.lastIndexOf(".")+1);
                    ClassBean classBean = new ClassBean();
                    classBean.setClassName(classNameAbs);
                    classBean.setSheetNo(sheetIndex);
                    classBean.setFileName(fileName);
                    initBeanDefine(classBean);
                    classBeanList.add(classBean);
                }
            }
        }
    }

    private static void initBeanDefine(ClassBean classBean) {
        ExcelReaderBuilder read = EasyExcel.read(
                excelRootPath + classBean.getFileName(),
                new HeadReadListener(classBean));
        read.useDefaultListener(false);
        ExcelReaderSheetBuilder excelReaderSheetBuilder = read.sheet(classBean.getSheetNo()).headRowNumber(headNum);
        excelReaderSheetBuilder.doRead();
    }


    public static void genSingleCodeExcel(ClassBean classBean) throws Exception{
        Template temp = getConfiguration(templatesPath);
        File fileName = new File(genBeanPath + classBean.getClassName() + ".java");
        log.info("Gen javaFilePath \n"+ fileName.getAbsolutePath());
        log.info("Gen java field info \n"+ JSONObject.toJSONString(classBean.getFiled()));
        OutputStream outputStream = new FileOutputStream(fileName);
        Writer out = new OutputStreamWriter(outputStream, "UTF-8");
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("supplier", classBean);
        root.put("packageName", packageName);
        root.put("afterReadDoPackage", afterReadDoPackage);
        root.put("convertPackage", convertPackage);
        temp.process(root, out);
        outputStream.flush();
        outputStream.close();
    }

    public static Template getConfiguration(String templatesPath) throws IOException {
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File(templatesPath));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg.getTemplate(templatesName);
    }

    public static String getCellData2String(CellData cellData){
        switch (cellData.getType()){
            case STRING:
                return cellData.getStringValue();
            case NUMBER:
                return cellData.getNumberValue().toString();
            case BOOLEAN:
                return cellData.getBooleanValue()==true?"1":"0";
            default:
                throw new RuntimeException("类型无法处理需要check");
        }
    }

    public static boolean isChinese(String str) throws UnsupportedEncodingException {
        int len = str.length();
        for(int i = 0;i < len;i ++) {
            String temp = URLEncoder.encode(str.charAt(i) + "", "utf-8");
            if(temp.equals(str.charAt(i) + ""))
                continue;
            String[] codes = temp.split("%");
            //判断是中文还是字符(下面判断不精确，部分字符没有包括)
            for(String code:codes)
            {
                if(code.compareTo("40") > 0)
                    return true;
            }
        }
        return false;
    }


    public static boolean oneData(String type){
        int splitSize = GenLuaFile.getSplitSize(type, "[");
        if(splitSize == 1){
            return true;
        }
        splitSize = GenLuaFile.getSplitSize(type, "<");
        if(splitSize == 1){
            return true;
        }
        return false;
    }

    public static boolean twoData(String type){
        int splitSize = GenLuaFile.getSplitSize(type, "[");
        if(splitSize == 2){
            return true;
        }
        splitSize = GenLuaFile.getSplitSize(type, "<");
        if(splitSize == 2){
            return true;
        }
        return false;
    }
}


