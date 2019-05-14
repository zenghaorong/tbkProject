package com.aebiz.app.alipay.modules.service.impl;

import com.aebiz.app.alipay.modules.models.AliPayFromQO;
import com.aebiz.app.alipay.modules.models.AlipayConfig;
import com.aebiz.app.alipay.modules.service.AlipayService;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/19  10:31
 * @Description: 支付宝支付下单接口
 */
@Service
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    private PropertiesProxy config;

    private static final Log log = Logs.get();

    @Override
    public String aliGetPayFrom(AliPayFromQO aliPayFromQO) {
        try {

            // 商户订单号，商户网站订单系统中唯一订单号，必填
            String out_trade_no = aliPayFromQO.getOut_trade_no();

            String notify_url = config.get("alipay.notify_url");

            String return_url = config.get("alipay.return_url")+"?orderId="+out_trade_no;

            // 订单名称，必填
            String subject = aliPayFromQO.getSubject();
            log.info(subject);
            // 付款金额，必填
            String total_amount=aliPayFromQO.getTotal_amount();
            // 商品描述，可空
            String body = aliPayFromQO.getProductBody();
            // 超时时间 可空
//            String timeout_express="2m";
            // 销售产品码 必填
            String product_code="QUICK_WAP_WAY";

            log.info("支付宝支付参数："+ JSON.toJSONString(aliPayFromQO));

            /**********************/
            // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
            //调用RSA签名方式
            AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
            AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();

            // 封装请求支付信息
            AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
            model.setOutTradeNo(out_trade_no);
            model.setSubject(subject);
            model.setTotalAmount(total_amount);
            model.setBody(body);
//            model.setTimeoutExpress(timeout_express);
            model.setProductCode(product_code);
            alipay_request.setBizModel(model);
            // 设置异步通知地址
            alipay_request.setNotifyUrl(notify_url);
            // 设置同步地址
            alipay_request.setReturnUrl(return_url);

            // form表单生产
            String form = "";

            // 调用SDK生成表单
            form = client.pageExecute(alipay_request).getBody();
            log.info("支付宝请求支付form表单："+form);
            return form;
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            log.error("获取支付宝支付表单时异常",e);
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public boolean aliReturn(AliPayFromQO aliPayFromQO) {
        try {
            //商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
            //商户订单号，和支付宝交易号二选一
            String out_trade_no = aliPayFromQO.getOut_trade_no();
            //支付宝交易号，和商户订单号二选一
    //        String trade_no = new String(request.getParameter("WIDtrade_no").getBytes("ISO-8859-1"),"UTF-8");
            //退款金额，不能大于订单总金额
            String refund_amount=aliPayFromQO.getTotal_amount();
            //退款的原因说明
            String refund_reason="商城用户退款";
            //标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
            String  out_request_no=aliPayFromQO.getOut_request_no();
            /**********************/
            // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
            AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
            AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();

            AlipayTradeRefundModel model=new AlipayTradeRefundModel();
            model.setOutTradeNo(out_trade_no);
    //        model.setTradeNo(trade_no);
            model.setRefundAmount(refund_amount);
            model.setRefundReason(refund_reason);
            model.setOutRequestNo(out_request_no);
            alipay_request.setBizModel(model);

            AlipayTradeRefundResponse alipay_response =client.execute(alipay_request);
            System.out.println("支付宝退款返回结果："+alipay_response.getBody());
            return true;
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            log.error("支付宝退款时异常",e);
            e.printStackTrace();
            return false;
        }
    }
}
