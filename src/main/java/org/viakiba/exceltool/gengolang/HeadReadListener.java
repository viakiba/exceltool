package org.viakiba.exceltool.gengolang;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import org.viakiba.exceltool.bean.ClassBean;
import org.viakiba.exceltool.bean.FiledBean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static org.viakiba.exceltool.ReadExcelUtil.ignoreField;
import static org.viakiba.exceltool.genjava.GenJavaFile.*;

@Slf4j
public class HeadReadListener extends AnalysisEventListener {

    private ClassBean classBean;

    public HeadReadListener(ClassBean classBean) {
        this.classBean = classBean;
    }

    @Override
    public void invokeHead(Map headMap, AnalysisContext context) {
        Integer headRowNumber = context.readSheetHolder().getHeadRowNumber();
        Integer rowIndex = context.readRowHolder().getRowIndex();
        if(headRowNumber-1 != rowIndex){
            return;
        }
        log.info("filedName "+ JSONObject.toJSONString(headMap));
        Iterator iterator = headMap.keySet().iterator();
        List<FiledBean> filed = new ArrayList<>();
        while (iterator.hasNext()){
            Object next = iterator.next();
            Object value = headMap.get(next);
            FiledBean filedBean = new FiledBean();
            filedBean.setFiledName(((CellData) value).getStringValue());
            filed.add(filedBean);
        }
        classBean.setFiled(filed);
    }

    @Override
    public void invoke(Object data, AnalysisContext context) {
        Integer rowIndex = context.readRowHolder().getRowIndex();
        if(rowIndex == dataType){
            LinkedHashMap dataMap = (LinkedHashMap) data;
            Iterator iterator = dataMap.keySet().iterator();
            while (iterator.hasNext()){
                Object next = iterator.next();
                Object dataCell = dataMap.get(next);
                CellData cellData = (CellData) dataCell;
                FiledBean filedBean = classBean.getFiled().get((Integer) next);
                String stringValue = cellData.getStringValue();
                if(stringValue == null){
                    stringValue = "";
                }
                filedBean.setFiledType(stringValue);
            }

        }
        if(rowIndex == dataDesc) {
            LinkedHashMap dataMap = (LinkedHashMap) data;
            Iterator iterator = dataMap.keySet().iterator();
            while (iterator.hasNext()) {
                Object next = iterator.next();
                Object dataCell = dataMap.get(next);
                CellData cellData = (CellData) dataCell;
                FiledBean filedBean = classBean.getFiled().get((Integer) next);
                String stringValue = cellData.getStringValue();
                if(stringValue == null){
                    stringValue = "";
                }
                filedBean.setFiledDesc(stringValue);
            }
        }
    }

    @SneakyThrows
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        List<FiledBean> filed = classBean.getFiled();
        Iterator<FiledBean> iterator = filed.iterator();
        while (iterator.hasNext()){
            FiledBean filedBean = iterator.next();
            String filedName = filedBean.getFiledName();
            String filedDesc = filedBean.getFiledDesc();
            String filedType = filedBean.getFiledType().toLowerCase();
            if(filedName == null || filedName.contains(ignoreField) || isChinese(filedName)){
                iterator.remove();
                continue;
            }
            if(filedDesc == null){
                filedBean.setFiledDesc("");
            }
            if(StringUtils.isEmpty(filedType)){
                throw new RuntimeException("表 "+classBean.getFileName()+" sheetNo "+classBean.getSheetNo()+" 字段名称 "+filedName +"类型不存在");
            }
            String type = getType(filedType);
            if(type == null){
                throw new RuntimeException("表 "+classBean.getFileName()+" sheetNo "+classBean.getSheetNo()+" 字段名称 "+filedName +"映射类型不存在");
            }
            filedBean.setFiledType(type);
            //设置 excelProperty
            setExcelProperty(filedBean);
        }
    }

    private void setExcelProperty(FiledBean filedBean) {
        String type = filedBean.getFiledType().trim().toLowerCase();
        filedBean.setExcelProperty("@ExcelProperty(value = \""+filedBean.getFiledName()+"\")");
        if(oneData(type)){
            if(type.contains("integer")){
                filedBean.setExcelProperty("@ExcelProperty(value = \""+filedBean.getFiledName()+"\",converter = StringListIntegerConverter.class)");
            }
            if(type.contains("long")){
                filedBean.setExcelProperty("@ExcelProperty(value = \""+filedBean.getFiledName()+"\",converter = StringListLongConverter.class)");
            }
            if(type.contains("double")){
                filedBean.setExcelProperty("@ExcelProperty(value = \""+filedBean.getFiledName()+"\",converter = StringListDoubleConverter.class)");
            }
            if(type.contains("float")){
                filedBean.setExcelProperty("@ExcelProperty(value = \""+filedBean.getFiledName()+"\",converter = StringListFloatConverter.class)");
            }
            if(type.contains("string")){
                filedBean.setExcelProperty("@ExcelProperty(value = \""+filedBean.getFiledName()+"\",converter = StringListStringConverter.class)");
            }
        }
        if(twoData(type)){
            if(type.contains("integer")){
                filedBean.setExcelProperty("@ExcelProperty(value = \""+filedBean.getFiledName()+"\",converter = StringListIntegerConverter2.class)");
            }
            if(type.contains("long")){
                filedBean.setExcelProperty("@ExcelProperty(value = \""+filedBean.getFiledName()+"\",converter = StringListLongConverter2.class)");
            }
            if(type.contains("fouble")){
                filedBean.setExcelProperty("@ExcelProperty(value = \""+filedBean.getFiledName()+"\",converter = StringListDoubleConverter2.class)");
            }
            if(type.contains("float")){
                filedBean.setExcelProperty("@ExcelProperty(value = \""+filedBean.getFiledName()+"\",converter = StringListFloatConverter2.class)");
            }
            if(type.contains("string")){
                filedBean.setExcelProperty("@ExcelProperty(value = \""+filedBean.getFiledName()+"\",converter = StringListStringConverter2.class)");
            }
        }
    }

    public static String getType(String filedType){
        String trim = filedType.trim();
        switch (trim){
            case "byte":
                return filedType;
            case "short":
                return "int32";
            case "int":
                return "int32";
            case "long":
                return "int64";
            case "float":
                return "float32";
            case "double":
                return "float64";
            case "boolean":
                return "bool";
            case "bool":
                return "bool";
            case "char":
                return filedType;
            case "string":
                return "string";
            case "integer":
                return "int32";
            case "character":
                return "Character";
            case "int[]":
            case "integer[]":
                return "[]int32";
            case "long[]":
                return "[]int64";
            case "double[]":
                return "[]float64";
            case "float[]":
                return "[]float32";
            case "string[]":
                return "[]string";
            case "int[][]":
            case "integer[][]":
                return "[][]int32";
            case "long[][]":
                return "[][]int64";
            case "double[][]":
                return "[][]double64";
            case "float[][]":
                return "[][]float32";
            case "string[][]":
                return "[][]string";
            default:
                return filedType;
        }
    }
}
