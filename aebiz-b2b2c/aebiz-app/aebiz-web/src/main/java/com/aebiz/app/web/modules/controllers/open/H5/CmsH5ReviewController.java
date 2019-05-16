package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.cms.modules.models.*;
import com.aebiz.app.cms.modules.services.CmsArticleService;
import com.aebiz.app.cms.modules.services.CmsLoveService;
import com.aebiz.app.cms.modules.services.CmsReviewService;
import com.aebiz.app.cms.modules.services.CmsVideoService;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.app.web.commons.utils.CalculateUtils;
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

import java.util.ArrayList;
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

    @Autowired
    private CmsArticleService cmsArticleService;

    @Autowired
    private CmsVideoService cmsVideoService;

    @Autowired
    private CmsLoveService cmsLoveService;

    @Autowired
    private MemberIntegralService memberIntegralService;


    /**
     * 获取当前内容下的评论和回复
     */
    @RequestMapping("getReview.html")
    @SJson
    public Result getReview(String cmsId){
        try {
            //查询评论
            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", 0 );
            cnd.and("type", "=", "1" ); // 类型 1评论 2回复
            cnd.and("cmsId", "=", cmsId );
            List<Cms_review> list = cmsReviewService.query(cnd);
            List<Cms_review> cms_reviewList = new ArrayList<>();
            for (Cms_review cms_review : list) {
                //查看回复
                Cnd replyCnd = Cnd.NEW();
                replyCnd.and("delFlag", "=", 0 );
                replyCnd.and("type", "=", "2" ); // 类型 1评论 2回复
                replyCnd.and("cmsId", "=", cmsId );
                replyCnd.and("reviewId", "=", cms_review.getId());
                List<Cms_review> replyList = cmsReviewService.query(replyCnd);
                List<ReplyVO> replyVOList = new ArrayList<>();
                for (Cms_review r:replyList) {
                    ReplyVO replyVO = new ReplyVO();
                    replyVO.setContent(r.getContent());
                    replyVO.setReviewFatherName(r.getReviewFatherName());
                    replyVO.setReviewOpName(r.getReviewOpName());
                    replyVO.setReviewFatherId(r.getReviewFatherId());
                    replyVOList.add(replyVO);
                }
                cms_review.setReplyVOList(replyVOList);
                cms_reviewList.add(cms_review);
            }
            return Result.success("ok",cms_reviewList);
        } catch (Exception e) {
            log.error("获取评论回复信息异常",e);
            return Result.error("fail");
        }
    }


    /**
     * 评论接口
     */
    @RequestMapping("review.html")
    @SJson
    public Result review(String cmsId,String cmsTitle,String content){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            if(accountUser == null){
                return Result.error(2,"请先登录");
            }
            Cms_review cms_review = new Cms_review();
            cms_review.setCmsId(cmsId);
            cms_review.setIsStore("2");
            cms_review.setType("1");
            cms_review.setCmsTitle(cmsTitle);
            cms_review.setContent(content);
            cms_review.setReviewOpName(accountUser.getMobile());
            cms_review.setReviewOpId(accountUser.getAccountId());
            cmsReviewService.insert(cms_review);

            //添加评论量
            Cms_article cms_article = cmsArticleService.fetch(cmsId);

            int num = 0;
            if( cms_article.getEvaluateNum()!=null){
                num = cms_article.getEvaluateNum();
            }
            num++;
            cms_article.setEvaluateNum(num);
            cmsArticleService.update(cms_article);

            try {
                memberIntegralService.addMemberIntegral(accountUser.getAccountId(), "3", "2", null);
            }catch (Exception e){
                log.error("回调给会员添加积分异常",e);
            }

            return Result.success("ok");
        } catch (Exception e) {
            log.error("发布评论异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 回复接口
     */
    @RequestMapping("reply.html")
    @SJson
    public Result reply(String cmsId,String cmsTitle,String content,
                        String reviewId,String reviewReplyId,String reviewFatherName,String reviewFatherId){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            if(accountUser == null){
                return Result.error(2,"请先登录");
            }
            Cms_review cms_review = new Cms_review();
            cms_review.setCmsId(cmsId);
            cms_review.setIsStore("2");
            cms_review.setType("2");
            cms_review.setCmsTitle(cmsTitle);
            cms_review.setContent(content);
            cms_review.setReviewOpName(accountUser.getMobile());
            cms_review.setReviewOpId(accountUser.getAccountId());
            cms_review.setReviewId(reviewId);
            cms_review.setReviewFatherName(reviewFatherName);
            cms_review.setReviewFatherId(reviewFatherId);
            cms_review.setReviewReplyId(reviewReplyId);
            cmsReviewService.insert(cms_review);

            //添加评论量
            Cms_article cms_article = cmsArticleService.fetch(cmsId);
            int num = cms_article.getEvaluateNum();
            num++;
            cms_article.setEvaluateNum(num);
            cmsArticleService.update(cms_article);
            return Result.success("ok");
        } catch (Exception e) {
            log.error("发布评论异常",e);
            return Result.error("fail");
        }
    }


    /**
     * 内容点赞接口
     */
    @RequestMapping("cmsLike.html")
    @SJson
    public Result cmsLike(String cmsId){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            if(accountUser == null){
                return Result.error(2,"请先登录");
            }

            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", 0 );
            cnd.and("cmsType", "=", "1" );
            cnd.and("cmsId", "=", cmsId );
            cnd.and("accountId", "=", accountUser.getAccountId() );
            List<Cms_love> loves = cmsLoveService.query(cnd);
            if(loves!=null && loves.size()>0){
                //进行取消点赞
                Cms_love cms_love = loves.get(0);
                cmsLoveService.delete(cms_love.getId());
                Cms_article cms_article = new Cms_article();
                cms_article.setId(cmsId);
                cms_article.setLikeNum(loves.size());
                cmsArticleService.update(cms_article);
                return Result.success("ok2");
            }else {
                Cms_article cms_article = cmsArticleService.fetch(cmsId);
                int num =0;
                if( cms_article.getLikeNum() !=null){
                    num = cms_article.getLikeNum();
                }
                num++;
                cms_article.setLikeNum(num);
                cmsArticleService.update(cms_article);

                //添加点赞记录
                Cms_love cms_love = new Cms_love();
                cms_love.setAccountId(accountUser.getAccountId());
                cms_love.setCmsType("1");
                cms_love.setCmsId(cmsId);
                cmsLoveService.insert(cms_love);
                return Result.success("ok1");
            }

        } catch (Exception e) {
            log.error("内容点赞异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 查询当前会员是否点赞
     */
    @RequestMapping("queryIsLike.html")
    @SJson
    public Result queryIsLike(String cmsId){
        try {
            Subject subject = SecurityUtils.getSubject();
            Account_user accountUser = (Account_user) subject.getPrincipal();
            if(accountUser == null){
                return Result.error(2,"请先登录");
            }

            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", 0 );
            cnd.and("cmsType", "=", "1" );
            cnd.and("cmsId", "=", cmsId );
            cnd.and("accountId", "=", accountUser.getAccountId() );
            List<Cms_love> loves = cmsLoveService.query(cnd);
            if(loves!=null && loves.size()>0){

                return Result.success("ok");
            }else {

                return Result.error(3,"未点赞");
            }

        } catch (Exception e) {
            log.error("查询点赞异常",e);
            return Result.error("fail");
        }
    }
    /**
     * 增加文章浏览量接口
     */
    @RequestMapping("addPageViews.html")
    @SJson
    public Result addPageViews(String cmsId){
        try {
//            Subject subject = SecurityUtils.getSubject();
//            Account_user accountUser = (Account_user) subject.getPrincipal();
//            if(accountUser == null){
//                return Result.error(2,"请先登录");
//            }
            Cms_article cms_article = cmsArticleService.fetch(cmsId);
            if(cms_article!=null) {
                int num = 0;
                if (cms_article.getPageViews() != null) {
                    num = cms_article.getPageViews();
                }
                num++;
                cms_article.setPageViews(num);
                cmsArticleService.update(cms_article);
            }

            return Result.success("ok");
        } catch (Exception e) {
            log.error("增加文章浏览量接口异常",e);
            return Result.error("fail");
        }
    }


}
