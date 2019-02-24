package com.aebiz.app.acc.modules.services.impl;

import com.aebiz.app.member.modules.services.MemberCartService;
import com.aebiz.baseframework.base.service.BaseServiceImpl;
import com.aebiz.app.acc.modules.models.Account_login;
import com.aebiz.app.acc.modules.services.AccountLoginService;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class AccountLoginServiceImpl extends BaseServiceImpl<Account_login> implements AccountLoginService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }

    @Autowired
    public MemberCartService memberCartService;

    @Override
    @Transactional
    public void doLogin(String accountId, String cookieSkuStr, String cookieNumStr, Account_login accountLogin) {
        this.insert(accountLogin);
        //同步购物车数据
        memberCartService.synchronizeCart(cookieSkuStr,cookieNumStr, accountId);
    }
}
