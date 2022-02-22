package org.viakiba.exceltool.genlua;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.viakiba.exceltool.ReadExcelUtil;
import org.viakiba.exceltool.genlua.luamodel.SheetBean;
import org.viakiba.exceltool.genlua.luamodel.TableBean;
import freemarker.template.*;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

import static org.viakiba.exceltool.ReadExcelUtil.oneDimensional;
import static org.viakiba.exceltool.ReadExcelUtil.twoDimensional;

/**
 * @description  lua生成工具
 */
public class GenLuaFile {
    private final static Logger logger = LoggerFactory.getLogger(GenLuaFile.class);
    public static String configPath ;
    public static String templateParentPath ;
    public static String luaOutPutParentPath ;
    public static String excelParentPath ;
    public static String templateName ;
    public static String idDefaultIndex ;
    public static int typeRowNum ;
    public static int fieldNameRowNum ;
    public static int descRowNum ;
    public static int firstDataRowNum ;
    public static boolean removeFirstColumn ;
    public static boolean unix ;
    public static boolean isSquare ;
    public static String arrayIdentifier ;

    public static List<SheetBean> sheetBeans = new ArrayList<>();

    /**
     * @description  lua生成工具
     */
    public static void main(String[] args) throws IOException, TemplateException {
        logger.info("0. 读取 vm 启动参数配置 =================");
        configPath = System.getProperty("gen.lua.configPath");
        if(StringUtils.isEmpty(configPath)){
            throw new RuntimeException("请使用vm参数 -Dgen.lua.configPath=D:\\Code\\common_excel_tool\\src\\main\\resources\\excel2lua.xml 设置excel2lua.xml配置路径");
        }
        logger.info("configPath "+configPath);
        templateParentPath = System.getProperty("gen.lua.templateParentPath");
        if(StringUtils.isEmpty(templateParentPath)){
            throw new RuntimeException("请使用vm参数 -Dgen.lua.templateParentPath=D:\\Code\\common_excel_tool\\src\\main\\resources 设置模板所在父路径");
        }
        logger.info("templateParentPath "+templateParentPath);
        luaOutPutParentPath = System.getProperty("gen.lua.luaOutPutParentPath");
        if(StringUtils.isEmpty(luaOutPutParentPath)){
            throw new RuntimeException("请使用vm参数 -Dgen.lua.luaOutPutParentPath=C:\\Users\\Admin\\Desktop\\ 设置lua文件输出父路径");
        }
        logger.info("luaOutPutParentPath "+luaOutPutParentPath);
        excelParentPath = System.getProperty("gen.lua.excelParentPath");
        if(StringUtils.isEmpty(excelParentPath)){
            throw new RuntimeException("请使用vm参数 -Dgen.lua.excelParentPath=D:\\Code\\common_excel_tool\\src\\main\\resources\\ 设置项excel所在父路径");
        }
        logger.info("excelParentPath "+excelParentPath);
        unix = Boolean.parseBoolean(System.getProperty("gen.lua.unix"));
        if(StringUtils.isEmpty(excelParentPath)){
            throw new RuntimeException("请使用vm参数 -Dgen.lua.unix=false 设置unix环境标识");
        }
        logger.info("unix "+unix);
        logger.info("1. 读取 指定的 configPath : " + configPath);
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
        isSquare = Boolean.parseBoolean(root.getAttributeValue("isSquare"));
        arrayIdentifier = root.getAttributeValue("arrayIdentifier");
        List<Element> children = root.getChildren();
        for(Element element:children){
            String fileName = element.getAttributeValue("name");
            List<Element> sheetElements = element.getChildren();
            for (int sheetIndex = 0; sheetIndex < sheetElements.size(); sheetIndex++) {
                Element sheet = sheetElements.get(sheetIndex);
                String luaFileName = sheet.getAttributeValue("luaFileName");
                if (StringUtils.isEmpty(luaFileName)){
                    continue;
                }
                String idName = sheet.getAttributeValue("idName");
                SheetBean sheetBean = new SheetBean();
                sheetBean.setSheetNo(sheetIndex);
                sheetBean.setFileName(fileName);
                sheetBean.setIdName(idName);
                sheetBean.setLuaFileName(luaFileName);
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
            String fileName = sheetBean.getLuaFileName();
            fileName.replace("\\",File.separator);
            sheetBean.setLuaFileName(fileName);
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
        patchSquareType(originalData, type);
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
        //把 null 换成 nil
        null2nil(dataResult);
        //设置内容
        TableBean tableBean = new TableBean();
        tableBean.setData(originalData);
        tableBean.setDesc(desc);
        tableBean.setType(type);
        tableBean.setHeader(header);
        tableBean.setIdIndex(sheetBean.getIdNameIndex());
        Template template = getConfiguration(templateParentPath);
        if(unix){
            String luaFileName = sheetBean.getLuaFileName();
            luaFileName.replace("\\",File.separator);
            sheetBean.setLuaFileName(luaFileName);
        }
        root.put("supplier", tableBean);
        root.put("data", dataResult);
        try {
            //输出文件
            File fileName = new File(luaOutPutParentPath  +sheetBean.getLuaFileName() +".lua");
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

    private static void null2nil(List<LinkedHashMap> dataResult) {
        Iterator<LinkedHashMap> iterator3 = dataResult.iterator();
        while (iterator3.hasNext()){
            LinkedHashMap next = iterator3.next();
            if (next.get(String.valueOf(idDefaultIndex)) == null) {
                iterator3.remove();
                continue;
            }
            Set set = next.keySet();
            Iterator iterator4 = set.iterator();
            while (iterator4.hasNext()) {
                Object next1 = iterator4.next();
                Object o = next.get(next1);
                if (o == null) {
                    next.put(next1, "nil");
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
                if(s1.equals("string")){
                    addMaoHao(next, dataResult);
                }
                continue;
            }
            if(splitSize == 1){
                splitOne(next, dataResult,s1.contains("string"));
            }
            if(splitSize == 2){
                splitTwo(next, dataResult,s1.contains("string"));
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

    private static void patchSquareType(List<LinkedHashMap> originalData, LinkedHashMap type) {
        if(isSquare){
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

    private static void addMaoHao(Object next, List<LinkedHashMap> data1) {
        Iterator<LinkedHashMap> iterator = data1.iterator();
        while (iterator.hasNext()){
            LinkedHashMap next1 = iterator.next();
            Object o = next1.get(String.valueOf(next));
            if(o == null){
                next1.put(String.valueOf(next),  "nil" );
            }else {
                next1.put(String.valueOf(next), "\"" + o + "\"");
            }
        }
    }

    private static void splitOne(Object next, List<LinkedHashMap> data1,boolean isStr) {
        Iterator<LinkedHashMap> iterator = data1.iterator();
        while (iterator.hasNext()){
            LinkedHashMap next1 = iterator.next();
            Object o1 = next1.get(String.valueOf(next));
            if(o1==null){
                next1.put(String.valueOf(next), "nil");
                continue;
            }
            String o = String.valueOf(o1);
            String[] split = o.split(oneDimensional);
            String replace = JSONObject.toJSONString(split).replace("[", "{").replace("]", "}");
            if(!isStr){
                replace = replace.replace("\"", "");
            }
            next1.put(String.valueOf(next), replace);
        }
    }

    private static void splitTwo(Object next, List<LinkedHashMap> data1,boolean isStr) {
        Iterator<LinkedHashMap> iterator = data1.iterator();
        while (iterator.hasNext()){
            LinkedHashMap next1 = iterator.next();
            Object o1 = next1.get(String.valueOf(next));
            if(o1==null){
                next1.put(String.valueOf(next), "nil");
                continue;
            }
            String o = String.valueOf(o1);
            String[] split = o.split(twoDimensional);
            ArrayList<Object> objects = new ArrayList<>();
            for(String s : split){
                String[] split1 = s.split(oneDimensional);
                objects.add(Arrays.asList(split1));
            }
            String replace = JSONObject.toJSONString(objects).replace("[", "{").replace("]", "}");
            if(!isStr){
                replace = replace.replace("\"", "");
            }
            next1.put(String.valueOf(next), replace);
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
