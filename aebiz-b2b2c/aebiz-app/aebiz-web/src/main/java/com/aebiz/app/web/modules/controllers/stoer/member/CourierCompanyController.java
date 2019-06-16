package com.aebiz.app.web.modules.controllers.stoer.member;

import com.aebiz.app.web.commons.log.annotation.SLog;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.datatable.DataTable;
import com.aebiz.commons.utils.StringUtil;
import com.aebiz.app.member.modules.models.Courier_company;
import com.aebiz.app.member.modules.services.CourierCompanyService;
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
@RequestMapping("/store/member/company")
public class CourierCompanyController {
    private static final Log log = Logs.get();
    @Autowired
	private CourierCompanyService courierCompanyService;

    @RequestMapping("")
//    @RequiresPermissions("store.member.company")
	public String index() {
		return "pages/store/member/company/index";
	}

	@RequestMapping("/data")
    @SJson("full")
//    @RequiresPermissions("store.member.company")
    public Object data(DataTable dataTable,String courierCompanyName,String courierCompanyKey) {
		Cnd cnd = Cnd.NEW();
		if(Strings.isNotBlank(courierCompanyName)) {
            cnd.and("courierCompanyName", "like", "%" + courierCompanyName + "%");
        }
    	return courierCompanyService.data(dataTable.getLength(), dataTable.getStart(), dataTable.getDraw(), dataTable.getOrders(), dataTable.getColumns(), cnd, null);
    }

    @RequestMapping("/add")
    @RequiresPermissions("store.member.company")
    public String add() {
    	return "pages/store/member/company/add";
    }

    @RequestMapping("/addDo")
    @SJson
    @SLog(description = "Courier_company")
    @RequiresPermissions("store.member.company.add")
    public Object addDo(Courier_company courierCompany, HttpServletRequest req) {
		try {
			courierCompanyService.insert(courierCompany);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }

    @RequestMapping("/edit/{id}")
    @RequiresPermissions("store.member.company")
    public String edit(String id,HttpServletRequest req) {
		req.setAttribute("obj", courierCompanyService.fetch(id));
		return "pages/store/member/company/edit";
    }

    @RequestMapping("/editDo")
    @SJson
    @SLog(description = "Courier_company")
    @RequiresPermissions("store.member.company.edit")
    public Object editDo(Courier_company courierCompany, HttpServletRequest req) {
		try {
			courierCompanyService.updateIgnoreNull(courierCompany);
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }

//    @RequestMapping(value = {"/delete/{id}", "/delete"})
//    @SJson
//    @SLog(description = "Courier_company")
//    @RequiresPermissions("store.member.company.delete")
//    public Object delete(@PathVariable(required = false) ${table.pkType} id, @RequestParam(value = "ids",required = false)  ${table.pkType}[] ids, HttpServletRequest req) {
//		try {
//			if(ids!=null&&ids.length>0){
//				courierCompanyService.delete(ids);
//    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
//			}else{
//				courierCompanyService.delete(id);
//    			req.setAttribute("id", id);
//			}
//            return Result.success("globals.result.success");
//        } catch (Exception e) {
//            return Result.error("globals.result.error");
//        }
//    }

//    @RequestMapping("/detail/{id}")
//    @RequiresPermissions("store.member.company")
//	public String detail(@PathVariable ${table.pkType} id, HttpServletRequest req) {
//		if (!Strings.isBlank(id)) {
//            req.setAttribute("obj", courierCompanyService.fetch(id));
//		}else{
//            req.setAttribute("obj", null);
//        }
//        return "pages/store/member/company/detail";
//    }

}
