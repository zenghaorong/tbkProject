package com.aebiz.app.web.modules.controllers.platform.cms;

import com.aebiz.app.web.commons.log.annotation.SLog;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.datatable.DataTable;
import com.aebiz.commons.utils.StringUtil;
import com.aebiz.app.cms.modules.models.Cms_love;
import com.aebiz.app.cms.modules.services.CmsLoveService;
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
@RequestMapping("/platform/cms/love")
public class CmsLoveController {
    private static final Log log = Logs.get();
    @Autowired
	private CmsLoveService cmsLoveService;

    @RequestMapping("")
    @RequiresPermissions("platform.cms.love")
	public String index() {
		return "pages/platform/cms/love/index";
	}

	@RequestMapping("/data")
    @SJson("full")
    @RequiresPermissions("platform.cms.love")
    public Object data(DataTable dataTable) {
		Cnd cnd = Cnd.NEW();
    	return cmsLoveService.data(dataTable.getLength(), dataTable.getStart(), dataTable.getDraw(), dataTable.getOrders(), dataTable.getColumns(), cnd, null);
    }

    @RequestMapping("/add")
    @RequiresPermissions("platform.cms.love")
    public String add() {
    	return "pages/platform/cms/love/add";
    }

    @RequestMapping("/addDo")
    @SJson
    @SLog(description = "Cms_love")
    @RequiresPermissions("platform.cms.love.add")
    public Object addDo(Cms_love cmsLove, HttpServletRequest req) {
		try {
			cmsLoveService.insert(cmsLove);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }

    @RequestMapping("/edit/{id}")
    @RequiresPermissions("platform.cms.love")
    public String edit(@PathVariable String id,HttpServletRequest req) {
		req.setAttribute("obj", cmsLoveService.fetch(id));
		return "pages/platform/cms/love/edit";
    }

    @RequestMapping("/editDo")
    @SJson
    @SLog(description = "Cms_love")
    @RequiresPermissions("platform.cms.love.edit")
    public Object editDo(Cms_love cmsLove, HttpServletRequest req) {
		try {
            cmsLove.setOpBy(StringUtil.getUid());
			cmsLove.setOpAt((int) (System.currentTimeMillis() / 1000));
			cmsLoveService.updateIgnoreNull(cmsLove);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }

    @RequestMapping(value = {"/delete/{id}", "/delete"})
    @SJson
    @SLog(description = "Cms_love")
    @RequiresPermissions("platform.cms.love.delete")
    public Object delete(@PathVariable(required = false) String id, @RequestParam(value = "ids",required = false)  String[] ids, HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				cmsLoveService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				cmsLoveService.delete(id);
    			req.setAttribute("id", id);
			}
            return Result.success("globals.result.success");
        } catch (Exception e) {
            return Result.error("globals.result.error");
        }
    }

    @RequestMapping("/detail/{id}")
    @RequiresPermissions("platform.cms.love")
	public String detail(@PathVariable String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", cmsLoveService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
        return "pages/platform/cms/love/detail";
    }

}
