package org.viakiba.exceltool.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.StringUtils;
import org.viakiba.exceltool.ReadExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.viakiba.exceltool.genjava.GenJavaFile.getCellData2String;

public class StringListLongConverter  implements Converter<List> {
    private static Logger logger = LoggerFactory.getLogger(StringListLongConverter.class);
    @Override
    public Class supportJavaTypeKey() {
        return List.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public List convertToJavaData(CellData cellData, ExcelContentProperty contentProperty,
                                  GlobalConfiguration globalConfiguration) throws Exception {
        try {
            ArrayList<Long> longs = new ArrayList<>();
            String stringValue = getCellData2String(cellData);
            if(StringUtils.isEmpty(stringValue)){
                return longs;
            }
            String[] split = stringValue.split(ReadExcelUtil.oneDimensional);
            for(String s:split){
                if(StringUtils.isEmpty(s)){
                    throw new RuntimeException("符号值重复"+cellData.getStringValue()+
                            "字段名称"+contentProperty.getField().getName()+
                            "字段类型"+contentProperty.getField().getType().getName()
                    );
                }
                longs.add(Long.parseLong(s));
            }
            return longs;
        }catch (Exception e){
            e.printStackTrace();
            logger.error(" 类名 "+ contentProperty.getField().toString() +
                    " 字段名称 "+ contentProperty.getHead().getFieldName() +
                    " 列号 " + contentProperty.getHead().getColumnIndex() );
            throw e;
        }
    }

    @Override
    public CellData convertToExcelData(List value, ExcelContentProperty contentProperty,
                                       GlobalConfiguration globalConfiguration) throws Exception {
        throw new RuntimeException("没实现导表");
    }

}
