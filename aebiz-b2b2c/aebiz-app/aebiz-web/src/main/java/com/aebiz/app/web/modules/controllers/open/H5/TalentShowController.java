package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.cms.modules.models.Cms_article;
import com.aebiz.app.cms.modules.models.Cms_channel;
import com.aebiz.app.cms.modules.models.Cms_video;
import com.aebiz.app.cms.modules.services.CmsArticleService;
import com.aebiz.app.cms.modules.services.CmsChannelService;
import com.aebiz.app.order.modules.models.em.OrderPayStatusEnum;
import com.aebiz.app.web.commons.utils.WXPayUtil;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import com.aebiz.commons.utils.DateUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: zenghaorong
 * @Date: 2019/5/8  17:10
 * @Description: 达人秀场
 */
@Controller
@RequestMapping("/open/h5/show")
public class TalentShowController {
    private static final Log log = Logs.get();

    @Autowired
    private CmsArticleService cmsArticleService;

    @Autowired
    private CmsChannelService cmsChannelService;

    /**
     * 进入达人秀场发布页
     */
    @RequestMapping("/goAddShow.html")
    public String goAddShow(){
        Subject subject = SecurityUtils.getSubject();
        Account_user accountUser = (Account_user) subject.getPrincipal();
        if(accountUser==null){
            return "pages/front/h5/niantu/login";
        }
        return "pages/front/h5/niantu/talentShowAdd";
    }

    /**
     * 进入达人秀场详情页
     */
    @RequestMapping("/goShowInfo.html")
    public String goShowInfo(String id,HttpServletRequest req){
        req.setAttribute("id",id);
        return "pages/front/h5/niantu/talentShowInfo";
    }

    /**
     * 进入我的达人秀场列表页
     */
    @RequestMapping("/goShowListMy.html")
    public String goShowListMy(String id,HttpServletRequest req){
        req.setAttribute("id",id);
        return "pages/front/h5/niantu/talentShowListMy";
    }

    /**
     * 进入我的达人秀场详情页
     */
    @RequestMapping("/goShowInfoMy.html")
    public String goShowInfoMy(String id,HttpServletRequest req){
        req.setAttribute("id",id);
        return "pages/front/h5/niantu/talentShowInfoMy";
    }

    /**
     * 保存达人秀场
     */
    @RequestMapping("/addShow.html")
    @SJson
    public Result addShow(String title,String content,String imageUrlStrs,String picUrl){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            if (accountUser == null) {
                Result.error("登录失效");
            }
            Cnd c = Cnd.NEW();
            c.and("name", "=", "达人秀场" );
            Cms_channel cms_channel = cmsChannelService.fetch(c);

            Cms_article cms_article = new Cms_article();
            cms_article.setTitle(title);
            cms_article.setInfo(content);
            cms_article.setContent(content);
            cms_article.setChannelId(cms_channel.getId());
            cms_article.setAuthor(accountUser.getAccountId());
            cms_article.setImageUrlStrs(imageUrlStrs);
            cms_article.setPicurl(picUrl);
            int at=(int)WXPayUtil.getCurrentTimestamp();
            cms_article.setPublishAt(at);
            cms_article.setDisabled(false);

            cmsArticleService.insert(cms_article);
            return  Result.success();
        }catch (Exception e){
            log.error("保存达人秀场异常",e);
        }
        return  Result.error("fail");
    }

    /**
     * 用户删除达人秀场
     */
    @RequestMapping("/removeShow.html")
    @SJson
    public Result removeShow(String id){
        try {
            Cms_article cms_article = cmsArticleService.fetch(id);
            cms_article.setDelFlag(true);
            cmsArticleService.update(cms_article);
            return  Result.success();
        }catch (Exception e){
            log.error("用户删除达人秀场异常",e);
        }
        return  Result.error("fail");
    }



}
