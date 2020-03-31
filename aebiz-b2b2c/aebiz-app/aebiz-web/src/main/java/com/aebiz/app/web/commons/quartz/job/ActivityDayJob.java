package com.aebiz.app.web.commons.quartz.job;

import com.aebiz.app.store.modules.models.Store_activity;
import com.aebiz.app.store.modules.services.StoreActivityService;
import com.aebiz.app.store.modules.services.impl.StoreActivityServiceImpl;
import com.aebiz.app.sys.modules.services.SysTaskService;
import com.aebiz.app.sys.modules.services.impl.SysTaskServiceImpl;
import com.aebiz.commons.utils.DateUtil;
import com.aebiz.commons.utils.SpringUtil;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class ActivityDayJob implements Job {

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
                //因为这样取出来是整数，能不能用随机函数再加上1-10之间的任意一个整数
                int yllrs = sa.getYllrs()+(int)(1+Math.random()*(10-1+1));
                sa.setYllrs(yllrs);
                storeActivityService.update(sa);
            }
            sysTaskService.update(Chain.make("exeAt", (int) (System.currentTimeMillis() / 1000)).add("exeResult", "执行成功").add("nextAt", DateUtil.getTime(context.getNextFireTime())), Cnd.where("id", "=", taskId));
        } catch (Exception e) {
            e.printStackTrace();
            sysTaskService.update(Chain.make("exeAt", (int) (System.currentTimeMillis() / 1000)).add("exeResult", "执行失败").add("nextAt", DateUtil.getTime(context.getNextFireTime())), Cnd.where("id", "=", taskId));
        }
    }


}
