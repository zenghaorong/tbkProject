package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.baseframework.view.annotation.SJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/18  17:19
 * @Description: H5微信相关控制器
 */
@Controller
public class WxH5Controller {


    /**
     * 返回微信验证txt
     */
    @RequestMapping("/MP_verify_suzOajpahiBy7LHb.txt")
    @ResponseBody
    public String verify() {
        //返回的值为：MP_verify_suzOajpahiBy7LHb.txt 里的内容
        return "suzOajpahiBy7LHb";
    }


}
