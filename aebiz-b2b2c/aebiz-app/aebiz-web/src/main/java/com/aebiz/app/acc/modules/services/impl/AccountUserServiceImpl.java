package com.aebiz.app.acc.modules.services.impl;

import com.aebiz.app.utils.modules.services.SmsService;
import com.aebiz.baseframework.base.service.BaseServiceImpl;
import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.baseframework.redis.RedisService;
import com.alibaba.fastjson.JSONObject;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

@Service
@CacheConfig(cacheNames = "accCache")
public class AccountUserServiceImpl extends BaseServiceImpl<Account_user> implements AccountUserService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private SmsService smsService;

    private static final String session_login_key="session_login_key_yd_sms";

    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }

    /**
     * 通过accountId从缓存取用户数据
     *
     * @param accountId
     * @return
     */
    public Account_user getAccount(String accountId) {
        return this.fetch(Cnd.where("accountId", "=", accountId));
    }

    /**
     * 通过loginname从缓存取用户数据
     *
     * @param loginname
     * @return
     */
    @Cacheable(key = "'LOGIN'+#loginname")
    public Account_user getAccountByLoginname(String loginname) {
        return this.fetch(Cnd.where("loginname", "=", loginname).or("email", "=", loginname).or("mobile", "=", loginname));
    }

    /**
     * 清除帐号缓存,可能存在用户修改登录名,导致旧登录缓存无法清除掉,所以accCache要设置失效时间
     * @param accountId
     */
    @Async
    public void clearAccount(String accountId) {
        this.clearGetAccount(accountId);
        Account_user accountUser = this.getAccount(accountId);
        this.clearGetAccountByLoginname(accountUser.getLoginname());
        this.clearGetAccountByLoginname(accountUser.getEmail());
        this.clearGetAccountByLoginname(accountUser.getMobile());
    }

    @CacheEvict(key = "'ACC'+#accountId")
    public void clearGetAccount(String accountId) {

    }

    @CacheEvict(key = "'LOGIN'+#loginname")
    public void clearGetAccountByLoginname(String loginname) {

    }


    @Override
    public boolean sendMsg(String content, String mobile) {
        try (Jedis jedis = redisService.jedis()) {

            String sndJson = smsService.sendMessages(content,mobile);
            JSONObject jsonObject = (JSONObject) com.alibaba.fastjson.JSON.parseObject(sndJson);
            String sendStatus = jsonObject.getString("status");
            if("-1".equals(sendStatus)){
                //获取登录session
                String loginJSON = smsService.SmsYdLogin();
                JSONObject jsonObjectLogin = com.alibaba.fastjson.JSON.parseObject(loginJSON);
                String loginStatus = jsonObjectLogin.getString("status");
                if("0".equals(loginStatus)){ //调用成功
                    String sessionId = jsonObjectLogin.getString("sessionId");
                    jedis.set(session_login_key,sessionId);
                    smsService.sendMessages(content,mobile);
                }
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
