package com.aebiz.app.web.modules.controllers.store.integral;

import com.aebiz.app.web.commons.log.annotation.SLog;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.datatable.DataTable;
import com.aebiz.commons.utils.StringUtil;
import com.aebiz.app.integral.modules.models.Integral_Rule;
import com.aebiz.app.integral.modules.services.IntegralRuleService;
import com.aebiz.baseframework.view.annotation.SJson;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/store/integral/rule")
public class IntegralRuleController {
    private static final Log log = Logs.get();
    @Autowired
	private IntegralRuleService integralRuleService;

    @RequestMapping("")
    @RequiresPermissions("store.integral.rule")
	public String index() {
		return "pages/store/integral/rule/index";
	}

	@RequestMapping("/data")
    @SJson("full")
    @RequiresPermissions("store.integral.rule")
    public Object data(DataTable dataTable) {
		Cnd cnd = Cnd.NEW();
        cnd.and("storeId","=",StringUtil.getStoreId());
    	return integralRuleService.data(dataTable.getLength(), dataTable.getStart(), dataTable.getDraw(), dataTable.getOrders(), dataTable.getColumns(), cnd, null);
    }

    @RequestMapping("/add")
    @RequiresPermissions("store.integral.rule")
    public String add() {
    	return "pages/store/integral/rule/add";
    }

    @RequestMapping("/addDo")
    @SJson
    @SLog(description = "Integral_Rule")
    @RequiresPermissions("store.integral.rule.add")
    public Object addDo(Integral_Rule integralRule, HttpServletRequest req) {
		try {
            Cnd cnd = Cnd.NEW();
            cnd.and("storeId","=",StringUtil.getStoreId());
            cnd.and("ruleCode","=",integralRule.getRuleCode());
            List<Integral_Rule> integralRuleList = integralRuleService.query(cnd);
            if(integralRuleList.size()>0){
                return Result.error("积分规则编码重复");
            }
            integralRule.setStoreId(StringUtil.getStoreId());
			integralRuleService.insert(integralRule);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }

    @RequestMapping("/edit/{id}")
    @RequiresPermissions("store.integral.rule")
    public String edit(@PathVariable String id,HttpServletRequest req) {
		req.setAttribute("obj", integralRuleService.fetch(id));
		return "pages/store/integral/rule/edit";
    }

    @RequestMapping("/editDo")
    @SJson
    @SLog(description = "Integral_Rule")
    @RequiresPermissions("store.integral.rule.edit")
    public Object editDo(Integral_Rule integralRule, HttpServletRequest req) {
		try {
            Cnd cnd = Cnd.NEW();
            cnd.and("storeId","=",StringUtil.getStoreId());
            cnd.and("ruleCode","=",integralRule.getRuleCode());
//            List<Integral_Rule> integralRuleList = integralRuleService.query(cnd);
//            if(integralRuleList.size()>0){
//                return Result.error("积分规则编码重复");
//            }
            integralRule.setOpBy(StringUtil.getUid());
			integralRule.setOpAt((int) (System.currentTimeMillis() / 1000));
            integralRule.setStoreId(StringUtil.getStoreId());
			integralRuleService.updateIgnoreNull(integralRule);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }

    @RequestMapping(value = {"/delete/{id}", "/delete"})
    @SJson
    @SLog(description = "Integral_Rule")
    @RequiresPermissions("store.integral.rule.delete")
    public Object delete(@PathVariable(required = false) String id, @RequestParam(value = "ids",required = false)  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				integralRuleService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				integralRuleService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("globals.result.success");
        } catch (Exception e) {
            return Result.error("globals.result.error");
        }
    }

    @RequestMapping("/detail/{id}")
    @RequiresPermissions("store.integral.rule")
	public String detail(@PathVariable String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", integralRuleService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
        return "pages/store/integral/rule/detail";
    }

}
