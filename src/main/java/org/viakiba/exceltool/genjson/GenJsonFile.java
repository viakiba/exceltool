package org.viakiba.exceltool.genjson;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.viakiba.exceltool.ReadExcelUtil;
import org.viakiba.exceltool.genjson.model.SheetBean;
import org.viakiba.exceltool.genjson.model.TableBean;
import freemarker.template.*;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

/**
 * @description json生成工具
 */
public class GenJsonFile {
    private final static Logger logger = LoggerFactory.getLogger(GenJsonFile.class);
    public static String configPath ;
    public static String templateParentPath ;
    public static String jsonOutPutParentPath ;
    public static String excelParentPath ;
    public static String templateName ;
    public static String idDefaultIndex ;
    public static int typeRowNum ;
    public static int fieldNameRowNum ;
    public static int descRowNum ;
    public static int firstDataRowNum ;
    public static boolean removeFirstColumn ;
    public static boolean unix ;
    public static boolean isRemoveFirstColumn ;
    public static String arrayIdentifier ;

    public static List<SheetBean> sheetBeans = new ArrayList<>();

    /**
     * @param args
     * @throws IOException
     * @throws TemplateException
     */
    public static void main(String[] args) throws IOException, TemplateException {
        logger.info("0. 读取 vm 启动参数配置 =================");
        configPath = System.getProperty("gen.json.configPath");
        if(StringUtils.isEmpty(configPath)){
            throw new RuntimeException("请使用vm参数 例子：-Dgen.json.configPath=/Users/dd/Desktop/exceltool/json/config/excel2json.xml 设置excel2json.xml配置路径");
        }
        logger.info("configPath "+configPath);
        templateParentPath = System.getProperty("gen.json.templateParentPath");
        if(StringUtils.isEmpty(templateParentPath)){
            throw new RuntimeException("请使用vm参数 例子：-Dgen.json.templateParentPath=/Users/dd/Desktop/exceltool/json/config/ 设置模板所在父路径");
        }
        logger.info("templateParentPath "+templateParentPath);
        jsonOutPutParentPath = System.getProperty("gen.json.jsonOutPutParentPath");
        if(StringUtils.isEmpty(jsonOutPutParentPath)){
            throw new RuntimeException("请使用vm参数 例子：-Dgen.json.jsonOutPutParentPath=/Users/dd/Desktop/exceltool/jsonexport/ 设置json文件输出父路径");
        }
        logger.info("jsonOutPutParentPath "+jsonOutPutParentPath);
        excelParentPath = System.getProperty("gen.json.excelParentPath");
        if(StringUtils.isEmpty(excelParentPath)){
            throw new RuntimeException("请使用vm参数 例子：-Dgen.json.excelParentPath=/Users/dd/Desktop/exceltool/excelresource/ 设置项excel所在父路径");
        }
        logger.info("excelParentPath "+excelParentPath);
        unix = Boolean.parseBoolean(System.getProperty("gen.json.unix"));
        if(StringUtils.isEmpty(excelParentPath)){
            throw new RuntimeException("请使用vm参数 例子：-Dgen.json.unix=false 设置项excel所在父路径");
        }
        logger.info("1. 读取 指定的 configPath : " + configPath +  "    unix "+unix);
        initConfig();
        for(SheetBean sheetBean:sheetBeans) {
            logger.info("3. 当前读取sheet : "+JSONObject.toJSONString(sheetBean));
            readToFile(sheetBean);
        }
    }

    private static void initConfig() throws MalformedURLException {
        Element root = ReadExcelUtil.getRootElemet(new File(configPath).toURL());
        templateName = root.getAttributeValue("templateName");
        idDefaultIndex = root.getAttributeValue("idDefaultIndex");
        typeRowNum = Integer.parseInt(root.getAttributeValue("typeRowNum"));
        fieldNameRowNum = Integer.parseInt(root.getAttributeValue("fieldNameRowNum"));
        descRowNum = Integer.parseInt(root.getAttributeValue("descRowNum"));
        firstDataRowNum = Integer.parseInt(root.getAttributeValue("firstDataRowNum"));
        firstDataRowNum = Integer.parseInt(root.getAttributeValue("firstDataRowNum"));
        removeFirstColumn = Boolean.parseBoolean(root.getAttributeValue("removeFirstColumn"));
        isRemoveFirstColumn = Boolean.parseBoolean(root.getAttributeValue("isRemoveFirstColumn"));
        arrayIdentifier = root.getAttributeValue("arrayIdentifier");
        List<Element> children = root.getChildren();
        for(Element element:children){
            String fileName = element.getAttributeValue("name");
            List<Element> sheetElements = element.getChildren();
            for (int sheetIndex = 0; sheetIndex < sheetElements.size(); sheetIndex++) {
                Element sheet = sheetElements.get(sheetIndex);
                String jsonFileName = sheet.getAttributeValue("jsonFileName");
                String idName = sheet.getAttributeValue("idName");
                SheetBean sheetBean = new SheetBean();
                sheetBean.setSheetNo(sheetIndex);
                sheetBean.setFileName(fileName);
                sheetBean.setIdName(idName);
                sheetBean.setjsonFileName(jsonFileName);
                logger.info("2. 需要读取的表配置 : "+JSON.toJSONString(sheetBean));
                sheetBeans.add(sheetBean);
            }
        }
    }

    /**
     * 方形的第五第六空行 要加上说明 （预留行1，预留行2） 占位行
     * @param sheetBean
     * @throws IOException
     * @throws TemplateException
     */
    private static void readToFile(SheetBean sheetBean) throws IOException, TemplateException {
        logger.info("=================================================================================== ");
        if(unix){
            String fileName = sheetBean.getjsonFileName();
            fileName.replace("\\",File.separator);
            sheetBean.setjsonFileName(fileName);
        }
        String filePath = excelParentPath + sheetBean.getFileName();
        logger.info("当前文件  "+filePath +" sheetNo "+sheetBean.getSheetNo());
        ExcelReaderBuilder read = EasyExcel.read(filePath);
        ExcelReaderSheetBuilder excelReaderSheetBuilder = read.sheet(sheetBean.getSheetNo()).headRowNumber(0);
        //获得sheet中 每一行为一个map的所有数据
        List<LinkedHashMap> originalData = excelReaderSheetBuilder.doReadSync();
        //判断移除第一列
        removeFirstCloumn(originalData);
        //获取对应行的  字段名称
        LinkedHashMap header = originalData.get(fieldNameRowNum);
        //获取对应行的  描述
        LinkedHashMap desc = originalData.get(descRowNum);
        //获取对应行的  初步类型
        LinkedHashMap type = originalData.get(typeRowNum);
        isRemoveFirstColumn(originalData, type);
        logger.info("描述 "+JSONObject.toJSONString(desc));
        logger.info("字段名称 "+JSONObject.toJSONString(header));
        logger.info("类型 "+JSONObject.toJSONString(type));
        //设置id索引
        setIdIndex(sheetBean, header);
        logger.info("id索引 "+ sheetBean.getIdNameIndex());
        //移除非数据行
        for(int i=0;i<firstDataRowNum;i++){
            originalData.remove(0);
        }
        Map<String, Object> root = new HashMap<>();
        //把 key 换成 字符串的 freemarker的map不能读int型的key
        List<LinkedHashMap> dataResult = new ArrayList<>();
        setNull2String(originalData, dataResult);
        //判断 忽略字段
        removeIgnoreField(header, desc, type, dataResult);
        //判断数组 与 字符串 字符串加冒号 数组要进行分割
        splitArrayAndSetMarks(type, dataResult);
        setKey2Header(dataResult,header);
        List<String> resultJsonStr = new ArrayList<>();
        Iterator<LinkedHashMap> iterator = dataResult.stream().iterator();
        while (iterator.hasNext()){
            LinkedHashMap next = iterator.next();
            String s = JSON.toJSONString(next);
            resultJsonStr.add(s);
        }
        //设置内容
        TableBean tableBean = new TableBean();
        tableBean.setData(originalData);
        tableBean.setDesc(desc);
        tableBean.setType(type);
        tableBean.setHeader(header);
        tableBean.setIdIndex(sheetBean.getIdNameIndex());
        Template template = getConfiguration(templateParentPath);
        if(unix){
            String jsonFileName = sheetBean.getjsonFileName();
            jsonFileName.replace("\\",File.separator);
            sheetBean.setjsonFileName(jsonFileName);
        }
        root.put("json", resultJsonStr);
        try {
            //输出文件
            File fileName = new File(jsonOutPutParentPath  +sheetBean.getjsonFileName() +".json");
            OutputStream outputStream = new FileOutputStream(fileName);
            Writer out = new OutputStreamWriter(outputStream, "UTF-8");
            template.process(root, out);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            logger.info("导表异常 " +sheetBean.getFileName() + "   " + sheetBean.getSheetNo() +">>>>>>",e);
            throw e;
        }
        logger.info("=================================================================================== "+dataResult.size());
    }

    private static void setKey2Header(List<LinkedHashMap> dataResult, LinkedHashMap header) {
        Iterator<LinkedHashMap> iterator = dataResult.iterator();
        while (iterator.hasNext()){
            LinkedHashMap next = iterator.next();
            Iterator iteratorKey = header.keySet().iterator();
            while (iteratorKey.hasNext()){
                int key = (int)iteratorKey.next();
                String fieldName = (String) header.get(key);
                if(next.containsKey(String.valueOf(key))) {
                    next.put(fieldName, next.get(String.valueOf(key)));
                    next.remove(String.valueOf(key));
                }
            }
        }
    }

    private static void splitArrayAndSetMarks(LinkedHashMap type, List<LinkedHashMap> dataResult) {
        Iterator iterator1 = type.keySet().iterator();
        while (iterator1.hasNext()){
            Object next = iterator1.next();
            String s = String.valueOf(type.get(next));
            String s1 = s.toLowerCase();
            int splitSize = getSplitSize(s1);
            if(splitSize == 0){
                addMaoHao(next, dataResult,s1.contains("string"),s1);
                continue;
            }
            if(splitSize == 1){
                splitOne(next, dataResult,s1);
                continue;
            }
            if(splitSize == 2){
                splitTwo(next, dataResult,s1);
                continue;
            }
        }
    }

    private static void removeIgnoreField(LinkedHashMap header, LinkedHashMap desc, LinkedHashMap type, List<LinkedHashMap> dataResult) {
        Iterator iterator2 = header.keySet().iterator();
        while (iterator2.hasNext()){
            Object key = iterator2.next();
            Object value = header.get(key);
            if(String.valueOf(value).contains("#")){
                type.remove(key);
                desc.remove(key);
                Iterator<LinkedHashMap> iterator1 = dataResult.iterator();
                while (iterator1.hasNext()){
                    LinkedHashMap next = iterator1.next();
                    next.remove(String.valueOf(key));
                }
                iterator2.remove();
            }
        }
    }

    private static void setNull2String(List<LinkedHashMap> originalData, List<LinkedHashMap> dataResult) {
        Iterator<LinkedHashMap> iterator = originalData.iterator();
        while (iterator.hasNext()){
            LinkedHashMap<Object, Object> objectObjectLinkedHashMap = new LinkedHashMap<>();
            LinkedHashMap next = iterator.next();
            Set set = next.keySet();
            Iterator iterator1 = set.iterator();
            while (iterator1.hasNext()){
                Object next1 = iterator1.next();
                objectObjectLinkedHashMap.put(String.valueOf(next1),next.get(next1));
                iterator1.remove();
            }
            dataResult.add(objectObjectLinkedHashMap);
        }
    }

    private static void setIdIndex(SheetBean sheetBean, LinkedHashMap header) {
        if(sheetBean.getIdName() == null || sheetBean.getIdName().trim().isEmpty()){
            sheetBean.setIdNameIndex(idDefaultIndex);
        }else{
            Iterator iterator = header.keySet().iterator();
            while (iterator.hasNext()){
                Object next = iterator.next();
                if(String.valueOf(header.get(next)).equals(sheetBean.getIdName())){
                    sheetBean.setIdNameIndex(String.valueOf(next));
                }
            }
        }
    }

    private static void isRemoveFirstColumn(List<LinkedHashMap> originalData, LinkedHashMap type) {
        if(isRemoveFirstColumn){
            //兼容 方形 （主公别消我）的表结构  两层类型 定义 cls字段类型   cls字段集合类型
            LinkedHashMap tempType= originalData.get(typeRowNum+1); //补充类型定义 集合
            Iterator iterator = tempType.keySet().iterator();
            while (iterator.hasNext()){
                Object next = iterator.next();
                Object o = tempType.get(next); //对应索引的补充定义结果 存在的话 覆盖第一层对应索引结果
                if(o!=null&&String.valueOf(o).trim().length()>0){
                    type.put(next,String.valueOf(o).trim());
                }
            }
        }
    }

    private static void removeFirstCloumn(List<LinkedHashMap> originalData) {
        if(removeFirstColumn){
            logger.info("移除第一列");
            Iterator<LinkedHashMap> iterator = originalData.iterator();
            while (iterator.hasNext()){
                LinkedHashMap next = iterator.next();
                next.remove(0);
            }
        }
    }

    private static void addMaoHao(Object next, List<LinkedHashMap> data1,boolean isStr,String type) {
        Iterator<LinkedHashMap> iterator = data1.iterator();
        while (iterator.hasNext()){
            LinkedHashMap next1 = iterator.next();
            Object o = next1.get(String.valueOf(next));
            if(o == null){
                if(isStr) {
                    next1.put(String.valueOf(next), "");
                }else{
                    next1.put(String.valueOf(next), 0);
                }
            }else {
                if(isStr) {
                    next1.put(String.valueOf(next),o);
                }else{
                    if(type.contains("int")) {
                        next1.put(String.valueOf(next), Integer.parseInt(String.valueOf(o)));
                    }else{
                        next1.put(String.valueOf(next), Float.parseFloat(String.valueOf(o)));
                    }
                }

            }
        }
    }

    private static void splitOne(Object next, List<LinkedHashMap> data1,String type) {
        Iterator<LinkedHashMap> iterator = data1.iterator();
        while (iterator.hasNext()){
            LinkedHashMap next1 = iterator.next();
            Object o1 = next1.get(String.valueOf(next));
            if(o1==null){
                next1.remove(String.valueOf(next));
                continue;
            }
            String o = String.valueOf(o1);
            String[] split = o.split(ReadExcelUtil.oneDimensional);
            if(type.contains("string")) {
                next1.put(String.valueOf(next), split);
            }else if(type.contains("int")){
                int[] ints = new int[split.length];
                for (int i = 0; i <split.length; i++) {
                    ints[i] = Integer.parseInt(split[i]);
                }
                next1.put(String.valueOf(next), ints);
            } else{
                float[] ints = new float[split.length];
                for (int i = 0; i <split.length; i++) {
                    ints[i] = Float.parseFloat(split[i]);
                }
                next1.put(String.valueOf(next), ints);
            }
        }
    }

    private static void splitTwo(Object next, List<LinkedHashMap> data1,String type) {
        Iterator<LinkedHashMap> iterator = data1.iterator();
        while (iterator.hasNext()){
            LinkedHashMap next1 = iterator.next();
            Object o1 = next1.get(String.valueOf(next));
            if(o1==null){
                next1.remove(String.valueOf(next));
                continue;
            }
            String o = String.valueOf(o1);
            String[] split = o.split(ReadExcelUtil.twoDimensional);
            ArrayList<List<String>> objects = new ArrayList<>();
            int maxY = 0;
            for(String s : split){
                String[] split1 = s.split(ReadExcelUtil.oneDimensional);
                if(split1.length>maxY){
                    maxY = split1.length;
                }
                objects.add(Arrays.asList(split1));
            }

            if(type.contains("string")) {
                next1.put(String.valueOf(next), objects);
            }else if(type.contains("int")){
                List<List<Integer>> result = new ArrayList<>();
                for (int i = 0; i < objects.size(); i++) {
                    ArrayList<Integer> objects1 = new ArrayList<>();
                    String[] split1 = split[i].split(",");
                    for (int j = 0; j < split1.length; j++) {
                        List<String> o2 = objects.get(i);
                        objects1.add(Integer.parseInt(o2.get(j)));
                    }
                    result.add(objects1);
                }
                next1.put(String.valueOf(next), result);
            }else{
                List<List<Float>> result = new ArrayList<>();
                for (int i = 0; i < objects.size(); i++) {
                    ArrayList<Float> objects1 = new ArrayList<>();
                    String[] split1 = split[i].split(",");
                    for (int j = 0; j < split1.length; j++) {
                        List<String> o2 = objects.get(j);
                        objects1.add(Float.parseFloat(o2.get(j)));
                    }
                    result.add(objects1);
                }
                next1.put(String.valueOf(next), result);
            }
        }
    }

    public static int getSplitSize(String s){
        return getSplitSize(s,arrayIdentifier);
    }

    public static int getSplitSize(String s,String arrayIdentifier){
        char[] chars = s.toCharArray();
        int i = 0;
        for(char c : chars){
            if(c == arrayIdentifier.toCharArray()[0]){
                i++;
            }
        }
        return i;
    }

    public static Template getConfiguration(String templatesPath) throws IOException {
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File(templatesPath));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg.getTemplate(templateName);
    }
}
