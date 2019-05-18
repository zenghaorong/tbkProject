package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_info;
import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountInfoService;
import com.aebiz.app.cms.modules.models.Cms_article;
import com.aebiz.app.cms.modules.models.Cms_channel;
import com.aebiz.app.cms.modules.models.Cms_video;
import com.aebiz.app.cms.modules.services.CmsArticleService;
import com.aebiz.app.cms.modules.services.CmsChannelService;
import com.aebiz.app.cms.modules.services.CmsVideoService;
import com.aebiz.app.order.modules.models.Order_goods;
import com.aebiz.app.order.modules.models.Order_main;
import com.aebiz.app.order.modules.models.em.OrderPayStatusEnum;
import com.aebiz.app.order.modules.models.em.OrderTypeEnum;
import com.aebiz.app.order.modules.services.OrderGoodsService;
import com.aebiz.app.order.modules.services.OrderMainService;
import com.aebiz.app.shop.modules.models.Shop_adv_main;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.Pagination;
import com.aebiz.baseframework.view.annotation.SJson;
import com.aebiz.commons.utils.DateUtil;
import com.aebiz.commons.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther: zenghaorong
 * @Date: 2019/3/23  20:54
 * @Description:
 */
@Controller
@RequestMapping("/open/h5/cms")
public class CmsVideoController {
    private static final Log log = Logs.get();

    @Autowired
    private CmsVideoService cmsVideoService;

    @Autowired
    private CmsChannelService cmsChannelService;

    @Autowired
    private CmsArticleService cmsArticleService;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private OrderMainService orderMainService;

    @Autowired
    private AccountInfoService accountInfoService;

    /**
     * 进入视频列表页
     */
    @RequestMapping("/video.html")
    public String index() {
        return "pages/front/h5/niantu/videoList";
    }

    /**
     * 获得视频列表
     * @param pageNumber 页码
     * @return
     */
    @RequestMapping("videoList.html")
    @SJson
    public Result getVideoList(Integer pageNumber,HttpServletRequest request){
        try {
            if(pageNumber == null){
                pageNumber = 0;
            }
            Cnd cnd = Cnd.NEW();
            String key = request.getParameter("key");
            if(StringUtils.isNotEmpty(key)){
                cnd.and("videoTitle","like","%"+key+"%");
            }

            cnd.and("delFlag", "=", 0 );
            cnd.desc("sort");

//            Pagination pagination = cmsVideoService.listPage(pageNumber,15,cnd,"^(id|videoTitle|imageUrl|price)$");
            List<Cms_video> cms_videoList = cmsVideoService.query(cnd,"^(id|videoTitle|imageUrl|price)$");
            return Result.success("ok",cms_videoList);
        } catch (Exception e) {
            log.error("获取视频列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 获得视频详情信息
     * @return
     */
    @RequestMapping("videoDetail.html")
    @SJson
    public Result getVideoDetail(String id){
        try {

            Cms_video cms_video = cmsVideoService.fetch(id);
//            cms_video.setVideoUrl("");
            return Result.success("ok",cms_video);
        } catch (Exception e) {
            log.error("获取视频详情异常",e);
            return Result.error("fail");
        }
    }


    /**
     * 获得 黏土教程 ，达人秀场 ，创意美术 列表
     * @param pageNumber 页码
     * @return
     */
    @RequestMapping("cmsListByType.html")
    @SJson
    public Result cmsListByType(Integer pageNumber,String typeName,String key,String accountId){
        try {
            //根据名称查询对应的栏目编号

            Cnd cnd = Cnd.NEW();
            String str="";
            if("cyms".equals(typeName)){
                str = "创意美术";
            }
            if("drxc".equals(typeName)){
                str = "达人秀场";
                if(!Strings.isEmpty(accountId)){
                    cnd.and("author", "=", accountId );
                }
            }
            if("ntjc".equals(typeName)){
                str = "黏土教程";
            }
            Cnd c = Cnd.NEW();
            c.and("name", "=", str );
            Cms_channel cms_channel = cmsChannelService.fetch(c);

            if(pageNumber == null){
                pageNumber = 0;
            }

            cnd.and("delFlag", "=", 0 );
            cnd.and("disabled", "=", 0 );
            cnd.and("channelId","=",cms_channel.getId());
            cnd.desc("location");
            if(StringUtils.isNotEmpty(key)){
                cnd.and("title","like","%"+key+"%");
            }
            List<Cms_article> list = cmsArticleService.query(cnd);
            if("达人秀场".equals(str)) {
                for (Cms_article cms_article : list) {
                    Account_info account_info = accountInfoService.fetch(cms_article.getAuthor());
                    cms_article.setAccount_info(account_info);
                }
            }
            return Result.success("ok",list);
        } catch (Exception e) {
            log.error("获取图文列表异常",e);
            return Result.error("fail");
        }
    }


    /**
     * 获得 黏土教程 ，达人秀场 ，创意美术 详情信息
     * @return
     */
    @RequestMapping("cmsArticle.html")
    @SJson
    public Result cmsArticle(String id){
        try {
            Cms_article cms_article = cmsArticleService.fetch(id);
            return Result.success("ok",cms_article);
        } catch (Exception e) {
            log.error("获取图文详情异常",e);
            return Result.error("fail");
        }
    }


    /**
     * 进入视频购买详情页
     */
    @RequestMapping("videoBuyPage.html")
    public String videoBuyPage(String id, HttpServletRequest req){
        req.setAttribute("id",id);
        Account_user accountUser = new Account_user();
        try {
            Subject subject = SecurityUtils.getSubject();
            accountUser = (Account_user) subject.getPrincipal();
            if(accountUser==null){
                return "pages/front/h5/niantu/login";
            }
        }catch (Exception e){
            return "pages/front/h5/niantu/login";
        }

        try {
            Cms_video cms_video = cmsVideoService.fetch(id);
            Integer num = cms_video.getPageViews();
            if (num == null) {
                num = 0;
            }
            num++;
            cms_video.setPageViews(num);
            cmsVideoService.update(cms_video);
        }catch (Exception e){
            log.error("添加视频浏览浪异常",e);
            e.printStackTrace();
        }

        //先判断是否开通包月
        Cnd cndM = Cnd.NEW();
        cndM.and("payStatus", "=", OrderPayStatusEnum.PAYALL.getKey() );
        cndM.and("accountId", "=", accountUser.getAccountId());
        cndM.and("orderType", "=", OrderTypeEnum.monthly_order_type.getKey());
        cndM.and("delFlag", "=", 0);
        cndM.desc("payAt");
        List<Order_main> list = orderMainService.query(cndM);
        if(list !=null && list.size()>0){
            Order_main order_main = list.get(0);
            boolean is = this.videoMonthlyTime(order_main.getPayAt().toString(),order_main.getMonthlyNum());
            if(is){
                return "pages/front/h5/niantu/videoDetail";
            }
        }

        //判断是否购买 从而判断进入购买页还是详情页
        Cnd cnd = Cnd.NEW();
        cnd.and("payStatus", "=", OrderPayStatusEnum.PAYALL.getKey() );
        cnd.and("accountId", "=", accountUser.getAccountId());
        cnd.and("orderType", "=", OrderTypeEnum.video_order_type.getKey());
        cnd.and("videoId", "=", id);
        cnd.and("delFlag", "=", 0);
        int orderSize =orderMainService.count(cnd);
        if(orderSize>0){ //进入播放详情页
            return "pages/front/h5/niantu/videoDetail";
        }


        return "pages/front/h5/niantu/videoBuyPage";
    }

    /**
     * 进入视频详情页
     */
    @RequestMapping("goVideoDetail.html")
    public String goVideoDetail(String id, HttpServletRequest req){
        req.setAttribute("id",id);

        Cms_video cms_video = cmsVideoService.fetch(id);
        Integer num = cms_video.getLikeNum();
        if(num == null){
            num = 0;
        }
        num++;
        cms_video.setLikeNum(num);
        cmsVideoService.update(cms_video);
        return "pages/front/h5/niantu/videoDetail";
    }

    /**
     * 获取视频购买详情页
     */
    @RequestMapping("getVideoDesc.html")
    @SJson
    public Result getVideoDesc(String id){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();

            Cms_video cms_video = new Cms_video();

            //先判断是否开通包月
            Cnd cndM = Cnd.NEW();
            cndM.and("payStatus", "=", OrderPayStatusEnum.PAYALL.getKey() );
            cndM.and("accountId", "=", accountUser.getAccountId());
            cndM.and("orderType", "=", OrderTypeEnum.monthly_order_type.getKey());
            cndM.and("delFlag", "=", 0);
            cndM.desc("payAt");
            List<Order_main> list = orderMainService.query(cndM);
            if(list !=null && list.size()>0){
                Order_main order_main = list.get(0);
                boolean is = this.videoMonthlyTime(order_main.getPayAt().toString(),order_main.getMonthlyNum());
                if(is){
                    cms_video=cmsVideoService.getField("^(id|videoTitle|videoDetails|imageUrl|price|opAt|videoUrl)$",id);
                    return Result.success("ok",cms_video);
                }
            }

            Cnd cnd = Cnd.NEW();
            cnd.and("payStatus", "=", OrderPayStatusEnum.PAYALL.getKey() );
            cnd.and("accountId", "=", accountUser.getAccountId());
            cnd.and("orderType", "=", "2");
            cnd.and("videoId", "=", id);
            cnd.and("delFlag", "=", 0);
            int orderSize =orderMainService.count(cnd);
            if(orderSize>0){//这里做一个判断 如果当前账号有此视频订单就把url地址返回
                cms_video=cmsVideoService.getField("^(id|videoTitle|videoDetails|imageUrl|price|opAt|videoUrl)$",id);
            }else{
                cms_video=cmsVideoService.getField("^(id|videoTitle|videoDetails|imageUrl|price|opAt)$",id);
            }
            return Result.success("ok",cms_video);
        } catch (Exception e) {
            log.error("获取图文详情异常",e);
            return Result.error("fail");
        }
    }


    /**
     * 进入我的视频列表
     */

    @RequestMapping("/goMyVideoList.html")
    public String goMyVideoList() {

        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if(accountUser==null){
            return "pages/front/h5/niantu/login";
        }
        return "pages/front/h5/niantu/myVideoList";
    }

    /**
     * 获得我的视频列表
     * @return
     */
    @RequestMapping("myVideoList.html")
    @SJson
    public Result getMyVideoList(){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();

            Cnd cndMain = Cnd.NEW();
            cndMain.and("payStatus", "=", OrderPayStatusEnum.PAYALL.getKey());
            cndMain.and("orderType", "=", OrderTypeEnum.video_order_type.getKey());
            cndMain.and("accountId", "=", accountUser.getAccountId());
            List<Order_main> order_mainList = orderMainService.query(cndMain);

            List<Order_goods> order_goodsList = new ArrayList<>();
            for (Order_main o:order_mainList){
                Cnd cndOrder = Cnd.NEW();
//                cndOrder.and("orderType", "=", OrderTypeEnum.video_order_type.getKey());
                cndOrder.and("orderId","=",o.getId());
                Order_goods order_goods = orderGoodsService.fetch(cndOrder);
                order_goodsList.add(order_goods);
            }

            List<Cms_video> cms_videoList = new ArrayList<>();
            for (Order_goods ol : order_goodsList){
                Cms_video cms_video = cmsVideoService.fetch(ol.getProductId());
                cms_videoList.add(cms_video);
            }
            return Result.success("ok",cms_videoList);
        } catch (Exception e) {
            log.error("获取视频列表异常",e);
            return Result.error("fail");
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
            return false;
        }
        return true;
    }


    /**
     * 测试日历
     */

    @RequestMapping("/ceshirili")
    @SJson
    public String ceshirili() {
        //到期时间
        String nowDate = null;
        String date = ""; //开始时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = format.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            calendar.add(Calendar.MONTH, 1);
            nowDate = format.format(calendar.getTime());
            System.out.println("到期时间:"+nowDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        String str = DateUtil.getDate(1558086430);
        System.out.println("----:"+str);
    }


}
