package com.aebiz.baseframework.rabbit;

import java.io.Serializable;

/**
 * 消息实体类
 * Created by wizzer on 2016/12/29.
 */
public class RabbitMessage implements Serializable {
    private static final long serialVersionUID = -6778170718151494509L;

    private String exchange;//交换器

    private Object[] params;

    private String routeKey;//路由key

    public RabbitMessage() {
    }

    public RabbitMessage(String exchange, String routeKey, Object... params) {
        this.params = params;
        this.exchange = exchange;
        this.routeKey = routeKey;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }
}
