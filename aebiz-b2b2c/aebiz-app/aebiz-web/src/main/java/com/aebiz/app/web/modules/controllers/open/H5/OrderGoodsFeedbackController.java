package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_info;
import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountInfoService;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.cms.modules.models.Cms_video;
import com.aebiz.app.cms.modules.services.CmsVideoService;
import com.aebiz.app.goods.modules.models.Goods_image;
import com.aebiz.app.goods.modules.models.Goods_main;
import com.aebiz.app.goods.modules.services.GoodsImageService;
import com.aebiz.app.goods.modules.services.GoodsService;
import com.aebiz.app.order.modules.models.Order_goods;
import com.aebiz.app.order.modules.models.Order_goods_feedback;
import com.aebiz.app.order.modules.models.Order_main;
import com.aebiz.app.order.modules.models.em.OrderFeedStatusEnum;
import com.aebiz.app.order.modules.services.OrderGoodsFeedbackService;
import com.aebiz.app.order.modules.services.OrderGoodsService;
import com.aebiz.app.order.modules.services.OrderMainService;
import com.aebiz.app.web.commons.utils.WXPayUtil;
import com.aebiz.baseframework.base.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Auther: zenghaorong
 * @Date: 2019/5/9  15:57
 * @Description: 订单商品评价
 */
@Controller
@RequestMapping("/open/h5/feedback")
public class OrderGoodsFeedbackController {

    private static final Log log = Logs.get();

    @Autowired
    private OrderGoodsFeedbackService orderGoodsFeedbackService;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private OrderMainService orderMainService;

    @Autowired
    private GoodsImageService goodsImageService;
    @Autowired
    private GoodsImageService getGoodsImageService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private AccountUserService accountUserService;
    @Autowired
    private AccountInfoService accountInfoService;

    @Autowired
    private CmsVideoService cmsVideoService;

    /**
     * 进入商品订单评价页
     *
     */
    @RequestMapping("goFeedback")
    public String goFeedback(String orderId, HttpServletRequest request){
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if(accountUser==null){
            return "pages/front/h5/niantu/login";
        }
        Cnd cnd = Cnd.NEW();
        cnd.and("orderId","=",orderId);
        List<Order_goods> order_goodsList = orderGoodsService.query(cnd);
        if(order_goodsList.size() == 1){ //直接进评价页
            Cnd imgCnd = Cnd.NEW();
            Order_goods good = order_goodsList.get(0);
            imgCnd.and("goodsId","=",good.getGoodsId());
            List<Goods_image> imgList = goodsImageService.query(imgCnd);
            if(imgList!=null&&imgList.size()>0){
                good.setImgUrl(imgList.get(0).getImgAlbum());
            }
            request.setAttribute("order_goods",good);
            return "pages/front/h5/niantu/feedback";
        }else { //进入选择评价商品页
            if(order_goodsList!=null&&order_goodsList.size()>0){
                for (int i =0 ; i<order_goodsList.size();i++){
                    Cnd imgCnd = Cnd.NEW();
                    Order_goods good = order_goodsList.get(i);
                    imgCnd.and("goodsId","=",good.getGoodsId());
                    List<Goods_image> imgList = goodsImageService.query(imgCnd);
                    if(imgList!=null&&imgList.size()>0){
                        good.setImgUrl(imgList.get(0).getImgAlbum());
                    }
                }
            }
            request.setAttribute("order_goodsList",order_goodsList);
            return "pages/front/h5/niantu/feedbackSelectProduct";
        }
    }

    /**
     * 从选择评价商品页 进入评价页
     */
    @RequestMapping("goFeedbackBy")
    public String goFeedbackBy(String orderGoodsId,HttpServletRequest request){
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if(accountUser==null){
            return "pages/front/h5/niantu/login";
        }
        Order_goods order_goods = orderGoodsService.fetch(orderGoodsId);
        Cnd imgCnd = Cnd.NEW();
        Order_goods good = order_goods;
        imgCnd.and("goodsId","=",good.getGoodsId());
        List<Goods_image> imgList = goodsImageService.query(imgCnd);
        if(imgList!=null&&imgList.size()>0){
            good.setImgUrl(imgList.get(0).getImgAlbum());
        }
        //直接进评价页
        request.setAttribute("order_goods", good);
        return "pages/front/h5/niantu/feedback";

    }

    /**
     * 进入我的评价列表
     */
    @RequestMapping("goMyFeedback")
    public String goMyFeedback(HttpServletRequest request){
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if(accountUser==null){
            return "pages/front/h5/niantu/login";
        }
        return "pages/front/h5/niantu/feedbackMy";

    }

    /**
     * 进入商品评价列表
     */
    @RequestMapping("goProductFeedback")
    public String goMyFeedback(HttpServletRequest request,String goodsId){
        request.setAttribute("goodsId",goodsId);
        return "pages/front/h5/niantu/productFeedback";

    }


    /**
     * 保存当前商品评价
     */
    @RequestMapping("saveFeedback")
    @ResponseBody
    public Result saveFeedback(String orderGoodId,Integer appScore,String content){
        try {
            Order_goods order_goods = orderGoodsService.fetch(orderGoodId);

            Cnd cnd = Cnd.NEW();
            cnd.and("orderId","=",order_goods.getOrderId());
            cnd.and("accountId","=",order_goods.getAccountId());
            cnd.and("goodsId","=",order_goods.getGoodsId());
            List<Order_goods_feedback> list = orderGoodsFeedbackService.query(cnd);
            if(list!=null){
                if(list.size()>0){
                    return Result.error("您已评价过此订单");
                }
            }

            Order_goods_feedback Order_goods_feedback = new Order_goods_feedback();
            Order_goods_feedback.setOrderId(order_goods.getOrderId());
            Order_goods_feedback.setAccountId(order_goods.getAccountId());
            Order_goods_feedback.setStoreId(order_goods.getStoreId());
            Order_goods_feedback.setAppScore(appScore);
            Order_goods_feedback.setGoodsId(order_goods.getGoodsId());
            Order_goods_feedback.setProductId(order_goods.getProductId());
            Order_goods_feedback.setSku(order_goods.getSku());
            Order_goods_feedback.setFeedAt((int)WXPayUtil.getCurrentTimestamp());
            Order_goods_feedback.setFeedNote(content);
            orderGoodsFeedbackService.insert(Order_goods_feedback);

            //查询有几个商品要评价
            Cnd cndgood = Cnd.NEW();
            cndgood.and("orderId","=",order_goods.getOrderId());
            int goodNum = orderGoodsService.count(cndgood);
            //查询已评价了几个商品
            Cnd cndFee = Cnd.NEW();
            cndFee.and("orderId","=",order_goods.getOrderId());
            cndFee.and("accountId","=",order_goods.getAccountId());
            int feeCount = orderGoodsFeedbackService.count(cndFee);

            if(goodNum == feeCount) {
                Order_main order_main = new Order_main();
                order_main.setId(order_goods.getOrderId());
                order_main.setFeedStatus(OrderFeedStatusEnum.FEED.getKey());
                //更新订单评价状态
                orderMainService.updateIgnoreNull(order_main);
            }
            return Result.success("ok");
        }catch (Exception e){
            log.error("保存当前商品评价信息异常",e);
            return Result.error("fail");
        }
    }


    /**
     * 查询商品评价列表
     */
    @RequestMapping("queryFeedback")
    @ResponseBody
    public Result queryFeedback(String goodsId){
        try {
            Cnd cnd = Cnd.NEW();
            if(!Strings.isEmpty(goodsId)){
                cnd.and("goodsId","=",goodsId);
            }
            cnd.and("delFlag", "=", 0 );
            cnd.desc("opAt");
            List<Order_goods_feedback> list = orderGoodsFeedbackService.query(cnd);
            for (Order_goods_feedback o:list) {
                Cnd imgCnd = Cnd.NEW();
                imgCnd.and("accountId","=",o.getAccountId());
                    Account_user account_user=accountUserService.fetch(imgCnd);
                if(account_user==null){
                    account_user = new Account_user();
                }
                Account_info ai = new Account_info();
                ai = accountInfoService.fetch(account_user.getAccountId());
                account_user.setAccountInfo(ai);
                o.setAccountUser(account_user);

            }
            return Result.success("ok",list);
        }catch (Exception e){
            log.error("保存当前商品评价信息异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 查询订单商品我的评价
     */
    @RequestMapping("queryFeedbackMy")
    @ResponseBody
    public Result queryFeedbackMy(){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            Cnd cnd = Cnd.NEW();
            cnd.and("accountId","=",accountUser.getAccountId());
            cnd.and("delFlag", "=", 0 );
            cnd.desc("opAt");
            List<Order_goods_feedback> list = orderGoodsFeedbackService.query(cnd);
            for (Order_goods_feedback o:list) {
                  Goods_main goods_main = goodsService.fetch(o.getGoodsId());
                  if(goods_main!=null) {
                      Cnd imgCnd = Cnd.NEW();
                      imgCnd.and("goodsId", "=", o.getGoodsId());
                      List<Goods_image> imgList = goodsImageService.query(imgCnd);
                      if (imgList != null && imgList.size() > 0) {
                          goods_main.setImgList(imgList.get(0).getImgAlbum());
                      }
                      o.setGoodsMain(goods_main);
                      if (goods_main == null) {
                          Goods_main goods_main2 = new Goods_main();
                          o.setGoodsMain(goods_main2);
                      }
                  }else {
                      Cms_video cms_video = cmsVideoService.fetch(o.getGoodsId());
                      Goods_main goodsMain = new Goods_main();
                      goodsMain.setImgList(cms_video.getImageUrl());
                      goodsMain.setName(cms_video.getVideoTitle());
                      o.setGoodsMain(goodsMain);
                  }

            }
            return Result.success("ok",list);
        }catch (Exception e){
            log.error("保存当前商品评价信息异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 删除我的评价
     */
    @RequestMapping("delFeedbackMy")
    @ResponseBody
    public Result delFeedbackMy(String id){
        try {
             orderGoodsFeedbackService.delete(id);
            return Result.success("ok");
        }catch (Exception e){
            log.error("保存当前商品评价信息异常",e);
            return Result.error("fail");
        }
    }


    /**
     * 查询评价数量
     */
    @RequestMapping("feedbackCount")
    @ResponseBody
    public Result feedbackCount(String goodsId){
        try {
            Cnd cnd = Cnd.NEW();
            if(!Strings.isEmpty(goodsId)){
                cnd.and("goodsId","=",goodsId);
            }
            cnd.and("delFlag", "=", 0 );
            int num = orderGoodsFeedbackService.count(cnd);
            return Result.success("ok",num);
        }catch (Exception e){
            log.error("查询评价数量信息异常",e);
            return Result.error("fail");
        }
    }





}
