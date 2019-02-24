package com.aebiz.baseframework.view.annotation;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个类的作用就是把@SJson 处理设置在return第一个位置,
 * 因为 RequestMappingHandlerAdapter 用户自定义的return在MapMethodProcessor之后,导致Map类型数据不能被处理
 * <mvc:return-value-handlers>配置的是默认顺序,同理不能处理Map类型
 * Created by wizzer on 2017/2/14.
 */
public class RequestMappingHandlerAdapter extends org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter {
    private List<HandlerMethodReturnValueHandler> prefixReturnValueHandlers;
    private List<HandlerMethodArgumentResolver> prefixArgumentResolver;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (prefixReturnValueHandlers != null) {
            List<HandlerMethodReturnValueHandler> oldHandlerMethodReturnValueHandler = this.getReturnValueHandlers();
            List<HandlerMethodReturnValueHandler> newHandlerMethodReturnValueHandler = new ArrayList<>();
            newHandlerMethodReturnValueHandler.addAll(prefixReturnValueHandlers);
            newHandlerMethodReturnValueHandler.addAll(oldHandlerMethodReturnValueHandler);
            super.setReturnValueHandlers(newHandlerMethodReturnValueHandler);
        }
        if (prefixArgumentResolver != null) {
            List<HandlerMethodArgumentResolver> oldHandlerMethodArgumentResolver = this.getArgumentResolvers();
            List<HandlerMethodArgumentResolver> newHandlerMethodArgumentResolver = new ArrayList<>();
            newHandlerMethodArgumentResolver.addAll(prefixArgumentResolver);
            newHandlerMethodArgumentResolver.addAll(oldHandlerMethodArgumentResolver);
            super.setArgumentResolvers(newHandlerMethodArgumentResolver);
        }
    }

    public List<HandlerMethodReturnValueHandler> getPrefixReturnValueHandlers() {
        return prefixReturnValueHandlers;
    }

    public void setPrefixReturnValueHandlers(List<HandlerMethodReturnValueHandler> prefixReturnValueHandlers) {
        this.prefixReturnValueHandlers = prefixReturnValueHandlers;
    }

    public List<HandlerMethodArgumentResolver> getPrefixArgumentResolver() {
        return prefixArgumentResolver;
    }

    public void setPrefixArgumentResolver(List<HandlerMethodArgumentResolver> prefixArgumentResolver) {
        this.prefixArgumentResolver = prefixArgumentResolver;
    }
}
