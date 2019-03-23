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
@RequestMapping("/open/h5/niantu/index")
public class NianTuIndexController {


    private static final Log log = Logs.get();

    /**
     * 进入首页
     * @return
     */
    @RequestMapping("")
    public String index() {
        return "pages/front/h5/niantu/index";
    }

    /**
     *
     */


}
