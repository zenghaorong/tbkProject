package com.aebiz.app.alipay.modules.service.impl;

import com.aebiz.app.alipay.modules.models.AliPayFromQO;
import com.aebiz.app.alipay.modules.models.AlipayConfig;
import com.aebiz.app.alipay.modules.service.AlipayService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
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

            String notify_url = config.get("alipay.notify_url");

            String return_url = config.get("alipay.return_url");

            // 商户订单号，商户网站订单系统中唯一订单号，必填
            String out_trade_no = aliPayFromQO.getOut_trade_no();
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
            return form;
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            log.error("获取支付宝支付表单时异常",e);
            e.printStackTrace();
            return null;
        }
    }
}
