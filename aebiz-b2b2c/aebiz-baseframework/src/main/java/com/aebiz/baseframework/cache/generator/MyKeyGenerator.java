package com.aebiz.baseframework.cache.generator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 这个类是给spring-cache 用的,默认的key生成规则
 * Created by wizzer on 2016/12/21.
 */
public class MyKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return StringUtils.join(target.getClass().getName(),".", method.getName(), "#", Arrays.toString(params));
    }
}
