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

    private static final String WexinAccessToken="WexinAccessToken_key";

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
                String aQs = "?appid="+appId+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";
                //用code取accessToken
                String result = CommonUtil.httpCallGet("https://api.weixin.qq.com/sns/oauth2/access_token" + aQs);
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
                jedis.set(accessToken, json.getString("openid"));
                jedis.expire(accessToken, EXPIRED_SECONDS);
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
