package com.aebiz.app.alipay.modules.service;

import com.aebiz.app.alipay.modules.models.AliPayFromQO;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/19  10:31
 * @Description: 支付宝支付下单接口
 */
public interface AlipayService {


    /***
     * 支付宝手机网站支付接口
     * H5下单返回支付表单接口
     */
    public String aliGetPayFrom(AliPayFromQO aliPayFromQO);

    /**
     * 支付宝退款接口
     */
    public boolean aliReturn(AliPayFromQO aliPayFromQO);


}
