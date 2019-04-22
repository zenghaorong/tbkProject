package com.aebiz.app.utils.modules.services.impl;


import com.aebiz.app.utils.modules.services.SmsService;
import com.aebiz.app.web.commons.utils.HttpClientUtil;
import com.aebiz.app.web.commons.utils.HttpClientYdUtils;
import com.aebiz.baseframework.redis.RedisService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

/**
 * 短信模块接口
 */
@Service
public class SmsServiceImpl implements SmsService {

    private static final Log log = Logs.get();

    @Autowired
    private PropertiesProxy config;

    @Autowired
    private RedisService redisService;


    private static final String session_login_key="session_login_key_yd_sms";

    @Override
    public String sendMessages(String msg,String mobile) {

        Jedis jedis = redisService.jedis();

        String redisSessionId = jedis.get(session_login_key);

        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("sessionId", redisSessionId));
        data.add(new BasicNameValuePair("destAddr", mobile));
        data.add(new BasicNameValuePair("content", msg));
        String sendPostUrl = config.get("ydsms.sendPostUrl");
        String dataStr = JSON.toJSONString(data);
        log.info("发送短信请求数据："+dataStr);
        try {
            String returnStr = HttpClientYdUtils.post(sendPostUrl,data,"gbk");
            log.error("发送短信请求返回结果："+returnStr);
            return returnStr;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String SmsYdLogin() {
        String enterAccount = config.get("ydsms.enterAccount");
        String userName = config.get("ydsms.userName");
        String userPwd = config.get("ydsms.userPwd");
        String srcExtCode = config.get("ydsms.srcExtCode");
        String userType = config.get("ydsms.userType");
        String loginPostUrl = config.get("ydsms.loginPostUrl");

        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("enterAccount", enterAccount));
        data.add(new BasicNameValuePair("userName", userName));
        data.add(new BasicNameValuePair("userPwd", userPwd));
        data.add(new BasicNameValuePair("srcExtCode", srcExtCode));
        data.add(new BasicNameValuePair("userType", userType));
        String json = JSON.toJSONString(data);
        try {
            log.info("登录移动获取session请求数据："+json);
            String returnStr = HttpClientYdUtils.post(loginPostUrl,data,"gbk");
//            JSONObject jsonObject = (JSONObject) JSON.parseObject(returnStr);
            log.info("登录移动获取session返回数据："+returnStr);
            return returnStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
