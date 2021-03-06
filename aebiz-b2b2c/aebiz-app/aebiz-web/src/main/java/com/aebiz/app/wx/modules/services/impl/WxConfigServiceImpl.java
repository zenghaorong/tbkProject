package com.aebiz.app.wx.modules.services.impl;

import com.aebiz.app.dec.commons.utils.CommonUtil;
import com.aebiz.app.web.commons.base.Globals;
import com.aebiz.baseframework.base.service.BaseServiceImpl;
import com.aebiz.app.wx.modules.models.Wx_config;
import com.aebiz.app.wx.modules.services.WxConfigService;
import com.aebiz.baseframework.redis.RedisService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.weixin.at.impl.DaoAccessTokenStore;
import org.nutz.weixin.at.impl.RedisAccessTokenStore;
import org.nutz.weixin.impl.WxApi2Impl;
import org.nutz.weixin.spi.WxApi2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@CacheConfig(cacheNames = "wxCache")
public class WxConfigServiceImpl extends BaseServiceImpl<Wx_config> implements WxConfigService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PropertiesProxy config;

    private static final Log logger = Logs.get();

    //redis缓存WexinAccessToken唯一key
    private static final String WexinAccessToken="WexinAccessToken_key";

    //redis缓存Wexinjsapi_ticket唯一key
    private static final String WexinJsapiTicket="Wexin_jsapi_ticket_key";

    //redis缓存WexinAccessToken唯一key-获取OpenId使用
    private static final String WexinAccessTokenOpenId="WexinAccessToken_openId_key";

    //redis缓存WexinAccessToken唯一key-获取OpenId使用
    private static final String WexinAccessTokenOpenId2="WexinAccessToken_openId_key2";

    private static int EXPIRED_SECONDS = 7100;//不到7200秒过期就去请求


    public synchronized WxApi2 getWxApi2(String wxid) {
        WxApi2Impl wxApi2 = Globals.WxMap.get(wxid);
        if (wxApi2 == null) {
            Wx_config appInfo = this.fetch(Cnd.where("id", "=", wxid));
            RedisAccessTokenStore redisAccessTokenStore = new RedisAccessTokenStore();
            redisAccessTokenStore.setTokenKey("data:wx_api_token_" + wxid);
            redisAccessTokenStore.setJedisPool(jedisPool);
            wxApi2 = new WxApi2Impl();
            wxApi2.setAppid(appInfo.getAppid());
            wxApi2.setAppsecret(appInfo.getAppsecret());
            wxApi2.setEncodingAesKey(appInfo.getEncodingAESKey());
            wxApi2.setToken(appInfo.getToken());
            wxApi2.setAccessTokenStore(redisAccessTokenStore);
            Globals.WxMap.put(wxid, wxApi2);
        }
        return wxApi2;
    }

    @Override
    public String getWxApiAccessToken(boolean fetchNew,String code) {
        try (Jedis jedis = redisService.jedis()) {
            String appId = config.get("wx.pay.AppID");
            String secret = config.get("wx.pay.AppSecret");
            String accessToken = jedis.get(WexinAccessToken);
            JSONObject json = new JSONObject();
            if (accessToken == null || fetchNew) {
                String aQs = "?appid="+appId+"&secret="+secret+"&grant_type=client_credential";
                //用code取accessToken
                String result = CommonUtil.httpCallGet("https://api.weixin.qq.com/cgi-bin/token" + aQs);
                if (result == null || result.length() == 0 || !result.startsWith("{")) {
                    logger.error("获取微信接口access_token错误，返回非json数据，result：" + result);
                    return null;
                }
                 json = JSON.parseObject(result);
                String expires_in = json.getString("expires_in");
                String errorCode = json.getString("errcode");
                if (expires_in != null && !"7200".equals(expires_in)) {
                    if (json.getString("errmsg") != null) {
                        logger.error("获取微信接口access_token错误：" + errorCode + "::" + json.getString("errmsg"));
                        return null;
                    }
                }
                accessToken = json.getString("access_token");
                if (accessToken.length() == 0) {
                    logger.error("获取微信接口access_token为空：" + errorCode + "::" + json.getString("errmsg"));
                    return null;
                }
                jedis.set(WexinAccessToken, accessToken);
                jedis.expire(WexinAccessToken, EXPIRED_SECONDS);
            }
            return accessToken;
        }catch (Exception e){
            logger.error("获取微信access_token发生异常",e);
            return null;
        }
    }

    /**
     * 获取微信jsapi_ticket
     * @return
     * @return JSONObject
     * @author tyg
     * @date   2019年5月8日下午7:18:59
     */
    @Override
    public String getJsapiTicket(boolean fetchNew,String access_token) {
        try (Jedis jedis = redisService.jedis()) {
            String jsapiTicket = jedis.get(WexinJsapiTicket);
            JSONObject json = new JSONObject();
            if (jsapiTicket == null || fetchNew) {
                //用accessToken获取JsapiTicket票据
                String result = CommonUtil.httpCallGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+access_token+"&type=jsapi");
                if (result == null || result.length() == 0 || !result.startsWith("{")) {
                    logger.error("获取微信jsapi_ticket错误，返回非json数据，result：" + result);
                    return null;
                }
                json = JSON.parseObject(result);
                String expires_in = json.getString("expires_in");
                String errorCode = json.getString("errcode");
                if (expires_in != null && !"7200".equals(expires_in)) {
                    if (json.getString("errmsg") != null) {
                        logger.error("获取微信jsapi_ticket错误：" + errorCode + "::" + json.getString("errmsg"));
                        return null;
                    }
                }
                jsapiTicket = json.getString("ticket");
                if (jsapiTicket.length() == 0) {
                    logger.error("获取微信jsapi_ticke为空：" + errorCode + "::" + json.getString("errmsg"));
                    return null;
                }
                jedis.set(WexinJsapiTicket, jsapiTicket);
                jedis.expire(WexinJsapiTicket, EXPIRED_SECONDS);
            }
            return jsapiTicket;
        }catch (Exception e){
            logger.error("获取微信access_token发生异常",e);
            return null;
        }
    }


    @Override
    public String getWxApiAccessTokenAndOpenId(boolean fetchNew,String code) {
        try (Jedis jedis = redisService.jedis()) {
            String appId = config.get("wx.pay.AppID");
            String secret = config.get("wx.pay.AppSecret");
            logger.info("WexinAccessTokenOpenId+code值KEY是："+WexinAccessTokenOpenId);
            String accessToken = jedis.get(WexinAccessTokenOpenId);
            String openid = jedis.get(WexinAccessTokenOpenId2);
            JSONObject json = new JSONObject();
            json.put("access_token",accessToken);
            json.put("openid",openid);
            logger.info("走redis缓存取出的微信数据"+json.toJSONString());
            if (accessToken == null || fetchNew) {
                String aQs = "?appid="+appId+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";
                //用code取accessToken
                String result = CommonUtil.httpCallGet("https://api.weixin.qq.com/sns/oauth2/access_token" + aQs);
                if (result == null || result.length() == 0 || !result.startsWith("{")) {
                    logger.error("获取微信接口access_token错误，返回非json数据，result：" + result);
                    return null;
                }
                json = JSON.parseObject(result);
                logger.info("微信接口直接返回数据："+json.toJSONString());
                String expires_in = json.getString("expires_in");
                String errorCode = json.getString("errcode");
                if (expires_in != null && !"7200".equals(expires_in)) {
                    if (json.getString("errmsg") != null) {
                        logger.error("获取微信接口access_token错误：" + errorCode + "::" + json.getString("errmsg"));
                        return null;
                    }
                }
                accessToken = json.getString("access_token");
                if (accessToken.length() == 0) {
                    logger.error("获取微信接口access_token为空：" + errorCode + "::" + json.getString("errmsg"));
                    return null;
                }
                jedis.set(WexinAccessTokenOpenId, accessToken);
                jedis.expire(WexinAccessTokenOpenId, EXPIRED_SECONDS);
                jedis.set(WexinAccessTokenOpenId2, json.getString("openid"));
                jedis.expire(WexinAccessTokenOpenId2, EXPIRED_SECONDS);
            }
            return json.toJSONString();
        }catch (Exception e){
            logger.error("获取微信access_token发生异常",e);
            return null;
        }
    }

    @Override
    public String getWxApiUserinfo() {
//        String access_token = this.getWxApiAccessToken(false);
//        String aQs = "?grant_type=client_credential&appid=" + appId + "&secret=" + secret;
        //用access_token取accessToken
//        String result = CommonUtil.httpCallGet("https://api.weixin.qq.com/cgi-bin/token" + aQs);
        return null;
    }
}
