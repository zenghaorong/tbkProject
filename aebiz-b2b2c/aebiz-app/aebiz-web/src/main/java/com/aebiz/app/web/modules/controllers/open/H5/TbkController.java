package com.aebiz.app.web.modules.controllers.open.H5;
import com.aebiz.app.tbk.modules.models.TbkConfig;
import com.aebiz.app.tbk.modules.services.TbkApiService;
import com.aebiz.app.web.commons.utils.TcpipUtil;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.request.TbkDgOptimusMaterialRequest;
import com.taobao.api.request.TbkItemGetRequest;
import com.taobao.api.request.TbkJuTqgGetRequest;
import org.apache.commons.lang.StringUtils;
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
import java.util.Date;
import java.util.Map;

/**
 * @Auther: zenghaorong
 * @Date: 2019/7/27  20:05
 * @Description: 淘宝客API
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/open/h5/tbk")
public class TbkController extends TbkBaseController{

    private static final Log log = Logs.get();

    @Autowired
    private TbkApiService tbkApiService;

    /**
     * 超级搜索api
     * @param request
     * @param cat 类目
     * @return
     */
    @RequestMapping("/getProductList")
    @SJson
    public Object getProductList(HttpServletRequest request,String cat,String q) {
        TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
        try {

            Map<String,Long> mapPage = getTbkPage(request);
            req.setPageNo(mapPage.get("pageNo"));
            req.setPageSize(mapPage.get("pageSize"));

            if(StringUtils.isEmpty(q)){
                q = "日用品";
            }
            if(StringUtils.isNotEmpty(cat)){
//                cat = "16,18"; //女装
                req.setCat(cat);//类目 应该是商品的一些分类
            }
            req.setAdzoneId(TbkConfig.adzone_Id);
            req.setQ(q);

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
            Map<String,Long> mapPage = getTbkPage(request);
            req.setPageNo(mapPage.get("pageNo"));
            req.setPageSize(mapPage.get("pageSize"));

            req.setAdzoneId(TbkConfig.adzone_Id); //mm_xxx_xxx_xxx的第三位
            req.setMaterialId(4094l);//物料id //https://tbk.bbs.taobao.com/detail.html?appId=45301&postId=8576096
            JSONObject jsonObject = tbkApiService.tbkGetLikeProductList(req);
            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }


    /**
     * 获得淘抢购商品
     * @return
     */
    @RequestMapping("/getTqgProductList")
    @SJson
    public Object getTqgProductList(HttpServletRequest request,String ua,String os,String net) {
        TbkJuTqgGetRequest req = new TbkJuTqgGetRequest();
        try {
            Map<String,Long> mapPage = getTbkPage(request);
            req.setPageNo(mapPage.get("pageNo"));
            req.setPageSize(mapPage.get("pageSize"));

            req.setAdzoneId(TbkConfig.adzone_Id); //mm_xxx_xxx_xxx的第三位
            req.setFields("click_url,pic_url,reserve_price,zk_final_price,total_amount,sold_num,title,category_name,start_time,end_time");
            Date date = new Date();
            req.setStartTime(date);//最早开团时间
            req.setEndTime(getNextDate(date)); //最晚开团时间 一天后
            JSONObject jsonObject = tbkApiService.tbkGetTqgProductList(req);
            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }


}
