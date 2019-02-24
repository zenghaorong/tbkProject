package com.aebiz.baseframework.redis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

/**
 * JedisAgent工厂类，方便初始化参数
 * Created by wizzer on 2017/1/21.
 */
public class JedisAgentFactoryBean implements FactoryBean<JedisAgent>, BeanFactoryPostProcessor {
    
    protected String mode;
    
    protected JedisAgent jedisAgent;

    public JedisAgent getObject() throws Exception {
        return jedisAgent;
    }

    public Class<?> getObjectType() {
        return JedisAgent.class;
    }

    public boolean isSingleton() {
        return true;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        if ("cluster".equals(mode)) {
            jedisAgent = new JedisAgent(beanFactory.getBean(JedisCluster.class));
        } else {
            jedisAgent = new JedisAgent(beanFactory.getBean(JedisPool.class));
        }
    }

}
