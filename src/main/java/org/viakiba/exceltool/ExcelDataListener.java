package org.viakiba.exceltool;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.listener.ModelBuildEventListener;
import com.alibaba.fastjson.JSONObject;
import org.apache.bval.jsr.ApacheValidationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

import static org.viakiba.exceltool.ReadExcelUtil.firstColsData;

public class ExcelDataListener extends ModelBuildEventListener {
    private final static Logger logger = LoggerFactory.getLogger(ExcelDataListener.class);

    public final static Validator validator;

    static{
        ValidatorFactory avf = Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory();
        validator = avf.getValidator();
    }

    @Override
    public void invokeHead(Map<Integer, CellData> cellDataMap, AnalysisContext context) {
        Integer headRowNumber = context.readSheetHolder().getHeadRowNumber();
        Integer rowIndex = context.readRowHolder().getRowIndex();

        if(headRowNumber-1 == rowIndex){
            logger.info("head info"+JSONObject.toJSONString(cellDataMap));
            super.invokeHead(cellDataMap, context);
        }

    }

    @Override
    public void invoke(Map<Integer, CellData> cellDataMap, AnalysisContext context) {
        try {
            AfterReadDo afterReadDo = getAfterReadDo(cellDataMap, context);
            if (afterReadDo == null) return;
            if(!cellDataMap.containsKey(firstColsData)) return;
            if(afterReadDo.getIdKey() == null ){
                throw new RuntimeException(afterReadDo.getClass().getName()+" getIdKey 方法 is null");
            }
            ExcelDataService.put(afterReadDo.getClass(),afterReadDo.getIdKey(),afterReadDo);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }
    public AfterReadDo getAfterReadDo(Map<Integer, CellData> cellDataMap, AnalysisContext context) {
        Integer rowIndex = context.readRowHolder().getRowIndex();
        if(ReadExcelUtil.skip != null && isSkip(ReadExcelUtil.skip,rowIndex)){
            return null;
        }
        super.invoke(cellDataMap, context);
        Object result = context.readRowHolder().getCurrentRowAnalysisResult();
        AfterReadDo afterReadDo = (AfterReadDo)result;
        return afterReadDo;
    }

    public static boolean isSkip(List<Integer> skip,Integer rowIndex) {
        for(Integer i : skip){
            if(i==rowIndex){
                return true;
            }
        }
        return false;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) throws RuntimeException{
        Class clazz = context.readWorkbookHolder().getClazz();
        logger.info(clazz+" read over "+context.readWorkbookHolder().getFile().getName());
        Map<Long, AfterReadDo> byClass = ExcelDataService.getByClass(clazz);
        Iterator<AfterReadDo> iterator = byClass.values().iterator();
        while (iterator.hasNext()){
            AfterReadDo next = iterator.next();
            Set<ConstraintViolation<AfterReadDo>> validate = validator.validate(next);
            for (ConstraintViolation<AfterReadDo> constraintViolation : validate) {
                logger.error(constraintViolation.getMessage());
            }
            if(validate.size()>0){
                throw new RuntimeException("表校验出现错误配置");
            }
            next.afterAllSheetReadDo();
        }
        logger.info("fileName "+context.readWorkbookHolder().getReadWorkbook().getFile().getName()+" sheetName  "+context.getCurrentSheet().getSheetName()+"读取完成");
    }

}
