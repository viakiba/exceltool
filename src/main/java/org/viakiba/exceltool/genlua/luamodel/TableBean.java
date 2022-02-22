package org.viakiba.exceltool.genlua.luamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class TableBean {

    public LinkedHashMap header;
    public LinkedHashMap desc;
    public LinkedHashMap type;
    public List<String> typeIndexStrs;
    public String idIndex;

    public List<LinkedHashMap> data;

    public LinkedHashMap getHeader() {
        return header;
    }

    public void setHeader(LinkedHashMap header) {
        this.header = header;
    }

    public LinkedHashMap getDesc() {
        return desc;
    }

    public void setDesc(LinkedHashMap desc) {
        this.desc = desc;
    }

    public List<LinkedHashMap> getData() {
        return data;
    }

    public void setData(List<LinkedHashMap> data) {
        this.data = data;
    }

    public String getDescIndex(int index){
        return String.valueOf(desc.get(index));
    }
    public String getTypeIndex(int index){
        return String.valueOf(type.get(index));
    }

    public LinkedHashMap getType() {
        return type;
    }

    public void setType(LinkedHashMap type) {
        this.type = type;
    }

    public String getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(String idIndex) {
        this.idIndex = idIndex;
    }

    public List<String> getStrs(){
        Iterator iterator = type.keySet().iterator();
        typeIndexStrs = new ArrayList<>();
        while (iterator.hasNext()){
            typeIndexStrs.add(String.valueOf(iterator.next()));
        }
        return typeIndexStrs;
    }

}
