package com.aebiz.app.web.modules.controllers.store.activity;

import com.aebiz.app.web.commons.log.annotation.SLog;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.datatable.DataTable;
import com.aebiz.commons.utils.StringUtil;
import com.aebiz.app.store.modules.models.Store_activity;
import com.aebiz.app.store.modules.services.StoreActivityService;
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

@Controller
@RequestMapping("/store/activity/home/activity")
public class StoreActivityController {
    private static final Log log = Logs.get();
    @Autowired
	private StoreActivityService storeActivityService;

    @RequestMapping("")
    @RequiresPermissions("store.activity.home.activity")
	public String index() {
		return "pages/store/activity/home/activity/index";
	}

	@RequestMapping("/data")
    @SJson("full")
    @RequiresPermissions("store.activity.home.activity")
    public Object data(DataTable dataTable) {
		Cnd cnd = Cnd.NEW();
    	return storeActivityService.data(dataTable.getLength(), dataTable.getStart(), dataTable.getDraw(), dataTable.getOrders(), dataTable.getColumns(), cnd, null);
    }

    @RequestMapping("/add")
    @RequiresPermissions("store.activity.home.activity")
    public String add() {
    	return "pages/store/activity/home/activity/add";
    }

    @RequestMapping("/addDo")
    @SJson
    @SLog(description = "Store_activity")
    @RequiresPermissions("store.activity.home.activity.add")
    public Object addDo(Store_activity storeActivity, HttpServletRequest req) {
		try {
			storeActivityService.insert(storeActivity);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }

    @RequestMapping("/edit/{id}")
    @RequiresPermissions("store.activity.home.activity")
    public String edit(@PathVariable String id,HttpServletRequest req) {
		req.setAttribute("obj", storeActivityService.fetch(id));
		return "pages/store/activity/home/activity/edit";
    }

    @RequestMapping("/editDo")
    @SJson
    @SLog(description = "Store_activity")
    @RequiresPermissions("store.activity.home.activity.edit")
    public Object editDo(Store_activity storeActivity, HttpServletRequest req) {
		try {
            storeActivity.setOpBy(StringUtil.getUid());
			storeActivity.setOpAt((int) (System.currentTimeMillis() / 1000));
			storeActivityService.updateIgnoreNull(storeActivity);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }

    @RequestMapping(value = {"/delete/{id}", "/delete"})
    @SJson
    @SLog(description = "Store_activity")
    @RequiresPermissions("store.activity.home.activity.delete")
    public Object delete(@PathVariable(required = false) String id, @RequestParam(value = "ids",required = false)  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				storeActivityService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				storeActivityService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("globals.result.success");
        } catch (Exception e) {
            return Result.error("globals.result.error");
        }
    }

    @RequestMapping("/detail/{id}")
    @RequiresPermissions("store.activity.home.activity")
	public String detail(@PathVariable String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", storeActivityService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
        return "pages/store/activity/home/activity/detail";
    }

}
