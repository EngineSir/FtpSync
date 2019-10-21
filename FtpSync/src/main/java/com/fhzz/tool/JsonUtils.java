package com.fhzz.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    /**
     * 序列化工具
     *
     * @param t
     * @return
     * @throws JsonProcessingException
     */
    public static <T> String serialize(T t) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(t);
        return jsonResult;
    }

    /**
     * 反序列化工具
     *
     * @param string
     * @param clazz
     * @return
     * @throws IOException
     */
    public static <T> T deserialize(String string, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(string, clazz);
    }



    /**
     * 作者：MYL
     * 用于复杂类型转换，如转换成List或Map
     * 如果是ArrayList<YourBean>那么使用ObjectMapper 的getTypeFactory().constructParametricType(collectionClass, elementClasses);
     * 如果是HashMap<String,YourBean>那么 ObjectMapper 的getTypeFactory().constructParametricType(HashMap.class,String.class, YourBean.class);
     * JavaType javaType = getCollectionType(ArrayList.class, YourBean.class);
     * List<YourBean> lst =  (List<YourBean>)mapper.readValue(jsonString, javaType);
     * 获取泛型的Collection Type
     * @param collectionClass 泛型的Collection
     * @param elementClasses 元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static <T> List<T> deserializeArrayList(String string, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = getCollectionType(ArrayList.class, clazz);
        return  (List<T>)mapper.readValue(string, javaType);
    }


    public static <T> Map<T, T> deserializeHashMap(String string, Class<T> keyClazz , Class<T> valueClazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = getCollectionType(HashMap.class,keyClazz, valueClazz);
        return (Map<T,T>)mapper.readValue(string, javaType);
    }



}
