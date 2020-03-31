package com.aebiz.app.member.modules.services.impl;

import com.aebiz.app.acc.modules.models.Account_info;
import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountInfoService;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.integral.modules.models.Member_Integral;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.app.member.modules.models.Member_account;
import com.aebiz.app.member.modules.models.Member_level;
import com.aebiz.app.member.modules.models.Member_type;
import com.aebiz.app.member.modules.models.Member_user;
import com.aebiz.app.member.modules.services.*;
import com.aebiz.app.msg.modules.models.Msg_conf_sms;
import com.aebiz.app.msg.modules.models.Msg_conf_sms_tpl;
import com.aebiz.app.shop.modules.services.ShopAreaService;
import com.aebiz.baseframework.base.service.BaseServiceImpl;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Service
public class MemberRegisterServiceImpl extends BaseServiceImpl<Member_user> implements MemberRegisterService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }
    @Autowired
    private MemberUserService memberUserService;
    @Autowired
    private AccountUserService accountUserService;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private MemberTypeService memberTypeService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    private MemberIntegralService memberIntegralService;

    private static final Log log = Logs.get();



    @Override
    @Transactional
    public void memberRegister(String mobile, String password,String username,String passwordStrength) {
        /*账户信息表添加一条记录*/
        Account_info accountInfo = new Account_info();
        String nickName = "xcy_"+getStringRandom(4);
        accountInfo.setNickname(nickName);
        accountInfo = accountInfoService.insert(accountInfo);

        // 获取账户id
        String accountId = accountInfo.getId();

        /*账户用户表添加一条记录*/
        Account_user accountUser = new Account_user();
        accountUser.setAccountId(accountId);
        accountUser.setMobile(mobile);
        accountUser.setLoginname(username);
        /*密码加密*/
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        String salt = rng.nextBytes().toBase64();
        String hashedPasswordBase64 = new Sha256Hash(password, salt, 1024).toBase64();
        accountUser.setPassword(hashedPasswordBase64);
        accountUser.setSalt(salt);// 设置密码盐
        accountUser.setPasswordStrength(Integer.parseInt(passwordStrength));
        accountUserService.insert(accountUser);

        /*会员账户表添加一条记录*/
        Member_account memberAccount = new Member_account();
        memberAccount.setAccountId(accountId);
        memberAccountService.insert(memberAccount);

        /*会员用户表添加一条记录*/
        Member_user memberUser = new Member_user();
        memberUser.setAccountId(accountId);

        /*初始化会员类型和等级*/
        Member_type memberType = memberTypeService.fetch(Cnd.NEW().asc("id"));
        if (memberType != null) {
            Integer typeId = memberType.getId();
            if (typeId != null) {
                memberUser.setTypeId(typeId);
                // 获取该类型的默认等级
                Member_level memberLevel = memberLevelService.fetch(Cnd.where("typeId", "=", typeId).and("defaultValue", "=", "1"));
                if (memberLevel != null) {
                    String levelId = memberLevel.getId();
                    if (!Strings.isEmpty(levelId)) {
                        memberUser.setLevelId(levelId);
                    }
                }
            }
        }

        memberUserService.insert(memberUser);

    }

    @Override
    @Transactional
    public String memberRegisterWx(JSONObject jsonWxUser, String password, String passwordStrength,String storeId) {
        log.info("进入service-jsonWxUser层用户数据："+jsonWxUser.toJSONString());
        log.info("进入service-jsonWxUser-openid："+jsonWxUser.getString("openid"));
        /*账户信息表添加一条记录*/
        Account_info accountInfo = new Account_info();
        accountInfo.setNickname(jsonWxUser.getString("nickname"));
        accountInfo.setOpenId(jsonWxUser.getString("openid"));
        accountInfo.setSex(jsonWxUser.getString("sex"));
        accountInfo.setImageUrl(jsonWxUser.getString("headimgurl"));
        accountInfo.setUserType("member");
        accountInfo.setStoreId(storeId);
        accountInfo.setNum(0);
        accountInfo = accountInfoService.insert(accountInfo);

        // 获取账户id
        String accountId = accountInfo.getId();

        /*账户用户表添加一条记录*/
        Account_user accountUser = new Account_user();
        accountUser.setAccountId(accountId);
        accountUser.setLoginname(jsonWxUser.getString("nickname"));
        accountUser.setStoreId(storeId);
        accountUser.setUserType("member");
        /*密码加密*/
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        String salt = rng.nextBytes().toBase64();
        String hashedPasswordBase64 = new Sha256Hash(password, salt, 1024).toBase64();
        accountUser.setPassword(hashedPasswordBase64);
        accountUser.setSalt(salt);// 设置密码盐
        accountUser.setPasswordStrength(Integer.parseInt(passwordStrength));
        accountUserService.insert(accountUser);

        /*会员账户表添加一条记录*/
        Member_account memberAccount = new Member_account();
        memberAccount.setAccountId(accountId);
        memberAccount.setStoreId(storeId);
        memberAccountService.insert(memberAccount);

        /*会员用户表添加一条记录*/
        Member_user memberUser = new Member_user();
        memberUser.setAccountId(accountId);
        memberUser.setStoreId(storeId);
        /*初始化会员类型和等级*/
        Member_type memberType = memberTypeService.fetch(Cnd.NEW().asc("id"));
        if (memberType != null) {
            Integer typeId = memberType.getId();
            if (typeId != null) {
                memberUser.setTypeId(typeId);
                // 获取该类型的默认等级
                Member_level memberLevel = memberLevelService.fetch(Cnd.where("typeId", "=", typeId).and("defaultValue", "=", "1"));
                if (memberLevel != null) {
                    String levelId = memberLevel.getId();
                    if (!Strings.isEmpty(levelId)) {
                        memberUser.setLevelId(levelId);
                    }
                }
            }
        }

        memberUserService.insert(memberUser);

        //积分记录加一条初始数据
        Member_Integral member_integral = new Member_Integral();
        member_integral.setStoreId(storeId);
        member_integral.setUseAbleIntegral(0);
        member_integral.setTotalIntegral(0);
        member_integral.setCustomerUuid(accountId);
        memberIntegralService.insert(member_integral);
       return  accountId;
    }


    //生成随机用户名，数字和字母组成,
    public static String  getStringRandom(int length) {

        String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    public static void main(String[] args) {
        System.out.println(getStringRandom(4));
    }
}
