package com.getsimplex.steptimer.datarepository;

import com.getsimplex.steptimer.utils.JedisClient;
import com.google.gson.Gson;
import com.google.common.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class GenericRepository<T> {


    private static JedisClient jedisClient;
    private static Gson gson = new Gson();
    private static Class clazz;
    private static String simpleClassName;

    static {
        jedisClient=new JedisClient();



    }

    public GenericRepository(){
        try {
            Type returnType = getClass().getMethod("getArrayAtKey",null).getGenericParameterTypes()[0];
            if (returnType instanceof ParameterizedType){
                ParameterizedType type = (ParameterizedType) returnType;
                Type[] typeArguments = type.getActualTypeArguments();
                for(Type typeArgument: typeArguments){
                    Class typeArgClass = (Class) typeArgument;
                }
            }
            simpleClassName = clazz.getSimpleName();
        } catch (Exception e){

        }
    }

    public  <T> void addToArrayAtKey (String key,T object){
        JedisClient.jsonArrayAdd(simpleClassName,"$."+key,object);
    }

    public ArrayList<T> getArrayAtKey(String key){
        ArrayList<T> arrayList = new ArrayList<>();

        JSONArray jsonArray = jedisClient.jsonGetArray("TestCustomersArray","$."+key);
        for(Object object:jsonArray){
            JSONObject jsonObject = (JSONObject) object;
            T typedObject = (T) gson.fromJson(jsonObject.toString(),clazz);
            arrayList.add(typedObject);
        }

        return arrayList;
    }

}
