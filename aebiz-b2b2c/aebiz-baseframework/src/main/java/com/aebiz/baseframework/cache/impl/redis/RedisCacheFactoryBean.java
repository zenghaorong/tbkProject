package com.aebiz.baseframework.cache.impl.redis;

import com.aebiz.baseframework.redis.JedisAgent;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * 这个类是给spring-cache 用的,使用jedisAgent实现具体功能
 * 只有db0 是可以集群的cluster模式
 * Created by wizzer on 2017/1/21.
 */
public class RedisCacheFactoryBean implements Cache {

    private String name;
    private int liveTime;
    private JedisAgent jedisAgent;

    public void setJedisAgent(JedisAgent jedisAgent) {
        this.jedisAgent = jedisAgent;
    }

    public Jedis jedis() {
        return jedisAgent.jedis();
    }

    @Override
    public Object getNativeCache() {
        return jedis();
    }

    @Override
    public ValueWrapper get(Object key) {
        final String keyf = this.getName()+":"+(String) key;
        try (Jedis jedis = jedis()) {
            byte[] value = jedis.get(keyf.getBytes());
            if (value == null) return null;
            Object object = toObject(value);
            return (object != null ? new SimpleValueWrapper(object) : null);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Object existingValue = get(key);
        if (existingValue == null) {
            put(key, value);
            return null;
        } else {
            return new SimpleValueWrapper(value);
        }
    }

    @Override
    public void put(Object key, Object value) {
        final String keyf = this.getName()+":"+(String) key;
        final Object valuef = value;
        byte[] keyb = keyf.getBytes();
        byte[] valueb = toByteArray(valuef);
        try (Jedis jedis = jedis()) {
            jedis.set(keyb, valueb);
            if (liveTime > 0) {
                jedis.expire(keyb, liveTime);
            }
        }
    }

    /**
     * 描述 : <Object转byte[]>. <br>
     * <p>
     * <使用方法说明>
     * </p>
     *
     * @param obj
     * @return
     */
    private byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 描述 : <byte[]转Object>. <br>
     * <p>
     * <使用方法说明>
     * </p>
     *
     * @param bytes
     * @return
     */
    private Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    @Override
    public void evict(Object key) {
        final String keyf = this.getName()+":"+(String) key;
        try (Jedis jedis = jedis()) {
            if (keyf.endsWith("*")) {
                Set<String> set = jedis.keys(keyf);
                for (String it : set) {
                    jedis.del(it.getBytes());
                }
            } else
                jedis.del(keyf.getBytes());
        }
    }

    /**
     * 清空当前命名空间
     */
    @Override
    public void clear() {
        this.evict("*");
//        try (Jedis jedis = jedis()) {
//            jedis.flushDB();
//        }
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public long getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(int liveTime) {
        this.liveTime = liveTime;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        // TODO Auto-generated method stub
        return null;
    }
}