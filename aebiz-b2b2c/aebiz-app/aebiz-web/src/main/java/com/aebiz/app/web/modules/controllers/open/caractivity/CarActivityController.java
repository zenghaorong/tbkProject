package com.aebiz.app.web.modules.controllers.open.caractivity;

import com.aebiz.app.acc.modules.models.Account_info;
import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountInfoService;
import com.aebiz.app.acc.modules.services.AccountLoginService;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.app.member.modules.models.Member_coupon;
import com.aebiz.app.member.modules.services.MemberCouponService;
import com.aebiz.app.member.modules.services.MemberRegisterService;
import com.aebiz.app.msg.modules.services.CommMsgService;
import com.aebiz.app.sales.modules.models.Activity_coupon;
import com.aebiz.app.sales.modules.models.Sales_coupon;
import com.aebiz.app.sales.modules.services.ActivityCouponService;
import com.aebiz.app.sales.modules.services.SalesCouponService;
import com.aebiz.app.store.modules.models.Store_activity;
import com.aebiz.app.store.modules.services.StoreActivityService;
import com.aebiz.app.sys.modules.models.Sys_dict;
import com.aebiz.app.sys.modules.services.SysDictService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.Pagination;
import com.aebiz.baseframework.redis.RedisService;
import com.aebiz.baseframework.view.annotation.SJson;
import com.aebiz.commons.utils.DateUtil;
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

import java.util.*;

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
    private ActivityCouponService activityCouponService;
    @Autowired
    private CommMsgService commMsgService;
    @Autowired
    private MemberIntegralService memberIntegralService;
    @Autowired
    private SysDictService sysDictService;
    @Autowired
    private AccountInfoService accountInfoService;

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
                //推荐人编号不为空给推荐人增加积分
                try{
                    memberIntegralService.saveMemberIntegral(storeId,"commentIntegral",
                            7,sourceAccountId);
                }catch (Exception e){
                    log.error("给推荐人增加积分异常",e);
                    e.printStackTrace();
                }
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
    public Result getReceiveCode(@RequestParam("mobile") String mobile,String couponId) {
        try {

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

            log.info("获取领取券验证码手机号："+mobile);
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
            cnd.and("disabled","=",false);
            cnd.and("startTime","<=", DateUtil.getNowTime());
            cnd.and("endTime",">", DateUtil.getNowTime());
            cnd.desc("index");
            Pagination pagination = storeActivityService.listPage(page,limit,cnd,"^(id|name|listImg)$");
            return Result.success("ok",pagination);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("验证码获取失败");
        }
    }

    /**
     * 获取活动详情
     * @param activityId
     * @return
     */
    @RequestMapping("getActivityById")
    @SJson
    public Result getActivityById(String activityId) {
        try {
            Map<String,Object> map = new HashMap<>();
            Cnd cnd = Cnd.NEW();
            cnd.and("activityId","=",activityId);
            Store_activity store_activity = storeActivityService.fetch(activityId);
            String timeStr = "";
            String timeStrCha = "";
            if(store_activity.getStartTime()> DateUtil.getNowTime()){
                timeStr = "未开始";
            }
            if(store_activity.getStartTime()<= DateUtil.getNowTime() && store_activity.getEndTime()>DateUtil.getNowTime()){
                timeStr = "已开始";
                //获取相差时间
                timeStrCha = DateUtil.getDatePoor( DateUtil.getNowTime(),store_activity.getStartTime());
            }
            if(store_activity.getEndTime()< DateUtil.getNowTime()){
                timeStr = "已结束";
            }
            map.put("store_activity",store_activity);
            map.put("timeStr",timeStr);
            map.put("timeStrCha",timeStrCha);
            return Result.success("ok",map);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("获取活动详情获取失败");
        }
    }

    /**
     * 获取活动下优惠券
     * @param activityId
     * @return
     */
    @RequestMapping("getActivityCoupon")
    @SJson
    public Result getActivityCoupon(String activityId) {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("activityId","=",activityId);
            List<Activity_coupon> activity_couponList = activityCouponService.query(cnd);
            List<Sales_coupon> salesCouponList = new ArrayList<>();
            for (Activity_coupon m:activity_couponList) {
                Sales_coupon salesCoupon = salesCouponService.fetch(m.getCouponId());
                salesCouponList.add(salesCoupon);
            }
            return Result.success("ok",salesCouponList);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("获取活动下优惠券获取失败");
        }
    }

    /**
     * 获取活动详情下统计数量值
     * @return
     */
    @RequestMapping("getActivityNum")
    @SJson
    public Result getActivityNum(String storeId) {
        try {
            Map<String,Object> map = new HashMap<>();
            // 已报名用户数
            Sys_dict sys_dict_ybmyhs = sysDictService.getSysDictByCode("ybmyhs",storeId);
            map.put("ybmyhs",sys_dict_ybmyhs.getValue());

            // 已分享人数
            Sys_dict sys_dict_yfxrs = sysDictService.getSysDictByCode("yfxrs",storeId);
            map.put("yfxrs",sys_dict_yfxrs.getValue());

            //  剩余礼品数
            Sys_dict sys_dict_sylps = sysDictService.getSysDictByCode("sylps",storeId);
            map.put("sylps",sys_dict_sylps.getValue());

            // 已浏览人数
            Sys_dict sys_dict_yllrs = sysDictService.getSysDictByCode("yllrs",storeId);
            map.put("yllrs",sys_dict_yllrs.getValue());

            //  参与人数
            Sys_dict sys_dict_cyrs = sysDictService.getSysDictByCode("cyrs",storeId);
            map.put("cyrs",sys_dict_cyrs.getValue());

            return Result.success("ok",map);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("获取活动下优惠券获取失败");
        }
    }

    /**
     * 获取经纪人排行榜
     * @return
     */
    @RequestMapping("getBrokerTop")
    @SJson
    public Result getBrokerTop(String storeId,int page, int limit) {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("storeId","=",storeId);
            cnd.and("userType","=","member");
//            cnd.and("num",">",0);
            cnd.desc("num");
            Pagination pagination = accountInfoService.listPage(page,limit,cnd);

            return Result.success("ok",pagination.getList());
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("获取活动下优惠券获取失败");
        }
    }

    /**
     * 获取我的优惠券
     * @return
     */
    @RequestMapping("getMyCoupon")
    @SJson
    public Result getMyCoupon(String mobile,String storeId) {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("storeId","=",storeId);
            cnd.and("mobile","=",mobile);
            List<Member_coupon> memberCouponList = memberCouponService.query(cnd);
            List<Map<String,Object>> mapList = new ArrayList<>();
            for (Member_coupon m:memberCouponList) {
                if(m.getCouponId() == null || m.getActivityId() == null){
                    continue;
                }
                Sales_coupon salesCoupon = salesCouponService.fetch(m.getCouponId());
                if(salesCoupon.isDisabled()){
                    //已禁用
                    continue;
                }
                Store_activity activity = storeActivityService.fetch(m.getActivityId());
                if(activity.isDisabled()){
                    //已禁用
                    continue;
                }
                if(DateUtil.getNowTime() > activity.getEndTime()){
                    //活动已过期
                    continue;
                }
                Map<String,Object> map = new HashMap<>();
                map.put("name",salesCoupon.getName());
                map.put("value",salesCoupon.getValue());
                map.put("code",m.getCode());
                if(salesCoupon.getEndTimeStr()!=null){
                    map.put("endTime",salesCoupon.getEndTimeStr().substring(0,10));
                }
                mapList.add(map);
            }

            return Result.success("ok",mapList);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("获取活动下优惠券获取失败");
        }
    }


}
