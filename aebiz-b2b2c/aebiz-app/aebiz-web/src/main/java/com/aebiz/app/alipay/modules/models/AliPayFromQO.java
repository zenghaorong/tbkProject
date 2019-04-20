package com.aebiz.app.alipay.modules.models;

import com.aebiz.app.alipay.modules.service.AlipayService;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/19  10:37
 * @Description: 支付宝下单传入参数
 */
public class AliPayFromQO {

    //商户订单号
    private String out_trade_no;

    //订单名称
    private String subject;

    //付款金额 （单位元）
    private String total_amount;

    // 商品描述，可空
    private String productBody;

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getProductBody() {
        return productBody;
    }

    public void setProductBody(String productBody) {
        this.productBody = productBody;
    }
}
