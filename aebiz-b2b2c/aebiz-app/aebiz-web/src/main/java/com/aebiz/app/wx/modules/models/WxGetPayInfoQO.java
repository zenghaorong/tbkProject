package com.aebiz.app.wx.modules.models;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/19  14:52
 * @Description: 微信jsapi公众号支付下单接口入参实体类
 */
public class WxGetPayInfoQO {


    //商品描述
    public String productBody;

    //商户订单号
    private String out_trade_no;

    //支付金额 单位分
    private String total_fee;

    //openId
    private String openId;

    //本机ip
    private String thisIp;

    //商品编号
    private String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getThisIp() {
        return thisIp;
    }

    public void setThisIp(String thisIp) {
        this.thisIp = thisIp;
    }

    public String getProductBody() {
        return productBody;
    }

    public void setProductBody(String productBody) {
        this.productBody = productBody;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
