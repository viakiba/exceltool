package org.viakiba.exceltool;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.util.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.*;

public class ReadExcelUtil {
    private final static Logger logger = LoggerFactory.getLogger(ReadExcelUtil.class);
    private static HashMap<String, List<String>> configClassToSheet = new HashMap<>();

    public final static List<Integer> skip = new ArrayList<>();
    public static Integer firstColsData = 0;
    public static void addSkipRowNumAll(List<Integer> skipList){
        skip.clear();
        skip.addAll(skipList);
    }

    /**
     * 一维分割符 使用 , 分割
     */
    public static String oneDimensional = ",";
    /**
     * 二维分割符 使用#分割成一维一维使用分割成具体单元
     */
    public static String twoDimensional = "#";
    /**
     * 忽略字段 以 # 开头
     */
    public static String ignoreField = "#";

    public static Element getRootElemet(URL xmlPath) {
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(xmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc.getRootElement();
    }

    public static void readExcelToMap(String excelParentPath,int headRowNumber) throws Exception{
        Set<Map.Entry<String, List<String>>> entries = configClassToSheet.entrySet();
        for(Map.Entry<String, List<String>> entry : entries){
            String excelName = entry.getKey();
            List<String> sheets = entry.getValue();
            for(int sheetIndex=0;sheetIndex<sheets.size();sheetIndex++){
                String fileName = excelParentPath + excelName;
                ExcelDataListener demoDataListener = new ExcelDataListener();
                String className = sheets.get(sheetIndex);
                if(StringUtils.isEmpty(className)){
                    logger.info("excelName="+excelName + "sheetIndex="+sheetIndex +"不读取此sheetIndex数据");
                    continue;
                }
                logger.info("excelName="+excelName + "sheetIndex="+sheetIndex +"读取此sheetIndex数据");
                ExcelReaderBuilder read = EasyExcel.read(fileName, Class.forName(className), demoDataListener);
                read.useDefaultListener(false);
                ExcelReaderSheetBuilder excelReaderSheetBuilder = read.sheet(sheetIndex).headRowNumber(headRowNumber);
                excelReaderSheetBuilder.doReadSync();
            }
        }
    }

    public static <T> List<T> readReloadSheet(int headRowNumber, int sheetIndex, String fileName,String fileParentPath) throws ClassNotFoundException {
        ExcelReaderBuilder read = EasyExcel.read(fileParentPath+fileName,Class.forName(findClassName(fileName,sheetIndex)),new SingleExcelDataListener());
        read.useDefaultListener(false);
        ExcelReaderSheetBuilder excelReaderSheetBuilder = read.sheet(sheetIndex).headRowNumber(headRowNumber);
        return excelReaderSheetBuilder.doReadSync();
    }

    public static void initExcel(String absoluteConfigPath,String excelParentPath,int headRowNumber,List<Integer> skipList) throws Exception {
        addSkipRowNumAll(skipList);
        Element root = getRootElemet(new File(absoluteConfigPath).toURL());
        firstColsData = Integer.parseInt(root.getAttributeValue("firstColsData"));
        List<Element> children = root.getChildren();
        for(Element element:children){
            String fileName = element.getAttributeValue("name");
            List<Element> sheetElements = element.getChildren();
            ArrayList<String> classNameStr = new ArrayList<>();
            for (int sheetIndex = 0; sheetIndex < sheetElements.size(); sheetIndex++) {
                Element sheet = sheetElements.get(sheetIndex);
                String className = sheet.getAttributeValue("class");
                if(className == null){
                    classNameStr.add("");
                }else{
                    logger.info("fileName="+fileName+" className="+className +" sheetIndex="+sheetIndex);
                    classNameStr.add(className);
                }
            }
            configClassToSheet.put(fileName,classNameStr);
        }
        readExcelToMap(excelParentPath,headRowNumber);
    }

    public static String findClassName(String fileName,int sheetNo){
        return configClassToSheet.get(fileName).get(sheetNo);
    }

}
