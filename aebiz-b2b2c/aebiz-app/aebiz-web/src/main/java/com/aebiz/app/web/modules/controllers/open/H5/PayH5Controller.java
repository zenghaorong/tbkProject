package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.alipay.modules.models.AliPayFromQO;
import com.aebiz.app.alipay.modules.models.AlipayConfig;
import com.aebiz.app.alipay.modules.service.AlipayService;
import com.aebiz.app.order.modules.models.Order_goods;
import com.aebiz.app.order.modules.models.Order_main;
import com.aebiz.app.order.modules.models.em.OrderTypeEnum;
import com.aebiz.app.order.modules.services.OrderMainService;
import com.aebiz.app.web.commons.utils.CalculateUtils;
import com.aebiz.app.web.commons.utils.HttpRequestUtil;
import com.aebiz.app.web.commons.utils.TcpipUtil;
import com.aebiz.app.wx.modules.models.WxGetPayInfoQO;
import com.aebiz.app.wx.modules.services.WxPayService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.redis.RedisService;
import com.aebiz.baseframework.view.annotation.SJson;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/22  9:48
 * @Description: 支付相关控制器
 */
@Controller
@RequestMapping("/open/h5/pay")
public class PayH5Controller {

    private static final Log log = Logs.get();

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private OrderMainService orderMainService;

    @Autowired
    private RedisService redisService;

    private static final String WexinAccessToken="WexinAccessToken_key";



    /***
     * 微信H5支付调用控制层
     */
    @RequestMapping("/weiXinH5Pay.html")
    @SJson
    public Result weiXinH5Pay(String orderId,HttpServletRequest request){
        try {
            //查询订单信息
            Order_main order_main = orderMainService.fetch(orderId);
            WxGetPayInfoQO wxGetPayInfoQO = new WxGetPayInfoQO();
            wxGetPayInfoQO.setOut_trade_no(orderId);
            wxGetPayInfoQO.setProductBody("黏土商城产品-订单支付");
            wxGetPayInfoQO.setProductId(order_main.getAccountId());
            wxGetPayInfoQO.setTotal_fee(order_main.getPayMoney().toString());
            wxGetPayInfoQO.setThisIp(TcpipUtil.getClientRealIp(request));
            String json=wxPayService.wxGetPayInfoH5(wxGetPayInfoQO);
            return Result.success("ok",json);
        } catch (Exception e) {
            log.error("获取支付信息失败",e);
            return Result.error("fail");
        }
    }

    /***
     * 微信jsapi支付调用控制层
     */
    @RequestMapping("/weiXinJsapiPay.html")
    @SJson
    public Result weiXinJsapiPay(String orderId,HttpServletRequest request){
        try {
            //查询订单信息
            Order_main order_main = orderMainService.fetch(orderId);
            WxGetPayInfoQO wxGetPayInfoQO = new WxGetPayInfoQO();
            wxGetPayInfoQO.setOut_trade_no(orderId);
            wxGetPayInfoQO.setProductBody("body");
            wxGetPayInfoQO.setProductId(order_main.getAccountId());
            wxGetPayInfoQO.setTotal_fee(order_main.getPayMoney().toString());
            wxGetPayInfoQO.setThisIp(TcpipUtil.getClientRealIp(request));

            Jedis jedis = redisService.jedis();
            String accessToken = jedis.get(WexinAccessToken);
            if(accessToken == null){
               log.error("accessToken已失效");
            }else {
                String wx_openId = jedis.get(accessToken);
                wxGetPayInfoQO.setOpenId(wx_openId);
            }
            String json = wxPayService.wxGetPayInfo(wxGetPayInfoQO);
            return Result.success("ok",json);
        } catch (Exception e) {
            log.error("获取支付信息失败",e);
            return Result.error("fail");
        }
    }

    /***
     * 支付宝支付
     */
    @RequestMapping("/aliPay.html")
    public Result aliPay(String orderId, HttpServletRequest request, HttpServletResponse response){
        try {
            //查询订单信息
            Order_main order_main = orderMainService.fetch(orderId);
            WxGetPayInfoQO wxGetPayInfoQO = new WxGetPayInfoQO();
            wxGetPayInfoQO.setOut_trade_no(orderId);
            wxGetPayInfoQO.setProductBody("body");
            wxGetPayInfoQO.setProductId(order_main.getAccountId());
            wxGetPayInfoQO.setTotal_fee(order_main.getPayMoney().toString());
            wxGetPayInfoQO.setThisIp(TcpipUtil.getClientRealIp(request));
            AliPayFromQO aliPayFromQO = new AliPayFromQO();
            aliPayFromQO.setOut_trade_no(orderId);
            aliPayFromQO.setProductBody("黏土商城产品订单支付");
            aliPayFromQO.setSubject("黏土商城产品订单支付");
            double orderPayMoney = order_main.getPayMoney();
            double payMoney = CalculateUtils.div(orderPayMoney,100,2);
            aliPayFromQO.setTotal_amount(payMoney+"");
            String form = alipayService.aliGetPayFrom(aliPayFromQO);
            response.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
            response.getWriter().write(form);//直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
        } catch (Exception e) {
            log.error("获取支付信息失败",e);
            return Result.error("fail");
        }
        return null;
    }


    /***
     * 微信H5支付回调通知地址
     */
    @RequestMapping("/wxNotify.html")
    @SJson
    public String wxNotify(HttpServletRequest request){
        try {
            //查询订单信息
            Map<String,String> params = HttpRequestUtil.getRequestParameters(request.getParameterMap(),false);
            return "<xml>\n" +
                    "\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        } catch (Exception e) {
            log.error("获取支付信息失败",e);
            return Result.error("fail").toString();
        }
    }

    /***
     * 支付宝H5支付回调通知地址
     */
    @RequestMapping("/aliNotify.html")
    @SJson
    public String aliNotify(HttpServletRequest request){
        try {
            //查询订单信息
            Map<String,String> params = HttpRequestUtil.getRequestParameters(request.getParameterMap(),false);
            return "<xml>\n" +
                    "\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        } catch (Exception e) {
            log.error("获取支付信息失败",e);
            return Result.error("fail").toString();
        }
    }


}
