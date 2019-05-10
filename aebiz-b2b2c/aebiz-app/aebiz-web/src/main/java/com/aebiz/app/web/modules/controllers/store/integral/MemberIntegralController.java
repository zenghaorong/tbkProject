package com.aebiz.app.web.modules.controllers.store.integral;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.order.modules.models.Order_after_refundment;
import com.aebiz.app.web.commons.log.annotation.SLog;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.datatable.DataTable;
import com.aebiz.commons.utils.StringUtil;
import com.aebiz.app.integral.modules.models.Member_Integral;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.baseframework.view.annotation.SJson;
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

    @RequestMapping("")
    @RequiresPermissions("store.integral.Integral")
	public String index() {
		return "pages/store/integral/Integral/index";
	}

	@RequestMapping("/data")
    @SJson("full")
    @RequiresPermissions("store.integral.Integral")
    public Object data(DataTable dataTable) {
		Cnd cnd = Cnd.NEW();
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

}
