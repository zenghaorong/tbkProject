package com.aebiz.app.web.modules.controllers.open.caractivity;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountInfoService;
import com.aebiz.app.acc.modules.services.AccountLoginService;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.member.modules.models.Member_coupon;
import com.aebiz.app.member.modules.services.MemberCouponService;
import com.aebiz.app.member.modules.services.MemberRegisterService;
import com.aebiz.app.msg.modules.services.CommMsgService;
import com.aebiz.app.sales.modules.models.Sales_coupon;
import com.aebiz.app.sales.modules.services.SalesCouponService;
import com.aebiz.app.store.modules.services.StoreActivityService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.Pagination;
import com.aebiz.baseframework.redis.RedisService;
import com.aebiz.baseframework.view.annotation.SJson;
import com.aebiz.commons.utils.StringUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("open/H5/caractivity")
public class CarActivityController {


    private static final Log log = Logs.get();

    @Autowired
    private RedisService redisService;

    @Autowired
    private MemberCouponService memberCouponService;
    @Autowired
    private SalesCouponService salesCouponService;
    @Autowired
    private StoreActivityService storeActivityService;
    @Autowired
    private CommMsgService commMsgService;

    /**
     * 手机短信验证码前缀
     */
    private final String MOBILE_CAPTCHA = "mobile_sms_captcha_getReceiveCode";

    //获取验证码重复请求限制
    private final String MOBILE_CAPTCHA_Request_limit = "mobile_sms_captcha_Request_limit_getReceiveCode";


    /**
     * 领取优惠劵接口
     * @param couponId 券编号
     * @param mobile 手机号
     * @param captcha 验证码
     * @param userName 用户姓名
     * @param activityId 参与的活动编号
     * @param sourceAccountId 分享推荐人编号
     * @param storeId 商户编号
     * @return
     */
    @RequestMapping("receive.html")
    @SJson
    public Result receive(String couponId,String mobile,String captcha,String userName,
                          String activityId,String sourceAccountId,String storeId){

        try (Jedis jedis = redisService.jedis()) {
            if (Strings.isEmpty(mobile) || Strings.isEmpty(captcha)) {
                return Result.error("请求参数为空");
            } else {
                if (!Strings.isMobile(mobile)) {
                    return Result.error("请输入正确的手机号");
                }
            }

            if (!captcha.equals(jedis.get(MOBILE_CAPTCHA + mobile))) {
                return Result.error("验证码不正确");
            }
        }


        try {
            //查询优惠劵详情信息
            Sales_coupon sales_coupon = salesCouponService.fetch(couponId);
            //查询本人优惠劵
            Cnd cndC = Cnd.NEW();
            cndC.and("couponId", "=", couponId );
            cndC.and("mobile", "=", mobile);
            List<Member_coupon> member_couponList = memberCouponService.query(cndC);
            if(member_couponList!=null) {
                if (member_couponList.size()>0) {
                    return Result.error(10001,"您已领取过该优惠券");
                }
            }

            //绑定该用户
            Member_coupon member_coupon = new Member_coupon();
            member_coupon.setMobile(mobile);
            member_coupon.setCouponId(couponId);
            int codeTime=getSecondTimestamp(new Date());
            String random =  getStringRandom(4);
            member_coupon.setCode(sales_coupon.getCodeprefix()+codeTime+random);
            member_coupon.setStoreId(sales_coupon.getStoreId());
            member_coupon.setMobile(mobile);
            member_coupon.setUserName(userName);
            if(StringUtils.isNotEmpty(sourceAccountId)){
                member_coupon.setSourceAccountId(sourceAccountId);
                //3别人推荐的活动获得
                member_coupon.setSource("3");
            }else {
                //1.自己领取
                member_coupon.setSource("1");
            }
            //0待核销
            member_coupon.setStatus(0);
            member_coupon.setActivityId(activityId);
            member_coupon.setStoreId(storeId);
            memberCouponService.insert(member_coupon);
            return Result.success("ok");
        } catch (Exception e) {
            log.error("获取领劵中心优惠劵列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 获取精确到秒的时间戳
     * @return
     */
    public static int getSecondTimestamp(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0,length-3));
        } else {
            return 0;
        }
    }

    //生成随机数字和字母,
    public static String getStringRandom(int length) {

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

    /**
     * 获取领取券验证码
     */
    @RequestMapping("/getReceiveCode")
    @SJson
    public Result getReceiveCode(@RequestParam("mobile") String mobile) {
        try {
            log.info("获取领取券验证码："+mobile);
            String key2 = MOBILE_CAPTCHA_Request_limit + mobile;
            String expireTime2 = "60";

            String key = MOBILE_CAPTCHA + mobile;
            String expireTime = "300";
            String code = StringUtil.getRndNumber(6);
            try (Jedis jedis = redisService.jedis()) {
                if(Strings.isNotBlank(jedis.get(key2))){
                    log.info("手机号"+mobile+"60秒内请求了两次");
                    return Result.error("手机号"+mobile+"60秒内请求了两次");
                }
                //重复请求限制
                jedis.set(key2, code);
                jedis.expire(key2, Integer.valueOf(expireTime2));

                jedis.set(key, code);
                jedis.expire(key, Integer.valueOf(expireTime));

                //编写发送验证码逻辑
                commMsgService.sendMsgAly(code,mobile);
            }
            log.info("短信验证码:  " + code);
            return Result.success("ok");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("验证码获取失败");
        }
    }

    /**
     * 分页获取活动列表
     * @param storeId
     * @return
     */
    @RequestMapping("getActivityList")
    @SJson
    public Result getActivityList(String storeId,int page, int limit) {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("storeId","=",storeId);
            cnd.and("delFlag","=",false);
            cnd.desc("index");
            Pagination pagination = storeActivityService.listPage(page,limit,cnd,"^(id|name|listImg)$");
            return Result.success("ok",pagination);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("验证码获取失败");
        }
    }

}
