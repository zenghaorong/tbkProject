package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.integral.modules.models.Member_Integral;
import com.aebiz.app.integral.modules.models.Member_Integral_Detail;
import com.aebiz.app.integral.modules.services.MemberIntegralDetailService;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.app.sys.modules.services.SysDictService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Auther: czm
 * @Date: 2019/3/23  20:55
 * @Description:
 */
@Controller
@RequestMapping("/open/h5/integral")
public class IntegralController {


    private static final Log log = Logs.get();

    @Autowired
    private MemberIntegralService memberIntegralService;
    @Autowired
    private MemberIntegralDetailService memberIntegralDetailService;

    @Autowired
    private SysDictService sysDictService;



    /**
     * 进入我的积分列表
     * @return
     */
    @RequestMapping("/myIntegral.html")
    public String myIntegral(String code,HttpServletRequest request) {
        return "pages/front/h5/niantu/myIntegral";
    }



    /**
     * 获取积分信息
     */
    @RequestMapping("getIntegral.html")
    @SJson
    public Result getIntegral() {
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = null;
            try{
                accountUser = (Account_user) subject.getPrincipal();

            }catch (Exception e){

            }
            if(accountUser==null){
                return Result.error(-1,"未登录，请重新登录！");
            }

            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", false);
            cnd.and("customerUuid","=",accountUser.getAccountId());
            List<Member_Integral> list=memberIntegralService.query(cnd);
            Member_Integral mi = new Member_Integral();
            if(list!=null&&list.size()>0){
                Cnd dCnd = Cnd.NEW();
                dCnd.and("delFlag", "=", false);
                dCnd.and("customerUuid", "=", accountUser.getAccountId());
                dCnd.desc("opAt");
                List<Member_Integral_Detail> details = memberIntegralDetailService.query(dCnd);
                mi=list.get(0);
                mi.setDetails(details);
            }
            String integralToMoney = sysDictService.getNameByCode("integralToMoney");
            mi.setIntegralMoney(integralToMoney);

            return Result.success("ok",mi);
        } catch (Exception e) {
            log.error("获取积分异常",e);
            return Result.error("fail");
        }
    }

}
