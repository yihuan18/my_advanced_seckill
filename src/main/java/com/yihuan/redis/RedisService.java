package com.yihuan.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

    @Autowired
    private JedisPool jedisPool;

    /*
        services provided : get/set/exists
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> tClass) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //get object
            String realKey = prefix.getPrefix() + key;
            String string = jedis.get(realKey);
            //deserialize
            return stringToBean(string, tClass);

        } finally {
            returnJedis(jedis);
        }
    }

    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //serialize
            String string = beanToString(value);
            if (string == null || string.length() <= 0)
                return false;
            //set
            String realKey = prefix.getPrefix() + key;
            int expireSeconds = prefix.expireSeconds();
            if (expireSeconds <= 0) {
                jedis.set(realKey, string);
            } else {
                jedis.setex(realKey, expireSeconds, string);
            }
            return true;
        } finally {
            returnJedis(jedis);
        }
    }

    public boolean exists(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            returnJedis(jedis);
        }
    }

    public Long incr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        }finally {
            returnJedis(jedis);
        }
    }

    public Long decr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally {
            returnJedis(jedis);
        }
    }

    public boolean del(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            long ret = jedis.del(realKey);
            return ret > 0;
        }finally {
            returnJedis(jedis);
        }
    }


    /*
        Utils
     */
    private void returnJedis(Jedis jedis) {
        if (jedis != null)
            jedis.close();
    }

    //serialize
    public static  <T> T stringToBean(String string, Class<T> tClass) {
        if (string == null || string.length() <= 0 || tClass == null)
            return null;
        if (tClass == int.class || tClass == Integer.class) {
            return (T) Integer.valueOf(string);
        } else if (tClass == long.class || tClass == Long.class) {
            return (T) Long.valueOf(string);
        } else if (tClass == String.class) {
            return (T) string;
        } else {
            return JSON.toJavaObject(JSON.parseObject(string), tClass);
        }
    }

    //deserialize
    public static  <T> String beanToString(T value) {
        if (value == null)
            return null;
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }
}
