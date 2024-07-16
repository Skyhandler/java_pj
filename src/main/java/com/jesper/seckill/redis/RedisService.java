package com.jesper.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Bean;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

//    从redis连接池获取redis实例
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try {
            //先从redis池中获取redis实例
            jedis = jedisPool.getResource();
            //先对key增加一个前缀
            String realKey = prefix.getPrefix() + key;
            //从redis中读出的是string
            String str = jedis.get(realKey);
            //然后把string转换成clazz
            return stringToBean(str, clazz);

        }finally {
            returnToPool(jedis);
        }
    }
    //存储对象到redis




    public static <T> T stringToBean(String string, Class<T> clazz){
        if (string == null || string.isEmpty() || clazz == null){
            return null;
        }
        if(clazz == Integer.class || clazz == int.class){
            return clazz.cast(Integer.valueOf(string));
        }
        if(clazz == Long.class || clazz == long.class){
            return clazz.cast(Long.valueOf(string));
        }
        if(clazz == String.class){
            return clazz.cast(string);
        }else{
            //其实这一步可以解决上面的所有，只是效率问题
            return JSON.toJavaObject(JSON.parseObject(string),clazz);
        }

    }

    public static void  returnToPool(Jedis jedis){
        if(jedis != null){
            jedis.close();//redis实例返回连接池，不是关闭
        }
    }
}
