package com.aebiz.app.wx.modules.services.impl;

import com.aebiz.app.web.commons.utils.HttpClientUtil;
import com.aebiz.app.web.commons.utils.WXPayUtil;
import com.aebiz.app.wx.modules.models.WxGetPayInfoQO;
import com.aebiz.app.wx.modules.services.WxConfigService;
import com.aebiz.app.wx.modules.services.WxPayService;
import com.aebiz.commons.utils.DateUtil;
import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/19  14:17
 * @Description: 微信支付实现
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    private static final Log log = Logs.get();

    @Autowired
    private PropertiesProxy config;
    @Autowired
    private WxConfigService wxConfigService;


    @Override
    public String wxGetPayInfo(WxGetPayInfoQO wxGetPayInfoQO) {
        try {

            String key = config.get("wx.pay.sign.key");
            String appid = config.get("wx.pay.AppID");
            String appSecret = config.get("wx.pay.AppSecret");
            String mchid = config.get("wx.pay.mchid");
            String notify_url = config.get("wx.pay.notify_url");

            SortedMap<String,String> map =  new TreeMap<String, String>();
            String nonce_str = WXPayUtil.generateNonceStr();
            String total=wxGetPayInfoQO.getTotal_fee();
            map.put("appid",appid);
            map.put("mch_id",mchid);
            map.put("nonce_str", nonce_str);
            map.put("body", wxGetPayInfoQO.getProductBody());
            map.put("out_trade_no", wxGetPayInfoQO.getOut_trade_no());
            map.put("total_fee",total); //金额(分)
            map.put("spbill_create_ip", wxGetPayInfoQO.getThisIp());
            map.put("notify_url", notify_url);//通知地址
            map.put("trade_type", "JSAPI");//微信浏览器里支付 必须传openId
            map.put("openid", wxGetPayInfoQO.getOpenId());
            map.put("sign_type","MD5");
            String signStr = WXPayUtil.createSign(map, key);//签名参数
            map.put("sign", signStr);
            log.info("传入map参数:" + JSON.toJSONString(map));
            JSONObject mapNobject = JSONObject.fromObject(map);
            String postXml = json2Xml(mapNobject);
            log.info("请求xml: "+postXml);
            //3.统一下单
            String wxXml = HttpClientUtil.submitHttpDate("https://api.mch.weixin.qq.com/pay/unifiedorder", postXml);
            log.info("微信下单返回参数" + wxXml);
            JSONObject xmlJson =  xml2Json(wxXml);
            log.info("微信下单返回参数信息："+xmlJson.toString());
            //判断下单是否成功
             if(!"SUCCESS".equals(xmlJson.get("return_code"))){
                 log.error("微信下单失败："+xmlJson.get("return_msg"));
                 return null;
             }else {
                 //统一下单成功后拿到微信返回的参数 进行签名返回给H5端jspai唤起支付
                 String timeStamp = Calendar.getInstance().getTimeInMillis()+""; //时间戳
                 String nonceStr = WXPayUtil.generateNonceStr(); //随机串
                 String prepay_id = "prepay_id="+xmlJson.getString("prepay_id");
                 SortedMap<String,String> jsapiMap =  new TreeMap<String, String>();
                 jsapiMap.put("appId",appid);
                 jsapiMap.put("timeStamp",timeStamp.substring(0,timeStamp.length()-3));
                 jsapiMap.put("nonceStr",nonceStr);
                 jsapiMap.put("package",prepay_id);
                 jsapiMap.put("signType","MD5");
                 String jsapiSign = WXPayUtil.createSign(jsapiMap, key);//签名参数
                 jsapiMap.put("paySign",jsapiSign);
                 log.info("返回jsapi签名参数："+JSON.toJSONString(jsapiMap));
                 return JSON.toJSONString(jsapiMap);
             }
        }catch (Exception e){
            log.error("请求微信支付二维码异常",e);
        }
        return null;
    }

    @Override
    public String wxGetPayInfoH5(WxGetPayInfoQO wxGetPayInfoQO) {
        try {

            String key = config.get("wx.pay.sign.key");
            String appid = config.get("wx.pay.AppID");
            String appSecret = config.get("wx.pay.AppSecret");
            String mchid = config.get("wx.pay.mchid");
            String notify_url = config.get("wx.pay.notify_url");

            Map map = new HashMap();
            String nonce_str = String.valueOf(System.currentTimeMillis());
            String total=wxGetPayInfoQO.getTotal_fee();
            map.put("appid",appid);
            map.put("mch_id",mchid);
            map.put("nonce_str", nonce_str);
            map.put("body", wxGetPayInfoQO.getProductBody());
            map.put("out_trade_no", wxGetPayInfoQO.getOut_trade_no());
            map.put("total_fee",total); //金额(分)
            map.put("spbill_create_ip", wxGetPayInfoQO.getThisIp());
            map.put("notify_url", notify_url);//通知地址
            map.put("trade_type", "MWEB");//H5支付的交易类型为MWEB
            map.put("product_id", wxGetPayInfoQO.getProductId());
            String signStr = WXPayUtil.generateSignature(map, key);//签名参数
            map.put("sign", signStr);
            log.info("传入map参数:" + JSON.toJSONString(map));
            JSONObject mapNobject = JSONObject.fromObject(map);
            String postXml = json2Xml(mapNobject);
            //3.统一下单
            String wxXml = HttpClientUtil.submitHttpDate("https://api.mch.weixin.qq.com/pay/unifiedorder", postXml);
            log.info("微信下单返回参数" + wxXml);
            JSONObject xmlJson =  xml2Json(wxXml);
            xmlJson.put("timeStamp", DateUtil.getTime(new Date())+"");
            log.info("微信下单返回参数信息："+xmlJson.toString());
            //判断下单是否成功
            if(!"SUCCESS".equals(xmlJson.get("return_code"))){
                log.error("微信下单失败："+xmlJson.get("return_msg"));
                return null;
            }else {
                return xmlJson.toString();
            }
        }catch (Exception e){
            log.error("请求微信支付二维码异常",e);
        }
        return null;
    }


    /**
     * 微信退款
     * @return
     */
    @Override
    public boolean wxRefund(WxGetPayInfoQO wxGetPayInfoQO) {
        try {

            String key = config.get("wx.pay.sign.key");
            String appid = config.get("wx.pay.AppID");
            String appSecret = config.get("wx.pay.AppSecret");
            String mchid = config.get("wx.pay.mchid");
            String path = config.get("wx.certificate.p12.path");

            SortedMap<String,String> map =  new TreeMap<String, String>();
            String nonce_str = WXPayUtil.generateNonceStr();
            String total=wxGetPayInfoQO.getTotal_fee();
            map.put("appid",appid);
            map.put("mch_id",mchid);
            map.put("nonce_str", nonce_str);
            map.put("out_trade_no", wxGetPayInfoQO.getOut_trade_no());
            map.put("out_refund_no", wxGetPayInfoQO.getOut_trade_no());
            map.put("total_fee",total); //金额(分)
            map.put("refund_fee",total); //退款金额(分)
            map.put("sign_type","MD5");
            String signStr = WXPayUtil.createSign(map, key);//签名参数
            map.put("sign", signStr);
            log.info("传入map参数:" + JSON.toJSONString(map));
            JSONObject mapNobject = JSONObject.fromObject(map);
            String postXml = json2Xml(mapNobject);
            log.info("请求xml: "+postXml);
            //3.退款接口
            String wxXml = HttpClientUtil.payHttps("https://api.mch.weixin.qq.com/secapi/pay/refund",mchid,
                    path,postXml);
            log.info("微信退款返回参数" + wxXml);
            JSONObject xmlJson =  xml2Json(wxXml);
            log.info("微信退款返回参数信息："+xmlJson.toString());
            //判断下单是否成功
            if(!"SUCCESS".equals(xmlJson.get("return_code"))){
                log.error("微信退款失败："+xmlJson.get("return_msg"));
                return false;
            }else {
                return true;
            }
        }catch (Exception e) {
            log.error("请求微信支付二维码异常", e);
        }
        return false;
    }


    @Override
    public String wxQueryOrderStatus(String out_trade_no) {
            try {
                String key = config.get("wx.pay.sign.key");
                String appid = config.get("wx.pay.AppID");
                String mchid = config.get("wx.pay.mchid");
                String path = config.get("wx.certificate.p12.path");

                SortedMap<String,String> map =  new TreeMap<String, String>();
                String nonce_str = WXPayUtil.generateNonceStr();
                map.put("appid",appid);
                map.put("mch_id",mchid);
                map.put("nonce_str", nonce_str);
                map.put("out_trade_no", out_trade_no);
                map.put("sign_type","MD5");
                String signStr = WXPayUtil.createSign(map, key);//签名参数
                map.put("sign", signStr);
                log.info("传入map参数:" + JSON.toJSONString(map));
                JSONObject mapNobject = JSONObject.fromObject(map);
                String postXml = json2Xml(mapNobject);
                log.info("请求xml: "+postXml);
                //3.退款接口
                String wxXml = HttpClientUtil.payHttps("https://api.mch.weixin.qq.com/pay/orderquery",mchid,
                        path,postXml);
                log.info("微信订单查询返回参数" + wxXml);
                JSONObject xmlJson =  xml2Json(wxXml);
                log.info("微信订单查询返回参数："+xmlJson.toString());
                //判断下单是否成功
                if(!"SUCCESS".equals(xmlJson.get("return_code"))){
                    log.error("微信退款失败："+xmlJson.get("return_msg"));
                    return "";
                }else {
                    //SUCCESS—支付成功
                    //
                    //REFUND—转入退款
                    //
                    //NOTPAY—未支付
                    //
                    //CLOSED—已关闭
                    //
                    //REVOKED—已撤销（付款码支付）
                    //
                    //USERPAYING--用户支付中（付款码支付）
                    //
                    //PAYERROR--支付失败(其他原因，如银行返回失败)
                    //
                    //支付状态机请见下单API页面
                    String orderStatus = (String) xmlJson.get("trade_state");
                    return orderStatus;
                }
            }catch (Exception e) {
                log.error("请求微信支付二维码异常", e);
            }
            return "";
    }

    /**
     * xml转json
     * @param xml
     * @return
     */
    public static JSONObject xml2Json(String xml){
        XMLSerializer xmlSerializer = new XMLSerializer();
        //将xml转为json（注：如果是元素的属性，会在json里的key前加一个@标识）
        String  json =  xmlSerializer.read(xml).toString();
        return JSONObject.fromObject(json);
    }

    /**
     * json转xml
     * @param json
     * @return
     * @throws DocumentException
     */
    public static String json2Xml(JSONObject json) throws DocumentException {
        String sXml = "";
        XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setTypeHintsEnabled(false);
        xmlSerializer.setRootName("xml");
        String sContent = xmlSerializer.write(json,"utf-8");
        try {
            Document docCon = DocumentHelper.parseText(sContent);
            sXml = docCon.getRootElement().asXML();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sXml;
    }


    public static void main(String[] args) throws DocumentException {
        String s = "<xml>\n" +
                "   <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "   <return_msg><![CDATA[OK]]></return_msg>\n" +
                "   <appid><![CDATA[wx2421b1c4370ec43b]]></appid>\n" +
                "   <mch_id><![CDATA[10000100]]></mch_id>\n" +
                "   <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>\n" +
                "   <openid><![CDATA[oUpF8uMuAJO_M2pxb1Q9zNjWeS6o]]></openid>\n" +
                "   <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>\n" +
                "   <result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "   <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>\n" +
                "   <trade_type><![CDATA[JSAPI]]></trade_type>\n" +
                "</xml>";

        /**
         * xml转json对象
         */
        JSONObject xmlJson =  xml2Json(s);
        System.out.println(xmlJson.toString());
        //json转xml
        System.out.println(json2Xml(xmlJson));
    }


}
