package com.getsimplex.steptimer.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Sean on 9/1/2015.
 */
public class JedisData {
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();



    public static synchronized <T> ArrayList<T> getEntityList(Class clazz) throws Exception{
        return getEntities(clazz);
    }

    public static synchronized <T> Optional<T> getEntity(Class clazz, String key) throws Exception{
        Optional<String> mapValueOptional = JedisClient.hmget(clazz.getSimpleName()+"Map", key);
        Optional<T> optionalValue = Optional.empty();

        if (mapValueOptional.isPresent()){
            optionalValue = Optional.of((T) gson.fromJson(mapValueOptional.get(), clazz));
        }

        return optionalValue;
    }

    public static synchronized <T> ArrayList<T> getEntities(Class clazz, long beginScore, long endScore) throws Exception{
        Set<String> set = JedisClient.zrangeByScore(clazz.getSimpleName(), beginScore, endScore);
        ArrayList<T> arrayList = new ArrayList<T>();

        // loop through all the keys from the sorted set and for each key get the value from the redis map
        for (String key:set){
            Optional<String> mapValueOptional = JedisClient.hmget(clazz.getSimpleName()+"Map", key);
            if (mapValueOptional.isEmpty()){
                throw new Exception("Map "+clazz.getSimpleName()+" and Key: "+key+" is empty: should contain a JSON object.");
            } else{
                arrayList.add((T) gson.fromJson(mapValueOptional.get(), clazz));
            }
        }

        return arrayList;
    }

    public static synchronized <T> ArrayList<T> getEntities(Class clazz) throws Exception{
        Set<String> set = JedisClient.zrange(clazz.getSimpleName(), 0, -1);
        ArrayList<T> arrayList = new ArrayList<T>();

        // loop through all the keys from the sorted set and for each key get the value from the redis map
        for (String key:set){
            Optional<String> mapValueOptional = JedisClient.hmget(clazz.getSimpleName()+"Map", key);
            if (mapValueOptional.isEmpty()){
                throw new Exception("Map "+clazz.getSimpleName()+" and Key: "+key+" is empty: should contain a JSON object.");
            } else{
                arrayList.add((T) gson.fromJson(mapValueOptional.get(), clazz));
            }
        }

        return arrayList;
    }

    public static synchronized <T> void update(T object, String key){
        JedisClient.hmset(object.getClass().getSimpleName()+"Map", key, gson.toJson(object));
    }

    public static <T> void set(T object, String keyName) throws Exception{
        String jsonFormatted = gson.toJson(object, object.getClass());
        JedisClient.set(keyName, jsonFormatted);
    }

    public static <T> T get(String keyName, Class clazz) throws Exception{
        String jsonFormatted = JedisClient.get(keyName);
        T object = (T) gson.fromJson(jsonFormatted, clazz);
        return object;
    }


    public static <T> void loadToJedis(T record, String id) throws Exception{

        try {
            loadToJedis(record, id, 0);
        } catch (Exception e) {

            throw (e);
        }

    }

    public static <T> void loadToJedis(T record, String id, long score) throws Exception{

        try {
            String jsonFormatted = gson.toJson(record,record.getClass());
            JedisClient.hmset(record.getClass().getSimpleName()+"Map", id, jsonFormatted);
            JedisClient.zadd(record.getClass().getSimpleName(), score, id);
        } catch (Exception e) {

            throw (e);
        }

    }


    public static <T> Long deleteFromRedis(List<T> list) throws Exception{
        Long deleteCount = 0l;
        int i = 0;
//        public List<CorporateDivision> getCorporateDivisionByID(Integer divisionID){
//            ArrayList<CorporateDivision> filteredDivisions = new ArrayList<>();
        for (T lists: list){
            if(deleteCount<list.size()){
//            if(list.size()>0){
                deleteFromRedis(list.get(i));
                deleteCount++;
                i++;
            }
        }

        return deleteCount;
    }

    public static <T> Long deleteFromRedis (T record) throws Exception{
        String jsonFormatted = gson.toJson(record, record.getClass());
        Long removeCount = JedisClient.zrem(record.getClass().getSimpleName(),jsonFormatted);
        if (removeCount!=1){
            throw new Exception("Attempt to remove the following json from redis failed: "+jsonFormatted);
        }
        return removeCount;

    }



}
