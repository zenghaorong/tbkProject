package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.cms.modules.models.Cms_video;
import com.aebiz.app.member.modules.models.Member_coupon;
import com.aebiz.app.member.modules.services.MemberCouponService;
import com.aebiz.app.sales.modules.models.Sales_coupon;
import com.aebiz.app.sales.modules.services.SalesCouponService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/10  22:02
 * @Description: 优惠劵 控制层
 */
@Controller
@RequestMapping("/open/h5/coupon")
public class CouponH5Controller {

    private static final Log log = Logs.get();

    @Autowired
    private MemberCouponService memberCouponService;
    @Autowired
    private SalesCouponService salesCouponService;

    /**
     * 订单确认页查询可用优惠劵列表
     */
    @RequestMapping("getMyOrderCoupon.html")
    @SJson
    public Result getMyOrderCoupon(String id){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", 0 );
            cnd.and("accountId", "=", accountUser.getId() );
            cnd.and("status", "=", 2);
            List<Member_coupon> member_couponList = memberCouponService.query(cnd);
            for(Member_coupon member_coupon : member_couponList){
                Sales_coupon sales_coupon = salesCouponService.fetch(member_coupon.getCouponId());
                member_coupon.setSales_coupon(sales_coupon);
            }
            return Result.success("ok",member_couponList);
        } catch (Exception e) {
            log.error("获取订单可用优惠劵异常",e);
            return Result.error("fail");
        }
    }


}
