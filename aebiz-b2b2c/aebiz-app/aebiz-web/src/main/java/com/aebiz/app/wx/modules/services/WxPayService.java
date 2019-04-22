package com.aebiz.app.wx.modules.services;

import com.aebiz.app.wx.modules.models.WxGetPayInfoQO;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/19  14:16
 * @Description: 微信支付实现
 */
public interface WxPayService {


    /**
     * 微信jsapi公众号支付下单接口
     */
    public String wxGetPayInfo(WxGetPayInfoQO wxGetPayInfoQO);

    /**
     * 微信H5支付下单接口
     */
    public String wxGetPayInfoH5(WxGetPayInfoQO wxGetPayInfoQO);
}
