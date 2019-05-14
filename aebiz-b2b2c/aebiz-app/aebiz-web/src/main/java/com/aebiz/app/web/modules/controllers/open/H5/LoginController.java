package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_info;
import com.aebiz.app.acc.modules.models.Account_login;
import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountInfoService;
import com.aebiz.app.acc.modules.services.AccountLoginService;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.app.member.modules.models.Member_user;
import com.aebiz.app.member.modules.services.MemberRegisterService;
import com.aebiz.app.member.modules.services.MemberUserService;
import com.aebiz.app.sys.modules.models.Sys_log;
import com.aebiz.app.sys.modules.services.SysApiService;
import com.aebiz.app.utils.modules.services.SmsService;
import com.aebiz.app.web.commons.base.Globals;
import com.aebiz.app.web.commons.log.annotation.SLog;
import com.aebiz.app.web.commons.shiro.token.MemberCaptchaToken;
import com.aebiz.app.web.commons.utils.CheckPasswordUtil;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.base.service.BaseService;
import com.aebiz.baseframework.redis.RedisService;
import com.aebiz.baseframework.shiro.exception.CaptchaEmptyException;
import com.aebiz.baseframework.shiro.exception.CaptchaIncorrectException;
import com.aebiz.baseframework.view.annotation.SJson;
import com.aebiz.commons.utils.*;
import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.fastjson.JSONObject;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.nutz.dao.Cnd;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.List;

/**
 * @Auther: zenghaorong
 * @Date: 2019/3/30  15:11
 * @Description: 登录 注册
 */
@Controller
@RequestMapping("open/H5/login")
public class LoginController {

    private static final Log log = Logs.get();

    @Autowired
    private AccountUserService accountUserService;

    @Autowired
    private AccountInfoService accountInfoService;

    @Autowired
    private AccountLoginService accountLoginService;

    @Autowired
    private MemberRegisterService memberRegisterService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MemberIntegralService memberIntegralService;

    @Autowired
    private SysApiService apiService;

    @Autowired
    private MemberUserService userService;

    /**
     * 手机短信验证码前缀
     */
    private final String MOBILE_CAPTCHA = "mobile_sms_captcha_";

    private static final String session_login_key="session_login_key_yd_sms";

    /**
     * token的有效期,默认是2小时
     */
    private final Integer TOKEN_EXP_TIME = 2 * 60 * 60 * 1000;

    /**
     * 应用的appId
     */
    private final String APPID = "a400da4ef4";


    @Autowired
    private SmsService smsService;

    private static final String cookieName = "cheryfsmemberRemeberMe";

    /**
     * 进入登录页
     */
    @RequestMapping("login.html")
    public String login() {
        return "pages/front/h5/niantu/login";
    }

    /**
     * 进入注册页
     */
    @RequestMapping("reg.html")
    public String reg() {
        return "pages/front/h5/niantu/reg";
    }

    /**
     * 进入忘记密码页
     */
    @RequestMapping("forgetPassword.html")
    public String forgetPassword() {
        return "pages/front/h5/niantu/forgetPassword";
    }

    /**
     * 进入修改密码页
     */
    @RequestMapping("updatePassword.html")
    public String updatePassword(HttpServletRequest request) {
        String mobile = request.getParameter("mobile");
        request.setAttribute("mobile",mobile);
        return "pages/front/h5/niantu/password";
    }
    /**
     * 进入修改昵称页
     */
    @RequestMapping("updateName.html")
    public String updateName(HttpServletRequest request) {
        String accountId = request.getParameter("accountId");
        request.setAttribute("accountId",accountId);
        return "pages/front/h5/niantu/userInfo";
    }
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @SJson
    public Object updateUserInfo(@RequestParam("accountId") String accountId, @RequestParam("nickName") String nickName, HttpServletRequest req) {
        try {
            Account_info info = accountInfoService.fetch(accountId);
            info.setNickname(nickName);
            accountInfoService.update(info);
            return Result.success("操作成功");
        } catch (Exception e) {
            return Result.error("操作失败");
        }
    }
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @SJson
    public Object update(@RequestParam("mobile") String mobile, @RequestParam("password") String password, HttpServletRequest req) {
        try {
            Account_user account_user = accountUserService.fetch(Cnd.where("mobile", "=", mobile));
            String hashedPasswordBase64 = new Sha256Hash(password, account_user.getSalt(), 1024).toBase64();
            account_user.setPassword(hashedPasswordBase64);
            accountUserService.updateIgnoreNull(account_user);
            return Result.success("操作成功");
        } catch (Exception e) {
            return Result.error("操作失败");
        }
    }

    /**
     * 会员验证码登录接口
     * @param username
     * @param captcha
     * @param request
     * @return
     */
    @RequestMapping(value = {"/doCodeLogin"}, method = RequestMethod.POST)
    @SJson
    public Object doCodeLogin(@RequestParam("mobile") String username, String captcha,
                              HttpServletRequest request,HttpServletResponse response) {

        try (Jedis jedis = redisService.jedis()) {
            if (Strings.isEmpty(username) || Strings.isEmpty(captcha)) {
                return Result.error("请求参数为空");
            } else {
                if (!Strings.isMobile(username)) {
                    return Result.error("请输入正确的手机号");
                }
            }

            if (!captcha.equals(jedis.get(MOBILE_CAPTCHA + username))) {
                return Result.error("验证码不正确");
            }

            Cnd cnd = Cnd.where("mobile", "=", username);
            Account_user accountUser = accountUserService.fetch(cnd);
            if (Lang.isEmpty(accountUser)) {
                // 如果未找到用户,就自动注册为会员,然后登录
                userService.autoRegister(username,request);
            } else if (accountUser.isDisabled() || accountUser.getDelFlag()) {
                return Result.error("该手机号对应的账户被冻结/删除");
            }
            accountUser = accountUserService.fetch(cnd);
            accountLoginService.login(request, accountUser.getAccountId(), "member");
            NutMap returnData = NutMap.NEW();
            returnData.put("token", apiService.generateToken(new Date(System.currentTimeMillis() + TOKEN_EXP_TIME), APPID));
            returnData.put("accountId", accountUser.getAccountId());
            returnData.put("accountName", accountUser.getLoginname());
            returnData.put("mobile", accountUser.getMobile());

            Subject subject = SecurityUtils.getSubject();
            ThreadContext.bind(subject);

            AuthenticationToken authenticationToken =createToken(username, accountUser.getPassword(), false, "6666", request);
            subject.login(authenticationToken);

            //设置登录
            CookieUtil.setCookie(response, "cheryfs_member_login", "true");

            Account_login accountLogin = new Account_login();
            accountLogin.setAccountId(accountUser.getAccountId());
            accountLogin.setIp(Lang.getIP(request));
            accountLogin.setLoginType("member");
            accountLogin.setLoginAt((int) (System.currentTimeMillis() / 1000));
            accountLogin.setClientType("wap");
            OperatingSystem operatingSystem = UserAgentUtils.getOperatingSystem(request);
            if (operatingSystem != null) {
                accountLogin.setClientName(operatingSystem.getName());
            }
            Browser browser = UserAgentUtils.getBrowser(request);
            if (browser != null) {
                accountLogin.setClientBrowser(browser.getName());
            }

            return Result.success("登录成功", returnData);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return Result.error(-1, "登录失败");
        }
    }


    /**
     * 会员登录接口
     * @param username
     * @param password
     * @param rememberMe
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/doLogin"}, method = RequestMethod.POST)
    @SJson
    public Object doLogin(@RequestParam("mobile") String username,
                          @RequestParam("password") String password,
                          @RequestParam(value = "rememberme", defaultValue = "0", required = false) boolean rememberMe,
                          HttpServletRequest request, HttpServletResponse response) {
        int errCount = 0;
        try {
            Subject subject = SecurityUtils.getSubject();
            ThreadContext.bind(subject);

            AuthenticationToken authenticationToken =createToken(username, password, false, "6666", request);
            subject.login(authenticationToken);



            Account_user accountUser = (Account_user) subject.getPrincipal();
            if (accountUser.isDisabled()) {
                return Result.error("此用户被冻结").addCode(3);
            }
            if (rememberMe) {
                SimpleCookie cookie = new SimpleCookie(cookieName);
                cookie.setHttpOnly(true);
                cookie.setMaxAge(31536000);
                String base64 = Base64.encodeToString(accountUser.getLoginname().getBytes());
                cookie.setValue(base64);
                cookie.saveTo(request, response);
            } else {
                SimpleCookie cookie = new SimpleCookie(cookieName);
                cookie.removeFrom(request, response);
            }

            //设置登录
            CookieUtil.setCookie(response, "cheryfs_member_login", "true");

            Account_login accountLogin = new Account_login();
            accountLogin.setAccountId(accountUser.getAccountId());
            accountLogin.setIp(Lang.getIP(request));
            accountLogin.setLoginType("member");
            accountLogin.setLoginAt((int) (System.currentTimeMillis() / 1000));
            accountLogin.setClientType("wap");
            OperatingSystem operatingSystem = UserAgentUtils.getOperatingSystem(request);
            if (operatingSystem != null) {
                accountLogin.setClientName(operatingSystem.getName());
            }
            Browser browser = UserAgentUtils.getBrowser(request);
            if (browser != null) {
                accountLogin.setClientBrowser(browser.getName());
            }
            accountLoginService.insert(accountLogin);
            return Result.success("sys.login.success");
        } catch (CaptchaIncorrectException e) {
            //自定义的验证码错误异常
            return Result.error(1, "sys.login.error.captcha");
        } catch (CaptchaEmptyException e) {
            //验证码为空
            return Result.error(2, "sys.login.error.captcha");
        } catch (LockedAccountException e) {
            return Result.error(3, "sys.login.error.locked");
        } catch (UnknownAccountException e) {
            errCount++;
            SecurityUtils.getSubject().getSession(true).setAttribute("memberErrCount", errCount);
            return Result.error(4, "sys.login.error.username");
        } catch (AuthenticationException e) {
            errCount++;
            SecurityUtils.getSubject().getSession(true).setAttribute("memberErrCount", errCount);
            return Result.error(5, "sys.login.error.password");
        } catch (Exception e) {
            errCount++;
            SecurityUtils.getSubject().getSession(true).setAttribute("memberErrCount", errCount);
            return Result.error(6, "sys.login.error.system");
        }

    }

    protected AuthenticationToken createToken(String username, String password, boolean rememberMe, String captcha, HttpServletRequest request) {
        String host = request.getRemoteHost();
        try {
            RSAPrivateKey memberPrivateKey = (RSAPrivateKey) request.getSession().getAttribute("memberPrivateKey");
            if (memberPrivateKey != null) {
                password = RSAUtil.decryptByPrivateKey(password, memberPrivateKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new MemberCaptchaToken(username, password, rememberMe, host, captcha);
    }


    /**
     * 用户注册
     */
    @RequestMapping("/doRegister")
    @SJson
    public Object doRegister(@RequestParam("mobile") String mobile,
                             String mobileCode,
                             @RequestParam("password") String password,
                             @RequestParam(value = "promotionCode",required = false) String promotionCode,
                             @RequestParam(value = "captcha",required = false) String captcha,HttpSession session,HttpServletRequest request) {
        try (Jedis jedis = redisService.jedis()) {
//            Cnd cnd = Cnd.where(USERNAME, ET, userName).and("delFlag", "=", false);
//            if (isExist(accountUserService, cnd)) {
//                return Result.error("用户名已存在");
//            }
            Cnd cnd = Cnd.where("mobile", "=", mobile).and("delFlag", "=", false);
            if (isExist(accountUserService, cnd)) {
                return Result.error("手机号已存在");
            }
            if (!captcha.equals(jedis.get(MOBILE_CAPTCHA + mobile))) {
                return Result.error("验证码不正确");
            }

            //注册
            memberRegisterService.memberRegister(mobile, password, mobile, CheckPasswordUtil.checkPassword(password).toString());
            Cnd mCnd = Cnd.NEW();
            mCnd.and("mobile","=",mobile);
            List<Account_user> userList = accountUserService.query(mCnd);
            if(userList!=null&&userList.size()>0){
                try {
                    memberIntegralService.addMemberIntegral(userList.get(0).getAccountId(),"2","1",null);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(-1, "验证码不正确");
        }
    }

    /**
     * 退出登录
     */
    @RequestMapping("logout.html")
    @SLog(description = "用户退出", methodReturn = true, type = Sys_log.TypeEnum.LOGIN)
    public void logout(HttpServletResponse response) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
//        return "redirect:/open/H5/login/login.html"; 这样写会报错
        response.sendRedirect("/open/H5/login/login.html");
    }

    /**
     * 判断是否能根据条件(cnd),查到对面表的数据(用service查)
     * 主要目的是判断某字段是否存在
     *
     * @param service 任何实现BaseService的service
     * @param cnd     查询条件
     */
    private boolean isExist(BaseService service, Cnd cnd) {
        Object obj = service.fetch(cnd);
        if (!Lang.isEmpty(obj)) {
            return true;
        }
        return false;
    }
    /**
     * 获取登录验证码
     */
    @RequestMapping("/getLoginMsgCode")
    @SJson
    public Result getLoginMsgCode(@RequestParam("mobile") String mobile) {
        try {

            Cnd cnd = Cnd.where("mobile", "=", mobile).and("delFlag", "=", false);
            if (isExist(accountUserService, cnd)) {
                return Result.error("手机号已注册");
            }

            String key = MOBILE_CAPTCHA + mobile;
            String expireTime = "300";
            String code = StringUtil.getRndNumber(6);
            try (Jedis jedis = redisService.jedis()) {
                jedis.set(key, code);
                jedis.expire(key, Integer.valueOf(expireTime));

                String content = "验证码"+code+"。本次验正码用于校验身份";
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
            }
            log.info("短信验证码:  " + code);
            return Result.success("ok");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("验证码获取失败");
        }
    }

    /**
     * 获取忘记密码验证码
     */
    @RequestMapping("/getforgetPasswordMsgCode")
    @SJson
    public Result getforgetPasswordMsgCode(@RequestParam("mobile") String mobile) {
        try {
            String key = MOBILE_CAPTCHA + mobile;
            String expireTime = "300";
            String code = StringUtil.getRndNumber(6);
            try (Jedis jedis = redisService.jedis()) {
                jedis.set(key, code);
                jedis.expire(key, Integer.valueOf(expireTime));

                String content = "验证码"+code+"。本次验正码用于校验身份";
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
            }
            log.info("短信验证码:  " + code);
            return Result.success("member.register.join.success");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("member.register.join.fail");
        }
    }

    /**
     * 重置密码
     */
    @RequestMapping("/resetPassword")
    @SJson
    public Result resetPassword(@RequestParam("mobile") String mobile,
                                @RequestParam("password") String password,
                                @RequestParam("captcha") String captcha,HttpServletRequest request) {
        try (Jedis jedis = redisService.jedis()) {
            if (Strings.isEmpty(mobile) || Strings.isEmpty(captcha) || Strings.isEmpty(password)) {
                return Result.error("请求参数为空");
            } else {
                if (!Strings.isMobile(mobile)) {
                    return Result.error("请输入正确的手机号");
                }
            }

            if (!captcha.equals(jedis.get(MOBILE_CAPTCHA + mobile))) {
                return Result.error("验证码不正确");
            }

            RandomNumberGenerator rng = new SecureRandomNumberGenerator();
            String passwordLength=CheckPasswordUtil.checkPassword(password).toString();
            String salt = rng.nextBytes().toBase64();
            String hashedPasswordBase64 = new Sha256Hash(password, salt, 1024).toBase64();

            Account_user account_user = accountUserService.getAccountByLoginname(mobile);
            account_user.setSalt(salt);// 设置密码盐
            account_user.setPassword(hashedPasswordBase64);
            account_user.setPasswordStrength(Integer.parseInt(passwordLength));
            accountUserService.update(account_user);
            return Result.success("member.register.join.success");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("member.register.join.fail");
        }
    }


}
