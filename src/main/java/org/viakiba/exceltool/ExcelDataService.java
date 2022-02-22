package org.viakiba.exceltool;

import org.viakiba.exceltool.annotate.UnionAnnotate;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExcelDataService {

    public final static Map<Class, Map<Object,Object>> dataMap = new ConcurrentHashMap<>();

    public final static Map<Class, Map<Object,Map<Object,Object>>> unionDataMap = new ConcurrentHashMap<>();

    /**
     * init put 系统启动时使用此方法做存放
     * @param classZ 参数
     * @param id 参数
     * @param data 参数
     * @throws RuntimeException  参数
     * @throws Exception  参数
     */
    public static void put(Class classZ,Object id,Object data) throws RuntimeException, Exception {
        Map<Object, Object> longObjectMap = dataMap.get(classZ);
        if(longObjectMap == null){
            longObjectMap = new ConcurrentHashMap<>();
            dataMap.put(classZ,longObjectMap);
        }
        longObjectMap.put(id,data); //通用数据集合
        UnionAnnotate annotation = data.getClass().getAnnotation(UnionAnnotate.class);
        if(annotation==null){
            return;
        }
        String declaredAnnotationName = annotation.type();
        Map<Object, Map<Object, Object>> class2TypeMap = unionDataMap.get(classZ); //一级分类数据集合
        if(class2TypeMap == null){
            class2TypeMap = new ConcurrentHashMap<>();
            unionDataMap.put(classZ,class2TypeMap);
        }

        Field field = data.getClass().getDeclaredField(declaredAnnotationName);
        field.setAccessible(true);//允许访问私有字段
        Object typeValue = field.get(data);

        Map<Object, Object> type2Map = class2TypeMap.get(typeValue);
        if(type2Map == null){
            type2Map = new ConcurrentHashMap<>();
            class2TypeMap.put(typeValue,type2Map);
        }
        type2Map.put(id,data);
    }

    public static <T> T getByClassAndId(Class classZ,Object id){
        Map<Object, Object> longObjectMap = dataMap.get(classZ);
        if(longObjectMap == null){
            return null;
        }
        return (T)longObjectMap.get(id);
    }

    public static <Integer,T> Map<Integer,T> getByClass(Class classZ){
        Map<Integer, T> longObjectMap = (Map<Integer,T>) dataMap.get(classZ);
        return longObjectMap;
    }

    public static <Integer,T> Map<Integer,T> getByClassAndTypeName(Class classZ,Object typeValue){
        Map<Object, Map<Object, Object>> stringMapMap = unionDataMap.get(classZ);
        if(stringMapMap == null){
            return null;
        }
        Map<Integer, T> longObjectMap = (Map<Integer,T>) stringMapMap.get(typeValue);
        return longObjectMap;
    }

}
