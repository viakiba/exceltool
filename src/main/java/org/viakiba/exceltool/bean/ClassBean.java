package org.viakiba.exceltool.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class ClassBean {
    private int sheetNo;
    private String fileName;
    private String className;
    private List<FiledBean> filed;
}
