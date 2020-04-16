package com.aebiz.app.web.modules.controllers.store.activity;

import com.aebiz.app.sales.modules.models.Activity_coupon;
import com.aebiz.app.sales.modules.models.Sales_coupon;
import com.aebiz.app.sales.modules.services.ActivityCouponService;
import com.aebiz.app.sales.modules.services.SalesCouponService;
import com.aebiz.app.web.commons.log.annotation.SLog;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.datatable.DataTable;
import com.aebiz.commons.utils.StringUtil;
import com.aebiz.app.store.modules.models.Store_activity;
import com.aebiz.app.store.modules.services.StoreActivityService;
import com.aebiz.baseframework.view.annotation.SJson;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/store/activity/home/activity")
public class StoreActivityController {
    private static final Log log = Logs.get();
    @Autowired
	private StoreActivityService storeActivityService;
    @Autowired
    private ActivityCouponService activityCouponService;
    @Autowired
    private SalesCouponService salesCouponService;

    @RequestMapping("")
    @RequiresPermissions("store.activity.home.activity")
	public String index() {
		return "pages/store/activity/home/activity/index";
	}

	@RequestMapping("/data")
    @SJson("full")
    @RequiresPermissions("store.activity.home.activity")
    public Object data(Store_activity storeActivity,DataTable dataTable) {
		Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(storeActivity.getName())) {
            cnd.and("name", "like", Sqls.escapeSqlFieldValue("%" + Strings.trim(storeActivity.getName()) + "%"));
        }
        cnd.and("storeId","=",StringUtil.getStoreId());
        cnd.and("delFlag", "=", false);
        cnd.desc("opAt");
    	return storeActivityService.data(dataTable.getLength(), dataTable.getStart(), dataTable.getDraw(), dataTable.getOrders(), dataTable.getColumns(), cnd, null);
    }

    @RequestMapping("/add")
    public String add() {
    	return "pages/store/activity/home/activity/add";
    }

    @RequestMapping("/addDo")
    @SJson
    @SLog(description = "Store_activity")
    @RequiresPermissions("store.activity.home.activity.add")
    public Object addDo(Store_activity storeActivity,@RequestParam(value = "tmp_sartAt", required = false) String sartAt, @RequestParam(value = "tmp_endAt", required = false) String endAt) {
		try {
            List<Activity_coupon> activity_couponList = new ArrayList<>();
            if(StringUtils.isNotEmpty(storeActivity.getActivityCouponList())){
                activity_couponList = JSON.parseArray(storeActivity.getActivityCouponList(),Activity_coupon.class);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (Strings.isNotBlank(sartAt)) {
                storeActivity.setStartTime((int) (sdf.parse(sartAt).getTime() / 1000));
            }
            if (Strings.isNotBlank(endAt)) {
                storeActivity.setEndTime((int) (sdf.parse(endAt).getTime() / 1000));
            }
            storeActivity.setStoreId(StringUtil.getStoreId());
			Store_activity store_activity = storeActivityService.insert(storeActivity);
            for (Activity_coupon a:activity_couponList) {
                a.setActivityId(store_activity.getId());
                activityCouponService.insert(a);
            }
			return Result.success("globals.result.success");
		} catch (Exception e) {
			return Result.error("globals.result.error");
		}
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable String id,HttpServletRequest req) {
		req.setAttribute("obj", storeActivityService.fetch(id));
		return "pages/store/activity/home/activity/edit";
    }

    @RequestMapping("/editDo")
    @SJson
    @SLog(description = "Store_activity")
    @RequiresPermissions("store.activity.home.activity.edit")
    public Object editDo(@RequestParam(value = "tmp_sartAt", required = false) String sartAt,@RequestParam(value = "tmp_endAt", required = false) String endAt,
                         Store_activity storeActivity, HttpServletRequest req) {
		try {
            List<Activity_coupon> activity_couponList = new ArrayList<>();
            if(StringUtils.isNotEmpty(storeActivity.getActivityCouponList())){
                activity_couponList = JSON.parseArray(storeActivity.getActivityCouponList(),Activity_coupon.class);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (Strings.isNotBlank(sartAt)) {
                storeActivity.setStartTime((int) (sdf.parse(sartAt).getTime() / 1000));
            }
            if (Strings.isNotBlank(endAt)) {
                storeActivity.setEndTime((int) (sdf.parse(endAt).getTime() / 1000));
            }
            storeActivity.setOpBy(StringUtil.getUid());
			storeActivity.setOpAt((int) (System.currentTimeMillis() / 1000));
            storeActivity.setStoreId(StringUtil.getStoreId());
			storeActivityService.updateIgnoreNull(storeActivity);
            Cnd cnd2 = Cnd.NEW();
            cnd2.and("activityId","=",storeActivity.getId());
            List<Activity_coupon> listC = activityCouponService.query(cnd2);
            for (Activity_coupon a:listC) {
                if(a != null){
                    activityCouponService.delete(a.getId());
                }
            }
            for (Activity_coupon a:activity_couponList) {
                a.setActivityId(storeActivity.getId());
                activityCouponService.insert(a);
//                System.err.println(activity_couponList.size()+"插入成功："+JSON.toJSONString(a));
//                System.err.println(activity_couponList.size()+"插入成功list："+JSON.toJSONString(activity_couponList));
            }

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
                for (String id2 : ids) {
                    storeActivityService.update(Chain.make("delFlag", true), Cnd.where("id", "=", id2));
                }
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
                storeActivityService.update(Chain.make("delFlag", true), Cnd.where("id", "=", id));
    			req.setAttribute("id", id);
			}
            return Result.success("globals.result.success");
        } catch (Exception e) {
            return Result.error("globals.result.error");
        }
    }

    @RequestMapping("/detail/{id}")
	public String detail(@PathVariable String id, HttpServletRequest req) {
		if (!Strings.isBlank(id)) {
            req.setAttribute("obj", storeActivityService.fetch(id));
		}else{
            req.setAttribute("obj", null);
        }
        return "pages/store/activity/home/activity/detail";
    }

    /**
     * 根据活动编号获取关联券
     * @return
     */
    @RequestMapping("/couponSelect")
    @SJson
    public Object couponSelect(String activityId) {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("activityId","=",activityId);
            cnd.desc("index");
            List<Activity_coupon> activity_couponList = activityCouponService.query(cnd);
            for (Activity_coupon a:activity_couponList) {
                Sales_coupon salesCoupon = salesCouponService.fetch(a.getCouponId());
                a.setCouponName(salesCoupon.getName());
            }
            return Result.success("globals.result.success",activity_couponList);
        } catch (Exception e) {
            return Result.error("globals.result.error");
        }
    }

}
