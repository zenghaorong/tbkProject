package com.aebiz.app.web.modules.controllers.store.sys;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.cms.modules.models.Cms_review;
import com.aebiz.app.order.modules.models.em.OrderPayStatusEnum;
import com.aebiz.app.order.modules.models.em.OrderTypeEnum;
import com.aebiz.app.order.modules.services.OrderMainService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import com.aebiz.commons.utils.DateUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/15  10:44
 * @Description: 数据统计相关控制层
 */
@Controller
@RequestMapping("/store/count")
public class StoreCountController {

    private static final Log log = Logs.get();

    @Autowired
    private AccountUserService accountUserService;

    @Autowired
    private OrderMainService orderMainService;

    /**
     * 获取商城总用户量
     */
    @RequestMapping("getUserCount.html")
    @SJson
    public Result getUserCount(){
        try {
            String month = null;
            String dateString;
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Date date = new Date();
            dateString = sdf.format(cal.getTime());
            List<String> rqList = new ArrayList<>();
            System.out.println("倒序前\n");
            for (int i = 0; i < 6; i++) {
                dateString = sdf.format(cal.getTime());
                rqList.add(dateString.substring(0, 7));
                cal.add(Calendar.MONTH, -1);
            }

            Collections.reverse(rqList);
            System.out.println("倒序后\n"+ JSON.toJSONString(rqList));


            List<Integer> integerList = new ArrayList<>();
            for (String s : rqList) {
                int a =  (int)(sdf.parse(s).getTime() / 1000);
                System.out.println("时间戳："+a);
                integerList.add(a);
            }

            Cnd cnd1 = Cnd.NEW();
            cnd1.and("opAt",">=",integerList.get(0));
            cnd1.and("opAt","<",integerList.get(1));
            int count1=accountUserService.count(cnd1);

            Cnd cnd2 = Cnd.NEW();
            cnd2.and("opAt",">=",integerList.get(1));
            cnd2.and("opAt","<",integerList.get(2));
            int count2=accountUserService.count(cnd2);

            Cnd cnd3 = Cnd.NEW();
            cnd3.and("opAt",">=",integerList.get(2));
            cnd3.and("opAt","<",integerList.get(3));
            int count3=accountUserService.count(cnd3);

            Cnd cnd4 = Cnd.NEW();
            cnd4.and("opAt",">=",integerList.get(3));
            cnd4.and("opAt","<",integerList.get(4));
            int count4=accountUserService.count(cnd4);

            Cnd cnd5 = Cnd.NEW();
            cnd5.and("opAt",">=",integerList.get(4));
            cnd5.and("opAt","<",integerList.get(5));
            int count5=accountUserService.count(cnd5);

            Cnd cnd6 = Cnd.NEW();
            cnd6.and("opAt",">",integerList.get(5));
            int count6=accountUserService.count(cnd6);
            List<Integer> countList = new ArrayList<>();
            countList.add(count1);
            countList.add(count2);
            countList.add(count3);
            countList.add(count4);
            countList.add(count5);
            countList.add(count6);

            int count=accountUserService.count();

            Map<String,Object> map = new HashedMap();
            map.put("countList",countList);
            map.put("rqList",rqList);
            map.put("count",count);
            return Result.success("ok",map);
        } catch (Exception e) {
            log.error("发布评论异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 获取商城总订单量
     */
    @RequestMapping("getOrderCount.html")
    @SJson
    public Result getOrderCount(){
        try {
            //实物订单总量
            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", 0 );
            cnd.and("payStatus", "=", OrderPayStatusEnum.PAYALL.getKey() );
            cnd.and("orderType", "=", OrderTypeEnum.product_order_type.getKey() );
            int count=orderMainService.count(cnd);

            //视频订单总量
            Cnd cnd2 = Cnd.NEW();
            cnd2.and("delFlag", "=", 0 );
            cnd2.and("payStatus", "=", OrderPayStatusEnum.PAYALL.getKey() );
            cnd2.and("orderType", "=", OrderTypeEnum.video_order_type.getKey());
            int count2=orderMainService.count(cnd2);

            //包月订单总量
            Cnd cnd3 = Cnd.NEW();
            cnd3.and("delFlag", "=", 0 );
            cnd3.and("payStatus", "=", OrderPayStatusEnum.PAYALL.getKey() );
            cnd3.and("orderType", "=", OrderTypeEnum.monthly_order_type.getKey());
            int count3=orderMainService.count(cnd3);

            Map<String,Object> map = new HashedMap();
            map.put("pCount",count);
            map.put("vCount",count2);
            map.put("mCount",count3);
            int zCount = count + count2 + count3;
            map.put("zCount",zCount);


           // 创建一个数值格式化对象
            NumberFormat numberFormat = NumberFormat.getInstance();
           // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(2);
            String pResult = numberFormat.format((float)count/(float)zCount*100);
            String vResult = numberFormat.format((float)count2/(float)zCount*100);
            String mResult = numberFormat.format((float)count3/(float)zCount*100);

            List<String> array = new ArrayList<>();
            array.add(pResult+"%");
            array.add(vResult+"%");
            array.add(mResult+"%");

            map.put("result",array);
            return Result.success("ok",map);
        } catch (Exception e) {
            log.error("发布评论异常",e);
            return Result.error("fail");
        }
    }




}
