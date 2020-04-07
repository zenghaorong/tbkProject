package com.aebiz.app.web.modules.controllers.open.caractivity;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.cms.modules.models.Cms_article;
import com.aebiz.app.cms.modules.models.Cms_channel;
import com.aebiz.app.cms.modules.services.CmsArticleService;
import com.aebiz.app.cms.modules.services.CmsChannelService;
import com.aebiz.app.integral.modules.models.Member_Integral;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.app.member.modules.models.Member_coupon;
import com.aebiz.app.msg.modules.services.CommMsgService;
import com.aebiz.app.sys.modules.models.Sys_dict;
import com.aebiz.app.sys.modules.services.SysDictService;
import com.aebiz.app.web.commons.utils.ComposeImageTest;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.Pagination;
import com.aebiz.baseframework.redis.RedisService;
import com.aebiz.baseframework.view.annotation.SJson;
import com.aebiz.commons.utils.DateUtil;
import com.aebiz.commons.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("open/H5/carindex")
public class CarIndexController {


    private static final Log log = Logs.get();

    @Autowired
    private RedisService redisService;

    @Autowired
    private CommMsgService commMsgService;
    @Autowired
    private CmsChannelService cmsChannelService;

    @Autowired
    private AccountUserService accountUserService;
    @Autowired
    private MemberIntegralService memberIntegralService;
    @Autowired
    private CmsArticleService cmsArticleService;
    @Autowired
    private SysDictService sysDictService;

    /**
     * 手机短信验证码前缀
     */
    private final String MOBILE_CAPTCHA = "mobile_sms_captcha_getWxMobileCode";

    //获取验证码重复请求限制
    private final String MOBILE_CAPTCHA_Request_limit = "mobile_sms_captcha_Request_limit_getWxMobileCode";

    /**
     * 微信号绑定手机号获取验证码
     */
    @RequestMapping("/getWxMobileCode")
    @SJson
    public Result getWxMobileCode(@RequestParam("mobile") String mobile) {
        try {

            log.info("微信号绑定手机号获取验证码手机号码："+mobile);
            String key2 = MOBILE_CAPTCHA_Request_limit + mobile;
            String expireTime2 = "60";

            String key = MOBILE_CAPTCHA + mobile;
            String expireTime = "300";
            String code = StringUtil.getRndNumber(6);
            try (Jedis jedis = redisService.jedis()) {
                if(Strings.isNotBlank(jedis.get(key2))){
                    log.info("手机号"+mobile+"60秒内请求了两次");
                    return Result.error("60秒内请求了两次");
                }
                //重复请求限制
                jedis.set(key2, code);
                jedis.expire(key2, Integer.valueOf(expireTime2));

                jedis.set(key, code);
                jedis.expire(key, Integer.valueOf(expireTime));

                //编写发送验证码逻辑
                commMsgService.sendMsgAly(code,mobile);
            }
            log.info("短信验证码:  " + code);
            return Result.success("ok");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("验证码获取失败");
        }
    }


    /**
     * 绑定手机号接口
      * @param accountId
     * @param mobile
     * @param captcha
     * @return
     */
    @RequestMapping("/wxBindMobile")
    @SJson
    public Result wxBindMobile(String accountId,String mobile,String captcha) {

        try (Jedis jedis = redisService.jedis()) {
            if (Strings.isEmpty(mobile) || Strings.isEmpty(captcha)) {
                return Result.error("请求参数为空");
            } else {
                if (!Strings.isMobile(mobile)) {
                    return Result.error("请输入正确的手机号");
                }
            }

            if (!captcha.equals(jedis.get(MOBILE_CAPTCHA + mobile))) {
                return Result.error("验证码不正确");
            }
        }

        try {
            Account_user accountUser = accountUserService.getAccount(accountId);
            accountUser.setMobile(mobile);
            accountUserService.update(accountUser);
            return Result.success("ok");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("绑定失败");
        }

    }

    /**
     * 浏览获取积分
     * @param storeId
     * @param sourceAccountId 推荐人编号
     * @param accountId 浏览人编号
     * @return accountId
     */
    @RequestMapping("/browseForPoints")
    @SJson
    public Result browseForPoints(String storeId,String sourceAccountId,String accountId) {
        try {
            //分享的页面别人浏览了 给推荐人加积分
            memberIntegralService.saveMemberIntegral(storeId,"buyIntegral",
                    6,sourceAccountId);
            return Result.success("ok");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("验证码获取失败");
        }

    }

    /**
     * 获取我的可用积分
     * @param storeId
     * @param accountId 会员编号
     * @return accountId
     */
    @RequestMapping("/getMyJf")
    @SJson
    public Result getMyJf(String storeId,String accountId) {
        try {
            //查询会员积分
            Cnd cndMi = Cnd.NEW();
            cndMi.and("storeId","=", storeId);
            cndMi.and("customerUuid","=",accountId);
            Member_Integral memberIntegral = memberIntegralService.fetch(cndMi);
            return Result.success("ok",memberIntegral);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("验证码获取失败");
        }

    }

    /**
     * 获取cms描述数据
     * @param storeId
     * @return accountId
     */
    @RequestMapping("/getCmsData")
    @SJson
    public Result getCmsData(String storeId,String name) {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("storeId","=",storeId);
            cnd.and("name","=",name);
            Cms_channel cms_channels = cmsChannelService.fetch(cnd);
            Cnd cnd2 = Cnd.NEW();
            cnd2.and("storeId","=",storeId);
            cnd2.and("channelId","=",cms_channels.getId());
            List<Cms_article> cms_articleList = cmsArticleService.query(cnd2);
            return Result.success("ok",cms_articleList.get(0));
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("验证码获取失败");
        }

    }

    /**
     * 获取微信分享描述
     * @return
     */
    @RequestMapping("getWxDesc")
    @SJson
    public Result getWxDesc(String storeId) {
        try {
            Map<String,Object> map = new HashMap<>();
            // 已报名用户数
            Sys_dict sys_dict_ybmyhs = sysDictService.getSysDictByCode("wxfengxiang_title",storeId);
            map.put("title",sys_dict_ybmyhs.getValue());

            // 已分享人数
            Sys_dict sys_dict_yfxrs = sysDictService.getSysDictByCode("weixingfengxiang_desc",storeId);
            map.put("desc",sys_dict_yfxrs.getValue());

            return Result.success("ok",map);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("获取活动下优惠券获取失败");
        }
    }

    /**
     * 图片合成算法-返回生成海报
     * @return
     */
    @RequestMapping("/generatePoster")
    @SJson
    public Result generatePoster(String storeId,String accountId,String myUrl,String nickName) {
        try {
            //1.查询改商户配置的公共海报图片
//            Cnd cnd = Cnd.NEW();
//            cnd.and("storeId","=",storeId);
//            cnd.and("name","=","微信海报配置");
//            Cms_channel cms_channels = cmsChannelService.fetch(cnd);
//            Cnd cnd2 = Cnd.NEW();
//            cnd2.and("storeId","=",storeId);
//            cnd2.and("channelId","=",cms_channels.getId());
//            List<Cms_article> cms_articleList = cmsArticleService.query(cnd2);
//            Cms_article cms_article = cms_articleList.get(0);
//            String mainImgUrl = cms_article.getPicurl();
//            if(StringUtils.isEmpty(mainImgUrl)){
//                return Result.error("cms未配置海报图片");
//            }
            //配置文件读取该商户海报二维码配置参数
            Sys_dict sys_dict_ybmyhs = sysDictService.getSysDictByCode("hchbcspz",storeId);
            String conf = sys_dict_ybmyhs.getValue();
            //配置文件读取该商户海报水印文字配置参数
            Sys_dict sys_dict_ybmyhsText = sysDictService.getSysDictByCode("hchbcspzText",storeId);
            String confText = sys_dict_ybmyhsText.getValue();
            String hbName = ComposeImageTest.generatePosterImg(storeId,accountId,myUrl,conf,confText,nickName);
            return Result.success("ok",hbName);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("图片合成算法异常");
        }
    }


}
