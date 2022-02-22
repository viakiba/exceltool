package org.viakiba.exceltool.genlua.luamodel;

public class SheetBean {

    private String fileName;
    private int sheetNo;
    private String idNameIndex;
    private String idName;
    private String luaFileName;

    public SheetBean() {
    }

    public String getLuaFileName() {
        return luaFileName;
    }

    public void setLuaFileName(String luaFileName) {
        this.luaFileName = luaFileName;
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
