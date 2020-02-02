package com.aebiz.app.web.modules.controllers.open.H5;
import com.aebiz.app.tbk.modules.models.TbkConfig;
import com.aebiz.app.tbk.modules.services.TbkApiService;
import com.aebiz.app.web.commons.utils.TcpipUtil;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.request.*;
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
     * @param sort 排序 筛选
     * @return
     */
    @RequestMapping("/getProductList")
    @SJson
    public Object getProductList(HttpServletRequest request,String cat,String q,String sort) {
        TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
        try {

            Map<String,Long> mapPage = getTbkPage(request);
            req.setPageNo(mapPage.get("pageNo"));
            req.setPageSize(mapPage.get("pageSize"));

            if(StringUtils.isNotEmpty(q)){
                req.setQ(q);
            }
            if(StringUtils.isNotEmpty(cat)){
//                cat = "16,18"; //女装 tbk_cat表
                //类目 应该是商品的一些分类
                req.setCat(cat);
            }
            if(StringUtils.isNotEmpty(sort)){
                req.setSort(sort);
            }
            req.setAdzoneId(TbkConfig.adzone_Id);


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
            //mm_xxx_xxx_xxx的第三位
            req.setAdzoneId(TbkConfig.adzone_Id);
            //物料id //https://tbk.bbs.taobao.com/detail.html?appId=45301&postId=8576096
            req.setMaterialId(4094L);
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

    /**
     * 聚划算商品搜索
     * @param request
     * @param cid //淘宝类目编号
     * @param str //搜索字段
     * @return
     */
    @RequestMapping("/getJhsProductList")
    @SJson
    public Object getJhsProductList(HttpServletRequest request,Long cid,String str) {
        JuItemsSearchRequest req = new JuItemsSearchRequest ();
        try {
            Map<String,Long> mapPage = getTbkPage(request);
            JuItemsSearchRequest.TopItemQuery obj1 = new JuItemsSearchRequest.TopItemQuery();
            obj1.setCurrentPage(mapPage.get("pageNo"));
            obj1.setPageSize(mapPage.get("pageSize"));
            obj1.setPid(TbkConfig.adzone_Id+"");
            obj1.setPostage(true);
//            obj1.setStatus(2L);
            if(cid !=null){
                obj1.setTaobaoCategoryId(cid);
            }

            if(StringUtils.isNotEmpty(str)){
                obj1.setWord(str);
            }

            req.setParamTopItemQuery(obj1);
            JSONObject jsonObject = tbkApiService.tbkGetJhsProductList(req);
            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }

    /**
     * 图文内容输出
     * @param type 内容类型，1:图文、2: 图集、3: 短视频
     * @return
     */
    @RequestMapping("/getContentList")
    @SJson
    public Object getContentList(Long type,Long before_timestamp,Long count) {
        TbkContentGetRequest req = new TbkContentGetRequest();
        try {
            if(type == null){
                type = 1L;
            }
            req.setType(type); //内容类型，1:图文、2: 图集、3: 短视频
            req.setBeforeTimestamp(before_timestamp); //表示取这个时间点以前的数据，
            // 以毫秒为单位（出参中的last_timestamp是指本次返回的内容中最早一条的数据，
            // 下一次作为before_timestamp传过来，即可实现翻页）
            req.setAdzoneId(TbkConfig.adzone_Id); //mm_xxx_xxx_xxx的第三位
            req.setCount(count);//期望获得条数
            JSONObject jsonObject = tbkApiService.tbkGetContentList(req);
            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }

    /**
     * taobao.tbk.uatm.favorites.get( 淘宝客-推广者-选品库宝贝列表 )
     * 默认值-1；选品库类型，1：普通选品组，2：高佣选品组，-1，同时输出所有类型的选品组
     */
    @RequestMapping("/getFavoritesList")
    @SJson
    public Object getFavoritesList(HttpServletRequest request,Long type) {
        TbkUatmFavoritesGetRequest req = new TbkUatmFavoritesGetRequest();
        try {
            if(type == null){
                type = -1L;
            }
            Map<String,Long> mapPage = getTbkPage(request);
            req.setPageNo(mapPage.get("pageNo"));
            req.setPageSize(mapPage.get("pageSize"));
            req.setFields("favorites_title,favorites_id,type");
            req.setType(type);
            JSONObject jsonObject = tbkApiService.tbkGetFavoritesList(req);
            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }


    /**
     * taobao.tbk.uatm.favorites.item.get( 淘宝客-推广者-选品库宝贝信息 )
     */
    @RequestMapping("/getFavoritesProductList")
    @SJson
    public Object getFavoritesProductList(HttpServletRequest request,Long fid) {
        TbkUatmFavoritesItemGetRequest req = new TbkUatmFavoritesItemGetRequest();
        try {
            Map<String,Long> mapPage = getTbkPage(request);
            req.setPageNo(mapPage.get("pageNo"));
            req.setPageSize(mapPage.get("pageSize"));
            //选品库编号
            req.setFavoritesId(fid);
            //mm_xxx_xxx_xxx的第三位
            req.setAdzoneId(TbkConfig.adzone_Id);
            req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick,shop_title,zk_final_price_wap,event_start_time,event_end_time,tk_rate,status,type");
            JSONObject jsonObject = tbkApiService.tbkGetFavoritesProductList(req);
            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }

    /**
     * taobao.tbk.coupon.get( 淘宝客-公用-阿里妈妈推广券详情查询 )
     */
    @RequestMapping("/getCouponIno")
    @SJson
    public Object getCouponIno() {
        TbkCouponGetRequest req = new TbkCouponGetRequest();
        try {
            req.setMe("nfr%2BYTo2k1PX18gaNN%2BIPkIG2PadNYbBnwEsv6mRavWieOoOE3L9OdmbDSSyHbGxBAXjHpLKvZbL1320ML%2BCF5FRtW7N7yJ056Lgym4X01A%3D");
            req.setItemId(123L);
            req.setActivityId("sdfwe3eefsdf");
            JSONObject jsonObject = tbkApiService.tbkGetCouponInfo(req);
            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }

    /**
     * taobao.tbk.tpwd.create( 淘宝客-公用-淘口令生成 )
     */
    @RequestMapping("/getTpwdCreate")
    @SJson
    public Object getTpwdCreate() {
        TbkTpwdCreateRequest req = new TbkTpwdCreateRequest();
        try {
            req.setUserId("123");
            req.setText("长度大于5个字符");
            req.setUrl("https://uland.taobao.com/");
            req.setLogo("https://uland.taobao.com/");
            req.setExt("{}");
            JSONObject jsonObject = tbkApiService.tbkGetTpwdCreate(req);
            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }

    /**
     * taobao.tbk.sc.invitecode.get( 淘宝客-公用-私域用户邀请码生成 )
     */
    @RequestMapping("/getScInvitecode")
    @SJson
    public Object getScInvitecode() {
        TbkScInvitecodeGetRequest req = new TbkScInvitecodeGetRequest();
        try {
            req.setRelationId(11L);
            req.setRelationApp("common");
            req.setCodeType(1L);
            JSONObject jsonObject = tbkApiService.tbkGetScInvitecode(req);
            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }

    /**
     * taobao.tbk.dg.vegas.tlj.create( 淘宝客-推广者-淘礼金创建 )
     *
     */
    @RequestMapping("/createTljVegas")
    @SJson
    public Object createTljVegas(String campaign_type,String item_id,
                                 String total_num,String name,String user_total_win_num_limit,
                                 String security_switch,String per_face,String send_start_time,
                                 String send_end_time,String use_end_time,String use_end_time_mode,
                                 String use_start_time) {
        TbkDgVegasTljCreateRequest req = new TbkDgVegasTljCreateRequest();
        try {
            JSONObject jsonObject = tbkApiService.tbkCreateTljVegas(req);

            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }



}
