package com.aebiz.app.web.modules.controllers.platform.tbk;

import com.aebiz.app.web.commons.log.annotation.SLog;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.datatable.DataTable;
import com.aebiz.commons.utils.StringUtil;
import com.aebiz.app.tbk.modules.models.Tbk_cat;
import com.aebiz.app.tbk.modules.services.TbkCatService;
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
@RequestMapping("/platform/tbk/cat")
public class TbkCatController {
    private static final Log log = Logs.get();
    @Autowired
	private TbkCatService tbkCatService;

    @RequestMapping("")
    @RequiresPermissions("platform.tbk.cat")
	public String index() {
		return "pages/platform/tbk/cat/index";
	}

	@RequestMapping("/data")
    @SJson("full")
    @RequiresPermissions("platform.tbk.cat")
    public Object data(DataTable dataTable) {
		Cnd cnd = Cnd.NEW();
    	return tbkCatService.data(dataTable.getLength(), dataTable.getStart(), dataTable.getDraw(), dataTable.getOrders(), dataTable.getColumns(), cnd, null);
    }

    @RequestMapping("/add")
    @RequiresPermissions("platform.tbk.cat")
    public String add() {
    	return "pages/platform/tbk/cat/add";
    }

    @RequestMapping("/addDo")
    @SJson
    @SLog(description = "Tbk_cat")
    @RequiresPermissions("platform.tbk.cat.add")
    public Object addDo(Tbk_cat tbkCat, HttpServletRequest req) {
		try {
			tbkCatService.insert(tbkCat);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }


    @RequestMapping("/editDo")
    @SJson
    @SLog(description = "Tbk_cat")
    @RequiresPermissions("platform.tbk.cat.edit")
    public Object editDo(Tbk_cat tbkCat, HttpServletRequest req) {
		try {
            tbkCat.setOpBy(StringUtil.getUid());
			tbkCat.setOpAt((int) (System.currentTimeMillis() / 1000));
			tbkCatService.updateIgnoreNull(tbkCat);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }


}
