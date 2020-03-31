package com.aebiz.app.web.modules.controllers.store.integral;

import com.aebiz.app.acc.modules.models.Account_info;
import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountInfoService;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.integral.modules.models.Member_Integral_Detail;
import com.aebiz.app.integral.modules.services.MemberIntegralDetailService;
import com.aebiz.app.order.modules.models.Order_after_refundment;
import com.aebiz.app.web.commons.log.annotation.SLog;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.datatable.DataTable;
import com.aebiz.commons.utils.StringUtil;
import com.aebiz.app.integral.modules.models.Member_Integral;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.baseframework.view.annotation.SJson;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/store/integral/Integral")
public class MemberIntegralController {
    private static final Log log = Logs.get();
    @Autowired
	private MemberIntegralService memberIntegralService;

    @Autowired
    private AccountUserService accountUserService;

    @Autowired
    private AccountInfoService accountInfoService;

    @Autowired
    private MemberIntegralDetailService memberIntegralDetailService;

    @RequestMapping("")
    @RequiresPermissions("store.integral.Integral")
	public String index() {
		return "pages/store/integral/Integral/index";
	}

    @RequestMapping("/datax")
    @SJson("full")
    @RequiresPermissions("store.integral.Integral")
    public Object datax(DataTable dataTable,String mobile,String nickName) {
        List<String> accountIdList = new ArrayList<>();
        if(StringUtils.isNotEmpty(mobile)){
            Cnd cnd = Cnd.NEW();
            cnd.and("mobile","like","%"+mobile+"%");
            List<Account_user> accountUserList = accountUserService.query(cnd,"^(accountId)$");
            for (Account_user accountUser : accountUserList){
                accountIdList.add(accountUser.getAccountId());
            }
        }
        if(StringUtils.isNotEmpty(nickName)){
            Cnd cnd = Cnd.NEW();
            cnd.and("nickName","like","%"+nickName+"%");
            List<Account_info> accountInfoList = accountInfoService.query(cnd,"^(id)$");
            for (Account_info account_info : accountInfoList){
                accountIdList.add(account_info.getId());
            }
        }
        Cnd cnd = Cnd.NEW();
        cnd.and("storeId","=",StringUtil.getStoreId());
        if(accountIdList.size()>0){
            cnd.and("customerUuid","in",accountIdList);
        }
        if(StringUtils.isNotEmpty(mobile) || StringUtils.isNotEmpty(nickName)){
            if(accountIdList.size()<1){
                NutMap nutMap2 = new NutMap();
                nutMap2.put("data", null);
                return nutMap2;
            }
        }
        cnd.desc("opAt");
        NutMap nutMap = memberIntegralDetailService.data(dataTable.getLength(), dataTable.getStart(), dataTable.getDraw(), dataTable.getOrders(), dataTable.getColumns(), cnd, null);
        if (nutMap.get("data") != null) {
            List<Member_Integral_Detail> refundment = (List<Member_Integral_Detail>) nutMap.get("data");
            for (Member_Integral_Detail m :refundment) {
                Account_info account_info = accountInfoService.fetch(m.getCustomerUuid());
                Account_user account_user = accountUserService.getAccount(m.getCustomerUuid());
                m.setMobile(account_user.getMobile());
                m.setNickName(account_info.getNickname());
            }
            nutMap.put("data", refundment);
        }
        return nutMap;
    }

	@RequestMapping("/data")
    @SJson("full")
    @RequiresPermissions("store.integral.Integral")
    public Object data(DataTable dataTable) {
		Cnd cnd = Cnd.NEW();
        cnd.and("storeId","=",StringUtil.getStoreId());
        NutMap nutMap = memberIntegralService.data(dataTable.getLength(), dataTable.getStart(), dataTable.getDraw(), dataTable.getOrders(), dataTable.getColumns(), cnd, "account_user");
        if (nutMap.get("data") != null) {
            List<Member_Integral> refundment = (List<Member_Integral>) nutMap.get("data");
            for (Member_Integral m : refundment) {
                Cnd cnd2 = Cnd.NEW();
                cnd2.and("accountId","=",m.getCustomerUuid());
                List<Account_user> auList = accountUserService.query(cnd2);
                if(auList!=null&&auList.size()>0){
                    m.setCustomerName(auList.get(0).getLoginname());
                }
            }
            nutMap.put("data", refundment);
        }
        return nutMap;
    }

    @RequestMapping("/add")
    @RequiresPermissions("store.integral.Integral")
    public String add() {
    	return "pages/store/integral/Integral/add";
    }

    @RequestMapping("/addDo")
    @SJson
    @SLog(description = "Member_Integral")
    @RequiresPermissions("store.integral.Integral.add")
    public Object addDo(Member_Integral memberIntegral, HttpServletRequest req) {
		try {
            memberIntegral.setStoreId(StringUtil.getStoreId());
			memberIntegralService.insert(memberIntegral);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }

    @RequestMapping("/edit/{id}")
    @RequiresPermissions("store.integral.Integral")
    public String edit(@PathVariable String id,HttpServletRequest req) {
		req.setAttribute("obj", memberIntegralService.fetch(id));
		return "pages/store/integral/Integral/edit";
    }

    @RequestMapping("/editDo")
    @SJson
    @SLog(description = "Member_Integral")
    @RequiresPermissions("store.integral.Integral.edit")
    public Object editDo(Member_Integral memberIntegral, HttpServletRequest req) {
		try {
            memberIntegral.setOpBy(StringUtil.getUid());
			memberIntegral.setOpAt((int) (System.currentTimeMillis() / 1000));
            memberIntegral.setStoreId(StringUtil.getStoreId());
			memberIntegralService.updateIgnoreNull(memberIntegral);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }

    @RequestMapping(value = {"/delete/{id}", "/delete"})
    @SJson
    @SLog(description = "Member_Integral")
    @RequiresPermissions("store.integral.Integral.delete")
    public Object delete(@PathVariable(required = false) String id, @RequestParam(value = "ids",required = false)  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				memberIntegralService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				memberIntegralService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("globals.result.success");
        } catch (Exception e) {
            return Result.error("globals.result.error");
        }
    }

    @RequestMapping("/detail/{id}")
    @RequiresPermissions("store.integral.Integral")
	public String detail(@PathVariable String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", memberIntegralService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
        return "pages/store/integral/Integral/detail";
    }

    /**
     * 后台给会员扣除积分
     * @param im
     * @param accountId
     * @param desc 说明
     * @return
     */
    @RequestMapping("/minusPoints")
    @SJson
    public Object minusPoints(int im,String accountId,String desc) {
        try {
            Cnd cndMi = Cnd.NEW();
            cndMi.and("storeId","=", StringUtil.getStoreId());
            cndMi.and("customerUuid","=", accountId);
            Member_Integral member_integral = memberIntegralService.fetch(cndMi);
            if(im>member_integral.getUseAbleIntegral()){
                return Result.error("积分不足");
            }
            memberIntegralService.minusPoints(StringUtil.getStoreId(),im,accountId,desc);
            return Result.success("globals.result.success");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("globals.result.error");
        }
    }

}
