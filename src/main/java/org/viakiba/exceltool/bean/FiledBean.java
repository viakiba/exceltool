package org.viakiba.exceltool.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class FiledBean {
    private String filedName;
    private String filedType;
    private String filedDesc;
    private String excelProperty;
}
