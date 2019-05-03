package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.order.modules.models.Order_goods;
import com.aebiz.app.order.modules.models.Order_main;
import com.aebiz.app.order.modules.models.Order_pay_refunds;
import com.aebiz.app.order.modules.models.em.OrderPayStatusEnum;
import com.aebiz.app.order.modules.models.em.OrderTypeEnum;
import com.aebiz.app.order.modules.services.OrderMainService;
import com.aebiz.app.order.modules.services.OrderPayRefundsService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zenghaorong
 * @Date: 2019/5/3  10:40
 * @Description: 订单退款
 */
@Controller
@RequestMapping("/open/h5/refunds")
public class OrderRefundsController {

    private static final Log log = Logs.get();

    @Autowired
    private OrderPayRefundsService orderPayRefundsService;

    @Autowired
    private OrderMainService orderMainService;


    /**
     * 进入退款申请页
     */
    @RequestMapping("/orderRefunds.html")
    public String orderRefunds(String orderId, HttpServletRequest request){
        request.setAttribute("orderId",orderId);
        return "pages/front/h5/niantu/orderRefunds";
    }

    /**
     * 保存退款申请
     */
    @RequestMapping("saveOrderRefunds.html")
    @SJson
    public Result saveOrderRefunds(String orderId,String node){
        try {


            //修改订单状态
            Order_main order_main = orderMainService.fetch(orderId);
            if(OrderPayStatusEnum.PAYALL.getKey() == order_main.getPayStatus()){
                Subject subject = SecurityUtils.getSubject();
                Account_user accountUser = (Account_user) subject.getPrincipal();
                Order_pay_refunds order_pay_refunds = new Order_pay_refunds();
                order_pay_refunds.setAccountId(accountUser.getAccountId());
                order_pay_refunds.setOrderId(orderId);
                order_pay_refunds.setNote(node);
                orderPayRefundsService.insert(order_pay_refunds);
                order_main.setPayStatus(OrderPayStatusEnum.REFUNDWAIT.getKey());
                orderMainService.update(order_main);
                return Result.success("ok");
            }else {
                return Result.error("fail2");
            }

        } catch (Exception e) {
            log.error("获取视频列表异常",e);
            return Result.error("fail");
        }
    }


}
