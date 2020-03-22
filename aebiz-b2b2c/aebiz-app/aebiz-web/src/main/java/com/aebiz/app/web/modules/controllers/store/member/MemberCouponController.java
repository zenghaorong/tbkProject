package com.aebiz.app.web.modules.controllers.store.member;


import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.cms.modules.models.Cms_article;
import com.aebiz.app.member.modules.models.Member_coupon;
import com.aebiz.app.member.modules.services.MemberCouponService;
import com.aebiz.app.sales.modules.models.Sales_coupon;
import com.aebiz.app.sales.modules.services.SalesCouponService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.datatable.DataTable;
import com.aebiz.baseframework.view.annotation.SJson;
import com.aebiz.commons.utils.DateUtil;
import com.aebiz.commons.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/store/member/coupon")
public class MemberCouponController {

    private static final Log log = Logs.get();

    @Autowired
    private MemberCouponService memberCouponService;
    @Autowired
    private SalesCouponService salesCouponService;

    @RequestMapping("")
    public String index(HttpServletRequest req) {
        //初始化会员类型
        return "pages/store/member/coupon/index";
    }

    /***
     * 获取核销优惠卷列表
     * @return
     */
    @RequestMapping("/data")
    @SJson("full")
    public Object data(DataTable dataTable,String name,String mobile,String code,String userName){
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("storeId","=", StringUtil.getStoreId());
            if(StringUtils.isNotEmpty(name)){
                Cnd cnd2 = Cnd.NEW();
                cnd2.and("storeId","=", StringUtil.getStoreId());
                cnd2.and("name","like","%"+name+"%");
                List<Sales_coupon> salesCouponList = salesCouponService.query("^(id|)$",cnd2);
                List<String> ids = new ArrayList<>();
                if(salesCouponList != null && salesCouponList.size()>0){
                    for (Sales_coupon salesCoupon:salesCouponList) {
                        ids.add(salesCoupon.getId());
                    }
                    cnd.and("couponId","in", ids);
                }else {
                    return null;
                }
            }
            if(StringUtils.isNotEmpty(mobile)){
                cnd.and("mobile","like","%"+mobile+"%");
            }
            if(StringUtils.isNotEmpty(code)){
                cnd.and("code","like","%"+code+"%");
            }
            if(StringUtils.isNotEmpty(userName)){
                cnd.and("userName","like","%"+userName+"%");
            }
            NutMap nutMap = memberCouponService.data(dataTable.getLength(), dataTable.getStart(), dataTable.getDraw(), dataTable.getOrders(), dataTable.getColumns(), cnd, null);
            List<Member_coupon> member_couponList = (List<Member_coupon>)nutMap.get("data");
            List<Member_coupon> member_coupons = new ArrayList<>();
            for(Member_coupon member_coupon : member_couponList) {
                Sales_coupon sales_coupon = salesCouponService.fetch(member_coupon.getCouponId());
//                if(sales_coupon.getDelFlag()){
//                    continue;
//                }
                member_coupon.setSales_coupon(sales_coupon);
                member_coupons.add(member_coupon);
            }
            nutMap.put("data",member_coupons);
            return nutMap;
        } catch (Exception e) {
            log.error("获取核销优惠卷列表异常",e);
            return Result.error("fail");
        }
    }

    /***
     * 获取核销优惠卷列表
     * @return
     */
    @RequestMapping("/hexiao")
    @SJson("full")
    public Object hexiao(String mcId){
        try {
            Member_coupon member_coupon = memberCouponService.fetch(mcId);
            Sales_coupon sales_coupon = salesCouponService.fetch(member_coupon.getCouponId());
            if(member_coupon.getStatus() == 1){
                log.error("此优惠券已核销mcId:"+mcId);
                return Result.error(1001,"此优惠券已核销");
            }
            Integer endAt = sales_coupon.getEndTime();
            Integer nowTime = DateUtil.getTime(new Date());
            if(nowTime > endAt){
                log.error("此优惠券已过期mcId:"+mcId);
                return Result.error(1002,"此优惠券已过期");
            }
            //更新成已核销
            Member_coupon member_couponUpd = new Member_coupon();
            member_couponUpd.setId(mcId);
            member_couponUpd.setStatus(1);
            memberCouponService.updateIgnoreNull(member_couponUpd);
            //如果有推荐人 给推荐人送一张券
            try{
               if(StringUtils.isNotEmpty(member_couponUpd.getSourceAccountId())){
                    String recommendCouponId = sales_coupon.getRecommendCouponId();
               }
            }catch (Exception e){
                log.error("给推荐人送券发生异常",e);
                e.printStackTrace();
            }
            return Result.success("ok");
        } catch (Exception e) {
            log.error("获取核销优惠卷列表异常",e);
            return Result.error(9999,"fail");
        }
    }


}
