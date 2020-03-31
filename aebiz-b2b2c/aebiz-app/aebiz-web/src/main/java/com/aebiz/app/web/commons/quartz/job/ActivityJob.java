package com.aebiz.app.web.commons.quartz.job;

import com.aebiz.app.goods.modules.models.Goods_main;
import com.aebiz.app.shop.modules.models.Shop_estemp;
import com.aebiz.app.store.modules.models.Store_activity;
import com.aebiz.app.store.modules.services.StoreActivityService;
import com.aebiz.app.store.modules.services.impl.StoreActivityServiceImpl;
import com.aebiz.app.sys.modules.services.SysTaskService;
import com.aebiz.app.sys.modules.services.impl.SysTaskServiceImpl;
import com.aebiz.commons.utils.DateUtil;
import com.aebiz.commons.utils.SpringUtil;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.List;

public class ActivityJob implements Job {

    private SysTaskService sysTaskService = SpringUtil.getBean("sysTaskServiceImpl", SysTaskServiceImpl.class);

    private StoreActivityService storeActivityService = SpringUtil.getBean("storeActivityServiceImpl", StoreActivityServiceImpl.class);

    private static final Log log = Logs.get();


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String taskId = context.getJobDetail().getKey().getName();
        try {
            log.info("每分钟给数据加量开始执行---------------");
            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag","=",false);
            cnd.and("disabled","=",false);
            cnd.and("startTime","<=", DateUtil.getNowTime());
            cnd.and("endTime",">", DateUtil.getNowTime());
            List<Store_activity> activityList = storeActivityService.query(cnd);
            for (Store_activity sa:activityList) {
                //已浏览人数:前几天每分钟四个，最后一天每分钟六个
                int yllrs = sa.getYllrs()+4;
                if(getDatePoor(sa.getEndTime(), DateUtil.getNowTime())<1){
                    yllrs = sa.getYllrs()+6;
                }
                //已分享人数:已浏览用户数的45.77%，取整数
                int yfxrs = (int)(yllrs * 0.4577);
                //已报名人数:已分享人数的61.77%，取整数
                int ybmyhs = (int)(yfxrs * 0.6177);;

                sa.setYllrs(yllrs);
                sa.setYfxrs(yfxrs);
                sa.setYbmyhs(ybmyhs);
                storeActivityService.update(sa);
            }
            sysTaskService.update(Chain.make("exeAt", (int) (System.currentTimeMillis() / 1000)).add("exeResult", "执行成功").add("nextAt", DateUtil.getTime(context.getNextFireTime())), Cnd.where("id", "=", taskId));
        } catch (Exception e) {
            e.printStackTrace();
            sysTaskService.update(Chain.make("exeAt", (int) (System.currentTimeMillis() / 1000)).add("exeResult", "执行失败").add("nextAt", DateUtil.getTime(context.getNextFireTime())), Cnd.where("id", "=", taskId));
        }
    }

    public static int getDatePoor(long time1,long time2 ) {
        Date endDate = new Date(time1);
        Date nowDate = new Date(time2);
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff =  time1*1000 - time2*1000;
        // 计算差多少天
        Long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
//        return day + "天" + hour + "小时" + min + "分钟";
        return day.intValue();
    }


}
