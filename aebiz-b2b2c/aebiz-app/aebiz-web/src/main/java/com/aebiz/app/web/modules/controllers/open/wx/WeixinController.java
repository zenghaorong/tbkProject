package com.aebiz.app.web.modules.controllers.open.wx;


import com.aebiz.app.web.commons.utils.WXPayUtil;
import com.aebiz.app.web.commons.utils.WxCardSign;
import com.aebiz.app.wx.modules.services.WxConfigService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.Pagination;
import com.aebiz.baseframework.view.annotation.SJson;
import com.alibaba.fastjson.JSON;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.web.session.HttpServletSession;
import org.nutz.dao.Cnd;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.weixin.util.Wxs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaoen on 2017-2-21
 */
@Controller
@RequestMapping("/open/weixin")
public class WeixinController {
    private static final Log log = Logs.get();
    @Autowired
    WxConfigService wxConfigService;
    @Autowired
    WxHandler wxHandler;
    @Autowired
    private PropertiesProxy config;

    public WeixinController() {
        if (log.isDebugEnabled())
            Wxs.enableDevMode(); // 开启debug模式,这样就会把接收和发送的内容统统打印,方便查看
    }

    @RequestMapping(value = {"/api", "/api/{key}"})
    public void msgIn(@PathVariable(required = true) String key, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        Wxs.handle(wxHandler, request, key).render(request, response, null);
    }

    /**
     * 获取调用微信js功能config参数 签名参数 微信分享
     * @return
     */
    @RequestMapping("getWxConfig")
    @SJson
    public Result getWxConfig(String url) {
        try {
            String appid = config.get("wx.pay.AppID");
            String nonce_str = WXPayUtil.generateNonceStr();
            String timeStamp = Calendar.getInstance().getTimeInMillis()+""; //时间戳
            String accessToken = wxConfigService.getWxApiAccessToken(false,"");
            String jsapiTicket = wxConfigService.getJsapiTicket(false,accessToken);
            Integer timestamp = (int)WXPayUtil.getCurrentTimestamp();
            Map map = new HashMap();
            map.put("url",url);
            map.put("nonceStr", nonce_str);
            map.put("timestamp",timestamp);

            WxCardSign signer = new WxCardSign();
            signer.AddData("jsapi_ticket="+jsapiTicket+"&");
            signer.AddData("noncestr="+nonce_str+"&");
            signer.AddData("timestamp="+timestamp+"&");
            signer.AddData("url="+url);
            System.out.println(signer.GetSignature());
            String signature = signer.GetSignature();

            map.put("signature", signature);
            map.put("appId",appid);
            return Result.success("ok",map);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("验证码获取失败");
        }
    }


}
