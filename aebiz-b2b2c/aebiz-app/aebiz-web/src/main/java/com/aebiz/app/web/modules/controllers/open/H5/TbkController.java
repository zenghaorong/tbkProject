package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.cms.modules.models.Cms_site;
import com.aebiz.app.tbk.modules.models.TbkApiBaseModel;
import com.aebiz.app.tbk.modules.models.TbkConfig;
import com.aebiz.app.tbk.modules.services.TbkApiService;
import com.aebiz.app.web.commons.utils.TcpipUtil;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.request.TbkDgOptimusMaterialRequest;
import com.taobao.api.request.TbkItemGetRequest;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: zenghaorong
 * @Date: 2019/7/27  20:05
 * @Description: 淘宝客API
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/open/h5/tbk")
public class TbkController {

    private static final Log log = Logs.get();

    @Autowired
    private TbkApiService tbkApiService;

    /**
     * 超级搜索api
     * @param request
     * @param ua
     * @return
     */
    @RequestMapping("/getProductList")
    @SJson
    public Object getProductList(HttpServletRequest request,String ua) {
        TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
        try {
            req.setAdzoneId(TbkConfig.adzone_Id);
            req.setQ("宝马一系");
            req.setCat("16,18");//类目 应该是商品的一些分类
            JSONObject jsonObject = tbkApiService.tbkGetProductList(req);
            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }


    /**
     * 获得到猜你喜欢商品
     * @return
     */
    @RequestMapping("/getYouLikeProductList")
    @SJson
    public Object getYouLikeProductList(HttpServletRequest request,String ua,String os,String net) {
        TbkDgOptimusMaterialRequest req = new TbkDgOptimusMaterialRequest();
        try {
            req.setAdzoneId(TbkConfig.adzone_Id); //mm_xxx_xxx_xxx的第三位
            req.setMaterialId(4094l);//物料id
            JSONObject jsonObject = tbkApiService.tbkGetLikeProductList(req);
            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }




}
