package com.aebiz.app.web.modules.controllers.open.H5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: zenghaorong
 * @Date: 2019/5/9  12:00
 * @Description:
 */
@Controller
public class IndexController {

    /**
     * 进入首页
     * @return
     */
    @RequestMapping("/index")
    public String goIndex(String code, HttpServletRequest request) {
        return "pages/front/h5/niantu/index";
    }

}
