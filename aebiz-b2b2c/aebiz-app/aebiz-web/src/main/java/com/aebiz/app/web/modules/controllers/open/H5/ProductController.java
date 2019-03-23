package com.aebiz.app.web.modules.controllers.open.H5;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther: zenghaorong
 * @Date: 2019/3/23  21:39
 * @Description:
 */
@Controller
@RequestMapping("/open/h5/product")
public class ProductController {

    /**
     * 进入商品列表页  或 进入宝妈专区
     */
    @RequestMapping("/list.html")
    public String index() {
        return "pages/front/h5/niantu/productList";
    }






}
