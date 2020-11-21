package com.getsimplex.steptimer.utils;


import com.typesafe.config.Config;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Created by Admin on 8/18/2016.
 */
public class JedisClient {

    private static Config config;
    private static String password;
    private static String host;
    private static String port;
    private static String dbName;
    private static String url;
    private static Jedis jedis;

    static {
        Config config = Configuration.getConfiguration();
        if (System.getenv("REDIS_HOST")!=null && !System.getenv("REDIS_HOST").isEmpty()){

            password = System.getenv("REDIS_PASSWORD");
            host = System.getenv("REDIS_HOST");
            port = System.getenv("REDIS_PORT");
            dbName = System.getenv("REDIS_DB");
            url = "redis://:"+password+"@"+host+":"+port+"/"+dbName;

        } else{
            config = Configuration.getConfiguration();
            password = config.getString("redis.password");
            host = config.getString("redis.host");
            port = config.getString("redis.port");
            dbName = config.getString("redis.db");
            url = "redis://:"+password+"@"+host+":"+port+"/"+dbName;

        }
        jedis  = new Jedis(url);
    }

    public static synchronized Jedis getJedis(){

        try{
            jedis.ping();
        }

        catch (Exception e){
            jedis = new Jedis(url);
        }
        return jedis;
    }

    public static synchronized void set(String key, String value) throws Exception{
        int tries =0;
        try{
            tries ++;
            jedis.set(key,value);
        } catch(Exception e){
            if (tries<1000) {
                getJedis();
                set(key, value);
            } else{
                throw new Exception ("Tried 1000 times setting key:"+key+ " and value:"+value+" without success");
            }
        }
    }

    public static synchronized Boolean exists(String key) throws Exception{
        int tries =0;
        try{
            tries++;
            return jedis.exists(key);
        }

        catch (Exception e ){
            if (tries<1000)
            {
                getJedis();
                return exists(key);
            }

            else {
                throw new Exception ("Tried 1000 times exists on key:"+key+" without success");
            }
        }
    }

    public static synchronized Set<String> zrange(String key, int start, int end) throws Exception{
        int tries =0;
        try {
            tries ++;
            return jedis.zrange(key,start,end);
        }

        catch (Exception e){
            if (tries<1000)
            {
                getJedis();
                return zrange(key,start,end);
            }
            else{
                throw new Exception("Tried 1000 times to get range:"+key+" start:"+start+" end:"+end+" without success");
            }
        }
    }

    public static synchronized void zadd(String key, int score, String value) throws Exception{
        int tries =0;
        try {
            tries ++;
            jedis.zadd(key,score,value);
        }

        catch (Exception e){
            if (tries<1000)
            {
                getJedis();
                jedis.zadd(key,score,value);
            }
            else{
                throw new Exception("Tried 1000 times to persist :"+value+" without success");
            }
        }
    }

    public static synchronized long zrem(String key, String value) throws Exception{
        int tries =0;
        try {
            tries ++;
            return jedis.zrem(key,value);
        }

        catch (Exception e){
            if (tries<1000)
            {
                getJedis();
                return jedis.zrem(key,value);
            }
            else{
                throw new Exception("Tried 1000 times to remove key: "+key+" value: "+value+" without success");
            }
        }
    }

    public static synchronized void zremrangeByScore(String key, double start, double end) throws Exception{
        int tries =0;
        try {
            tries ++;
            jedis.zremrangeByScore(key,start,end);
        }

        catch (Exception e){
            if (tries<1000)
            {
                getJedis();
                jedis.zremrangeByScore(key,start,end);
            }
            else{
                throw new Exception("Tried 1000 times to remove :"+key+" without success");
            }
        }
    }

    public static synchronized Long zcount(String keyName, double min, double max) throws Exception{
        int tries =0;
        try {
            tries ++;
            return getJedis().zcount(keyName,min, max);
        }

        catch (Exception e){
            if (tries<1000)
            {
                getJedis();
                return getJedis().zcount(keyName,0d,-1);
            }
            else{
                throw new Exception("Tried 1000 times to zcount :"+keyName+" without success");
            }
        }

    }

    public static synchronized String get(String key) throws Exception{
        int tries = 0;
        try{
            tries ++;
            return jedis.get(key);
        }
        catch (Exception e){
            if (tries<1000) {
                getJedis();
                return get(key);
            }else{
                throw new Exception("Tried 1000 times to get key:"+key+" without success");
            }

        }
    }

    public void disconnect(){
        jedis.disconnect();
    }

}
