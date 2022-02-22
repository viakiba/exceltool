package org.viakiba.exceltool;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.listener.ModelBuildEventListener;
import org.viakiba.exceltool.annotate.UnionAnnotate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import javax.validation.ConstraintViolation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SingleExcelDataListener extends ModelBuildEventListener {
    public static Map tempMap = new ConcurrentHashMap<>();

    @Override
    public void invoke(Map<Integer, CellData> cellDataMap, AnalysisContext context) {
        try {
            AfterReadDo afterReadDo = getAfterReadDo(cellDataMap, context);
            if (afterReadDo == null) return;
            tempMap.put(afterReadDo.getIdKey(),afterReadDo);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private AfterReadDo getAfterReadDo(Map<Integer, CellData> cellDataMap, AnalysisContext context) {
        Integer rowIndex = context.readRowHolder().getRowIndex();
        if(ReadExcelUtil.skip != null && ExcelDataListener.isSkip(ReadExcelUtil.skip,rowIndex)){
            return null;
        }
        super.invoke(cellDataMap, context);
        Object result = context.readRowHolder().getCurrentRowAnalysisResult();
        AfterReadDo afterReadDo = (AfterReadDo)result;
        return afterReadDo;
    }

    @SneakyThrows
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) throws RuntimeException{
        Class clazz = context.readWorkbookHolder().getClazz();
        log.info(clazz+"single read over "+context.readWorkbookHolder().getFile().getName());
        Iterator<AfterReadDo> iterator = tempMap.values().iterator();
        while (iterator.hasNext()){
            AfterReadDo next = iterator.next();
            next.afterAllSheetReadDo();
            Set<ConstraintViolation<AfterReadDo>> validate = ExcelDataListener.validator.validate(next);
            for (ConstraintViolation<AfterReadDo> constraintViolation : validate) {
                log.error(constraintViolation.getMessage());
            }
            if(validate.size()>0){
                throw new RuntimeException("表校验出现错误配置");
            }
        }
        log.info("fileName "+context.readWorkbookHolder().getReadWorkbook().getFile().getName()+" sheetName  "+context.getCurrentSheet().getSheetName()+"single读取完成");
        UnionAnnotate annotation = (UnionAnnotate) clazz.getAnnotation(UnionAnnotate.class);
        if(annotation==null){
            ExcelDataService.dataMap.put(clazz,tempMap);
            return;
        }
        String declaredAnnotationName = annotation.type();
        Map<Object,Map<Object,Object>> class2TypeMap = new ConcurrentHashMap<>();

        Iterator iterator1 = tempMap.values().iterator();
        while (iterator1.hasNext()) {
            AfterReadDo data = (AfterReadDo)iterator1.next();
            Field field = data.getClass().getDeclaredField(declaredAnnotationName);
            field.setAccessible(true);//允许访问私有字段
            Object typeValue = field.get(data);
            Map<Object, Object> type2Map = class2TypeMap.get(typeValue);
            if (type2Map == null) {
                type2Map = new ConcurrentHashMap<>();
                class2TypeMap.put(typeValue, type2Map);
            }
            type2Map.put(data.getIdKey(), data);
        }
        ExcelDataService.dataMap.put(clazz,tempMap);
        ExcelDataService.unionDataMap.put(clazz,class2TypeMap);
        tempMap.clear();
    }
}
