package com.aebiz.commons.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * spring工具类
 * Created by wizzer on 2016/12/28.
 */
@Component
public class SpringUtil {
    public static ApplicationContext applicationContext = null;

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return ra.getRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return ra.getResponse();
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String id, Class<T> clazz) {
        return applicationContext.getBean(id, clazz);
    }

    public static Object getBean(String id) {
        return applicationContext.getBean(id);
    }

    /**
     * 判断是否启用Reids
     *
     * @return
     */
    public static boolean isRedisEnabled() {
        try {
            return getBean("redisTemplate") != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否启用缓存
     *
     * @return
     */
    public static boolean isCacheEnabled() {
        try {
            return getBean("cacheManager") != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否启用队列
     *
     * @return
     */
    public static boolean isRabbitEnabled() {
        try {
            return applicationContext.getBean("rabbitConnectionFactory") != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否启用定时任务
     *
     * @return
     */
    public static boolean isQuartzEnabled() {
        try {
            return applicationContext.getBean("clusterScheduler") != null;
        } catch (Exception e) {
            return false;
        }
    }
}
