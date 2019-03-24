package com.aebiz.app.web.modules.controllers.open.H5;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther: zenghaorong
 * @Date: 2019/3/23  20:55
 * @Description:
 */
@Controller
@RequestMapping("/open/h5/niantu")
public class NianTuIndexController {


    private static final Log log = Logs.get();

    /**
     * 进入首页
     * @return
     */
    @RequestMapping("/index.html")
    public String index() {
        return "pages/front/h5/niantu/index";
    }


    /**
     * 进入个人中心页
     */
    @RequestMapping("userCenter.html")
    public String userCenter() {
        return "pages/front/h5/niantu/userCenter";
    }

    /**
     * 进入创意美术
     */
    @RequestMapping("creativeArt.html")
    public String creativeArt() {
        return "pages/front/h5/niantu/creativeArt";
    }

    /**
     * 进入达人秀场
     */
    @RequestMapping("talentShow.html")
    public String talentShow() {
        return "pages/front/h5/niantu/talentShow";
    }

    /**
     * 进入黏土教程
     */
    @RequestMapping("tutorial.html")
    public String tutorial() {
        return "pages/front/h5/niantu/tutorial";
    }

}
