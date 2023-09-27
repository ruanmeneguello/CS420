package com.getsimplex.steptimer.datarepository;

import com.getsimplex.steptimer.utils.JedisClient;
import com.google.gson.Gson;
import com.google.common.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GenericRepository<T> {


    private static JedisClient jedisClient;
    private static Gson gson = new Gson();
    private Class clazz;
    private String simpleClassName;

    static {
        jedisClient=new JedisClient();



    }

    public <T> GenericRepository(){
        try {

            this.clazz= (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            this.simpleClassName = this.clazz.getSimpleName();

        } catch (Exception e){
            System.out.println("Exception instantiating GenericRepository for: ");
        }
    }

    public  <T> void addToArrayAtKey (String key,T object){
        JedisClient.jsonArrayAdd(simpleClassName,"$."+key,object);
    }

    public ArrayList<T> getArrayAtKey(String key){
        ArrayList<T> arrayList = new ArrayList<>();

        JSONArray jsonArray = jedisClient.jsonGetArray(simpleClassName,"$."+key);
        for(Object object:jsonArray){
            JSONObject jsonObject = (JSONObject) object;
            T typedObject = (T) gson.fromJson(jsonObject.toString(),clazz);
            arrayList.add(typedObject);
        }

        return arrayList;
    }

}
