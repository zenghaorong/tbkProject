package com.aebiz.app.wx.modules.services;

import com.aebiz.baseframework.base.service.BaseService;
import com.aebiz.app.wx.modules.models.Wx_config;
import org.nutz.weixin.spi.WxApi2;

public interface WxConfigService extends BaseService<Wx_config>{
    WxApi2 getWxApi2(String wxid);


    /**
     * 公众号和小程序均可以使用AppID和AppSecret调用本接口来获取access_token
     * https请求方式: GET
     * https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
     * @param fetchNew 没有特殊需求必须传出false，如果调用接口是提示access_token过期则传入true
     * @return  access_token，如果access_token获取失败则返回null，
     */
    String getWxApiAccessToken(boolean fetchNew,String code);


    /**
     * 获到用户基本信息
     */
    String getWxApiUserinfo();

}
