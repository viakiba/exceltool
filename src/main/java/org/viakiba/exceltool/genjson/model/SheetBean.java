package org.viakiba.exceltool.genjson.model;

public class SheetBean {

    private String fileName;
    private int sheetNo;
    private String idNameIndex;
    private String idName;
    private String jsonFileName;

    public SheetBean() {
    }

    public String getjsonFileName() {
        return jsonFileName;
    }

    public void setjsonFileName(String jsonFileName) {
        this.jsonFileName = jsonFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSheetNo() {
        return sheetNo;
    }

    public void setSheetNo(int sheetNo) {
        this.sheetNo = sheetNo;
    }

    public String getIdNameIndex() {
        return idNameIndex;
    }

    public void setIdNameIndex(String idNameIndex) {
        this.idNameIndex = idNameIndex;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }
}
