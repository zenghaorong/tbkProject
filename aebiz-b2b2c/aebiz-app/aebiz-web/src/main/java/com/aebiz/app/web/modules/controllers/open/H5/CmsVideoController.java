package com.aebiz.app.web.modules.controllers.open.H5;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther: zenghaorong
 * @Date: 2019/3/23  20:54
 * @Description:
 */
@Controller
@RequestMapping("/open/h5/cms")
public class CmsVideoController {

    /**
     * 进入视频列表页
     */
    @RequestMapping("/video.html")
    public String index() {
        return "pages/front/h5/niantu/videoList";
    }



}
