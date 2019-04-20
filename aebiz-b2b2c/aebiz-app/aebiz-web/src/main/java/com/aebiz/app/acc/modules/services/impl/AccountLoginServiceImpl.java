package com.aebiz.app.acc.modules.services.impl;

import com.aebiz.app.acc.modules.models.Account_info;
import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountInfoService;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.member.modules.services.MemberCartService;
import com.aebiz.baseframework.base.service.BaseServiceImpl;
import com.aebiz.app.acc.modules.models.Account_login;
import com.aebiz.app.acc.modules.services.AccountLoginService;
import com.aebiz.commons.utils.DateUtil;
import com.aebiz.commons.utils.UserAgentUtils;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.dao.Dao;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class AccountLoginServiceImpl extends BaseServiceImpl<Account_login> implements AccountLoginService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }

    @Autowired
    public MemberCartService memberCartService;

    @Autowired
    public AccountUserService accountUserService;

    @Autowired
    public AccountInfoService accountInfoService;

    @Override
    @Transactional
    public void doLogin(String accountId, String cookieSkuStr, String cookieNumStr, Account_login accountLogin) {
        this.insert(accountLogin);
        //同步购物车数据
        memberCartService.synchronizeCart(cookieSkuStr,cookieNumStr, accountId);
    }


    @Override
    @Transactional
    public void memberRegister(Account_user accountUser) {
        //注册
        /*账户信息表添加一条记录*/
        Account_info accountInfo = new Account_info();
        String name=accountUser.getMobile();
        accountInfo.setName(name);//前台上传的名称字段用mobile字段来接收
        accountInfo.setNickname(name);

        accountInfo = accountInfoService.insert(accountInfo);

        // 获取账户id
        String accountId = accountInfo.getId();

        /*密码加密*/
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        String salt = rng.nextBytes().toBase64();
        String hashedPasswordBase64 = new Sha256Hash(accountUser.getPassword(), salt, 1024).toBase64();
        accountUser.setPassword(hashedPasswordBase64);
        accountUser.setSalt(salt);// 设置密码盐
        accountUser.setPasswordStrength(1);
        accountUser.setMobile(accountUser.getMobile());
        accountUser.setAccountId(accountId);
        accountUserService.insert(accountUser);
    }
    @Override
    public void login(HttpServletRequest request, String accountId, String loginType) {
        //登录日志
        Account_login loginLog = new Account_login();
        loginLog.setAccountId(accountId);
        loginLog.setIp(Lang.getIP(request));
        loginLog.setLoginAt(DateUtil.getTime(new Date()));

        // 已知登录类型有2种:member--会员登录,store--商户/经销商登录
        loginLog.setLoginType(loginType);

        //获取操作系统
        OperatingSystem operatingSystem = UserAgentUtils.getOperatingSystem(request);
        if (operatingSystem != null) {
            //如果操作系统对象不为空，获取名称,类型
            loginLog.setClientName(operatingSystem.getName());
            loginLog.setClientType(operatingSystem.getDeviceType().getName());
        }

        //获取浏览器
        Browser browser = UserAgentUtils.getBrowser(request);
        if (browser != null) {
            //如果浏览器对象不为空，获取名称
            loginLog.setClientBrowser(browser.getName());
        }

        //记录登录日志
        this.insert(loginLog);
    }
}
