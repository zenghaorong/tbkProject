package com.aebiz.app.tbk.modules.models;

/**
 * @Auther: zenghaorong
 * @Date: 2019/7/14  12:23
 * @Description: api接口调用配置文件
 */
public class TbkConfig {

    // TOP服务地址，正式环境需要设置为http://gw.api.taobao.com/router/rest
    public static final String serverUrl = "http://gw.api.taobao.com/router/rest";
    public static final String appKey = "27666005"; // 可替换为您的沙箱环境应用的AppKey
    public static final String appSecret = "9b48c1d6a222c88c8077f2c34c1d3f14"; // 可替换为您的沙箱环境应用的AppSecret
    public static final String sessionKey = "6101719197ebae75a13eb3db2d91f11f939b4f59a4d3cac74948387"; // 必须替换为沙箱账号授权得到的真实有效SessionKey



}
