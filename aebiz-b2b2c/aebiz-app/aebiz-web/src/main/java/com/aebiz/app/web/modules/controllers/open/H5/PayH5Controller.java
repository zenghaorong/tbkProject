//package com.aebiz.app.web.modules.controllers.open.H5;
//
//import com.aebiz.app.acc.modules.models.Account_user;
//import com.aebiz.app.alipay.modules.models.AliPayFromQO;
//import com.aebiz.app.alipay.modules.models.AlipayConfig;
//import com.aebiz.app.alipay.modules.service.AlipayService;
//import com.aebiz.app.order.modules.models.Order_goods;
//import com.aebiz.app.order.modules.models.Order_main;
//import com.aebiz.app.order.modules.models.em.OrderPayStatusEnum;
//import com.aebiz.app.order.modules.models.em.OrderPayTypeEnum;
//import com.aebiz.app.order.modules.models.em.OrderTypeEnum;
//import com.aebiz.app.order.modules.services.OrderMainService;
//import com.aebiz.app.web.commons.utils.CalculateUtils;
//import com.aebiz.app.web.commons.utils.HttpRequestUtil;
//import com.aebiz.app.web.commons.utils.TcpipUtil;
//import com.aebiz.app.wx.modules.models.WxGetPayInfoQO;
//import com.aebiz.app.wx.modules.services.WxPayService;
//import com.aebiz.baseframework.base.Result;
//import com.aebiz.baseframework.redis.RedisService;
//import com.aebiz.baseframework.view.annotation.SJson;
//import com.alibaba.fastjson.JSON;
//import com.alipay.api.internal.util.AlipaySignature;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.session.Session;
//import org.apache.shiro.subject.Subject;
//import org.nutz.dao.Cnd;
//import org.nutz.log.Log;
//import org.nutz.log.Logs;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import redis.clients.jedis.Jedis;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.xml.ws.Action;
//import java.util.*;
//
///**
// * @Auther: zenghaorong
// * @Date: 2019/4/22  9:48
// * @Description: 支付相关控制器
// */
//@Controller
//@RequestMapping("/open/h5/pay")
//public class PayH5Controller {
//
//    private static final Log log = Logs.get();
//
//    @Autowired
//    private WxPayService wxPayService;
//
//    @Autowired
//    private AlipayService alipayService;
//
//    @Autowired
//    private OrderMainService orderMainService;
//
//    @Autowired
//    private RedisService redisService;
//
//    private static final String WexinAccessToken="WexinAccessToken_key";
//
//
//
//    /***
//     * 微信H5支付调用控制层
//     */
//    @RequestMapping("/weiXinH5Pay.html")
//    @SJson
//    public Result weiXinH5Pay(String orderId,HttpServletRequest request){
//        try {
//            //查询订单信息
//            Order_main order_main = orderMainService.fetch(orderId);
//            WxGetPayInfoQO wxGetPayInfoQO = new WxGetPayInfoQO();
//            wxGetPayInfoQO.setOut_trade_no(orderId);
//            wxGetPayInfoQO.setProductBody("黏土商城产品-订单支付");
//            wxGetPayInfoQO.setProductId(order_main.getAccountId());
//            wxGetPayInfoQO.setTotal_fee(order_main.getPayMoney().toString());
//            wxGetPayInfoQO.setThisIp(TcpipUtil.getClientRealIp(request));
//            String json=wxPayService.wxGetPayInfoH5(wxGetPayInfoQO);
//            return Result.success("ok",json);
//        } catch (Exception e) {
//            log.error("获取支付信息失败",e);
//            return Result.error("fail");
//        }
//    }
//
//    /***
//     * 微信jsapi支付调用控制层
//     */
//    @RequestMapping("/weiXinJsapiPay.html")
//    @SJson
//    public Result weiXinJsapiPay(String orderId,HttpServletRequest request){
//        try {
//            //查询订单信息
//            Order_main order_main = orderMainService.fetch(orderId);
//            WxGetPayInfoQO wxGetPayInfoQO = new WxGetPayInfoQO();
//            wxGetPayInfoQO.setOut_trade_no(orderId);
//            wxGetPayInfoQO.setProductBody("body");
//            wxGetPayInfoQO.setProductId(order_main.getAccountId());
//            wxGetPayInfoQO.setTotal_fee(order_main.getPayMoney().toString());
//            wxGetPayInfoQO.setThisIp(TcpipUtil.getClientRealIp(request));
//
//            Jedis jedis = redisService.jedis();
//            String accessToken = jedis.get(WexinAccessToken);
//            if(accessToken == null){
//               log.error("accessToken已失效");
//            }else {
//                String wx_openId = jedis.get(accessToken);
//                wxGetPayInfoQO.setOpenId(wx_openId);
//            }
//            String json = wxPayService.wxGetPayInfo(wxGetPayInfoQO);
//            return Result.success("ok",json);
//        } catch (Exception e) {
//            log.error("获取支付信息失败",e);
//            return Result.error("fail");
//        }
//    }
//
//    /***
//     * 支付宝支付
//     */
//    @RequestMapping("/aliPay.html")
//    public Result aliPay(String orderId, HttpServletRequest request, HttpServletResponse response){
//        try {
//            //查询订单信息
//            Order_main order_main = orderMainService.fetch(orderId);
//            WxGetPayInfoQO wxGetPayInfoQO = new WxGetPayInfoQO();
//            wxGetPayInfoQO.setOut_trade_no(orderId);
//            wxGetPayInfoQO.setProductBody("body");
//            wxGetPayInfoQO.setProductId(order_main.getAccountId());
//            wxGetPayInfoQO.setTotal_fee(order_main.getPayMoney().toString());
//            wxGetPayInfoQO.setThisIp(TcpipUtil.getClientRealIp(request));
//            AliPayFromQO aliPayFromQO = new AliPayFromQO();
//            aliPayFromQO.setOut_trade_no(orderId);
//            aliPayFromQO.setProductBody("黏土商城产品订单支付");
//            aliPayFromQO.setSubject("黏土商城产品订单支付");
//            double orderPayMoney = order_main.getPayMoney();
//            double payMoney = CalculateUtils.div(orderPayMoney,100,2);
//            aliPayFromQO.setTotal_amount(payMoney+"");
//            String form = alipayService.aliGetPayFrom(aliPayFromQO);
//            response.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
//            response.getWriter().write(form);//直接将完整的表单html输出到页面
//            response.getWriter().flush();
//            response.getWriter().close();
//        } catch (Exception e) {
//            log.error("获取支付信息失败",e);
//            return Result.error("fail");
//        }
//        return null;
//    }
//
//    /***
//     * 支付宝H5支付回调通知地址
//     */
//    @RequestMapping("/aliNotify.html")
//    @SJson
//    public String aliNotify(HttpServletRequest request){
//        try {
//            //获取支付宝POST过来反馈信息
//            Map<String,String> params = new HashMap<String,String>();
//            Map requestParams = request.getParameterMap();
//            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
//                String name = (String) iter.next();
//                String[] values = (String[]) requestParams.get(name);
//                String valueStr = "";
//                for (int i = 0; i < values.length; i++) {
//                    valueStr = (i == values.length - 1) ? valueStr + values[i]
//                            : valueStr + values[i] + ",";
//                }
//                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
//                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
//                params.put(name, valueStr);
//            }
//            log.info("支付宝回调所接收参数："+ JSON.toJSONString(params));
//            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
//            //商户订单号
//            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
//            //支付宝交易号
//            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
//            //交易状态
//            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
//            //支付金额
//            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");
//
//            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
//            //计算得出通知验证结果
//            //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
//            boolean verify_result = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");
//
//            //1.校验验签
//            if(verify_result){//验证成功
//                //请在这里加上商户的业务逻辑程序代码
//                //2.查询订单明细
//                Order_main order_main = orderMainService.fetch(out_trade_no);
//                if(order_main==null){
//                    log.error("订单不存在："+out_trade_no);
//                    return "fail";
//                }
//                //3.校验支付金额
////
////                if(total_amount){
////
////                }
//                //4.校验订单状态
//                if(OrderPayStatusEnum.NO.getKey()!=order_main.getPayStatus()){
//                    log.error("当前订单状态非待支付状态："+out_trade_no);
//                    return "fail";
//                }
//
//                //5.更新订单数据
//                order_main.setPayType(OrderPayTypeEnum.ALIPAY.getKey());
//                order_main.setPayStatus(OrderPayStatusEnum.PAYALL.getKey());
//                orderMainService.update(order_main);
//                return "success";	//请不要修改或删除
//            }else{//验证失败
//                log.error("支付宝回调验签失败:"+out_trade_no);
//                return "fail";
//            }
//        } catch (Exception e) {
//            log.error("回调时发生异常",e);
//            return "fail";
//        }
//    }
//
//
//    /***
//     * 微信H5支付回调通知地址
//     */
//    @RequestMapping("/wxNotify.html")
//    @SJson
//    public String wxNotify(HttpServletRequest request){
//        try {
//            //查询订单信息
//            Map<String,String> params = HttpRequestUtil.getRequestParameters(request.getParameterMap(),false);
//            return "<xml>\n" +
//                    "\n" +
//                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
//                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
//                    "</xml>";
//        } catch (Exception e) {
//            log.error("获取支付信息失败",e);
//            return Result.error("fail").toString();
//        }
//    }
//
//
//
//
//
//
//
//}
