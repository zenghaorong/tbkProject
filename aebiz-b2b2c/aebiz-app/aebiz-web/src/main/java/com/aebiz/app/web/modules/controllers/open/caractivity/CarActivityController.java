package com.aebiz.app.web.modules.controllers.open.caractivity;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountInfoService;
import com.aebiz.app.acc.modules.services.AccountLoginService;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.member.modules.models.Member_coupon;
import com.aebiz.app.member.modules.services.MemberCouponService;
import com.aebiz.app.member.modules.services.MemberRegisterService;
import com.aebiz.app.sales.modules.models.Sales_coupon;
import com.aebiz.app.sales.modules.services.SalesCouponService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.redis.RedisService;
import com.aebiz.baseframework.view.annotation.SJson;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("open/H5/caractivity")
public class CarActivityController {


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
    private MemberCouponService memberCouponService;
    @Autowired
    private SalesCouponService salesCouponService;


    /**
     * 领取优惠劵接口
     */
    @RequestMapping("receive.html")
    @SJson
    public Result receive(String couponId){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();

            //查询优惠劵详情信息
            Sales_coupon sales_coupon = salesCouponService.fetch(couponId);
            if(sales_coupon.getSend_num()<1){
                sales_coupon.setSend_num(0);
                return Result.error(-1,"很抱歉，优惠券已经领完了！");
            }
            //查询本人优惠劵
            Cnd cndC = Cnd.NEW();
            cndC.and("couponId", "=", couponId );
            cndC.and("accountId", "=", accountUser.getAccountId());
            List<Member_coupon> member_couponList = memberCouponService.query(cndC);
            if(member_couponList!=null) {
                if (member_couponList.size()>0) {
                    return Result.error(10001,"您已领取过该优惠券");
                }
            }

            //绑定该用户
            Member_coupon member_coupon = new Member_coupon();
            member_coupon.setAccountId(accountUser.getAccountId());
            member_coupon.setCouponId(couponId);
            int codeTime=getSecondTimestamp(new Date());
            String random =  getStringRandom(4);
            member_coupon.setCode(sales_coupon.getCodeprefix()+codeTime+random);
            member_coupon.setStoreId(sales_coupon.getStoreId());
            member_coupon.setMobile(accountUser.getMobile());
            //1.自己领取
            member_coupon.setSource("1");
            //0待核销
            member_coupon.setStatus(0);
            memberCouponService.insert(member_coupon);
            sales_coupon.setSend_num(sales_coupon.getSend_num()-1);
            salesCouponService.update(sales_coupon);
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

}
