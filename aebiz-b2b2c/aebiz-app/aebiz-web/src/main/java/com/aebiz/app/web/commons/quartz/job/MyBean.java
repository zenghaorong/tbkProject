package com.aebiz.app.web.commons.quartz.job;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.order.modules.models.Order_main;
import com.aebiz.app.order.modules.models.em.OrderPayStatusEnum;
import com.aebiz.app.order.modules.models.em.OrderTypeEnum;
import com.aebiz.app.order.modules.services.OrderMainService;
import com.aebiz.app.sys.modules.services.SysTaskService;
import com.aebiz.commons.utils.DateUtil;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 定时器的bean
 * Created by Administrator on 2018/4/4.
 */
@Component("myBean")
public class MyBean {

    private static final Log log = Logs.get();

    @Autowired
    private SysTaskService sysTaskService;

    @Autowired
    private OrderMainService orderMainService;

    @Autowired
    private AccountUserService accountUserService;

    public void printMessage(){
        try{
            log.debug("视频包月检查到期发短信开始执行---------------");

            //1.查询订单列表
            //先判断是否开通包月
            Cnd cndM = Cnd.NEW();
            cndM.and("payStatus", "=", 3 );
            cndM.and("orderType", "=", "3");
            cndM.and("delFlag", "=", 0);
            List<Order_main> orderList = orderMainService.query(cndM);
            for(Order_main order_main:orderList){
                boolean is = this.videoMonthlyTime(order_main.getPayAt().toString(),order_main.getMonthlyNum());
                if(!is){//包月会员已到期
                    //判断短信是否已发送
                    if(Strings.isEmpty(order_main.getIsSend())){
                        //查询手机号
                        Cnd cnd = Cnd.NEW();
                        cnd.and("accountId", "=", order_main.getAccountId());
                        Account_user account_user = accountUserService.fetch(cnd);
                        //发送短信
                        String content = "尊敬的用户您好，您的包月观看视频权益已到期";
                        boolean isSend = accountUserService.sendMsg(content,account_user.getMobile());
                        if(isSend){
                            //更新发送状态
                            order_main.setIsSend("1");
                            orderMainService.update(order_main);
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
//            sysTaskService.update(Chain.make("exeAt", (int) (System.currentTimeMillis() / 1000)).add("exeResult", "执行失败").add("nextAt", DateUtil.getTime(), Cnd.where("id", "=", taskId));
        }finally {
            log.debug("视频包月检查到期发短信执行结束---------------");
        }
    }

    /**
     * 判断当前会员是否到期
     * @param payAt
     * @param monthlyNum
     * @return
     */
    public static boolean videoMonthlyTime(String payAt,int monthlyNum){
        //到期时间
        String nowDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long date = new Long(payAt);
            String str = DateUtil.getDate(date);
            Date parse = format.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            calendar.add(Calendar.MONTH, monthlyNum);
            nowDate = format.format(calendar.getTime());
            System.out.println("到期时间:"+nowDate);

            //到期时间转化为时间戳
            int endTime = DateUtil.getTime(nowDate);

            //获取当前时间戳
            int thisTime = DateUtil.getTime(new Date());

            if(endTime<thisTime){
                System.out.println("当前会员已到期");
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

}
