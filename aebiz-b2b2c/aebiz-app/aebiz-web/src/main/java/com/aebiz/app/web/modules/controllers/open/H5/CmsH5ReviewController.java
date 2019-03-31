package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.cms.modules.models.Cms_article;
import com.aebiz.app.cms.modules.models.Cms_review;
import com.aebiz.app.cms.modules.services.CmsReviewService;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Auther: zenghaorong
 * @Date: 2019/3/31  12:23
 * @Description:  社区模块
  */
@Controller
@RequestMapping("/open/h5/cms")
public class CmsH5ReviewController {

    private static final Log log = Logs.get();

    @Autowired
    private CmsReviewService cmsReviewService;


    /**
     * 获取当前内容下的评论
     */
    @RequestMapping("get.html")
    @SJson
    public Result cmsArticle(String id){
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", 0 );
            cnd.desc("sort");
            List<Cms_review> list = cmsReviewService.query(cnd);
            return Result.success("ok",list);
        } catch (Exception e) {
            log.error("获取图文详情异常",e);
            return Result.error("fail");
        }
    }
}
