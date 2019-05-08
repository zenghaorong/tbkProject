package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.goods.modules.models.Goods_image;
import com.aebiz.app.goods.modules.models.Goods_main;
import com.aebiz.app.goods.modules.models.Goods_product;
import com.aebiz.app.goods.modules.services.GoodsImageService;
import com.aebiz.app.goods.modules.services.GoodsProductService;
import com.aebiz.app.goods.modules.services.GoodsService;
import com.aebiz.app.web.commons.utils.CalculateUtils;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.page.Pagination;
import com.aebiz.baseframework.view.annotation.SJson;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Auther: zenghaorong
 * @Date: 2019/3/23  21:39
 * @Description:
 */
@Controller
@RequestMapping("/open/h5/product")
public class ProductController {

    private static final Log log = Logs.get();

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsImageService goodsImageService;

    @Autowired
    private GoodsProductService goodsProductService;

    /**
     * 进入商品列表页  或 进入宝妈专区
     */
    @RequestMapping("/list.html")
    public String index() {
        return "pages/front/h5/niantu/productList";
    }

    /**
     * 进入商品详情页
     */
    @RequestMapping("/productDetail.html")
    public String productDetail(HttpServletRequest request) {
        String id = request.getParameter("id");
        request.setAttribute("id",id);
        return "pages/front/h5/niantu/productDetails";
    }




    /**
     * 获得商品列表
     * @param pageNumber 页码
     * @return
     */
    @RequestMapping("ProductList.html")
    @SJson
    public Result getProductList(Integer pageNumber,HttpServletRequest request){
        try {
            if(pageNumber == null){
                pageNumber = 0;
            }
            String key = request.getParameter("key");
            String priceArea = request.getParameter("priceArea");
            String isRecommend = request.getParameter("isRecommend");
            String sortType = request.getParameter("sortType");
            Cnd cnd = Cnd.NEW();
            if(StringUtils.isNotEmpty(isRecommend)){
                cnd.and("recommend","=",Integer.parseInt(isRecommend));
            }
            if(StringUtils.isNotEmpty(key)){
                cnd.and("name","like","%"+key+"%");
            }

            cnd.and("delFlag", "=", 0 );
            cnd.and("sale", "=", 1);
            cnd.and("status", "=", 3);
            Pagination res = goodsService.listPage(pageNumber, 2000, cnd);
            List<?> resList = res.getList();
            List<Goods_main> productList = new ArrayList<>();
            if(resList!=null&&resList.size()>0){
                for (int i=0;i<resList.size();i++){
                    Goods_main o = (Goods_main) resList.get(i);
                    Cnd imgCnd = Cnd.NEW();
                    imgCnd.and("goodsId","=",o.getId());
                    List<Goods_image> imgList = goodsImageService.query(imgCnd);
                    if(imgList!=null&&imgList.size()>0){
                        o.setImgList(imgList.get(0).getImgAlbum());
                    }
                    Cnd proCnd = Cnd.NEW();
                    proCnd.and("goodsId","=",o.getId());
                    List<Goods_product> gpList = goodsProductService.query(proCnd);
                    if(gpList!=null&&gpList.size()>0){
                        Integer salePrice = gpList.get(0).getSalePrice();
                        double price = salePrice.doubleValue()/100;
                        if(StringUtils.isNotEmpty(priceArea)){
                            String[] prices = priceArea.split("_");
                            if(!(Double.parseDouble(prices[0])<price&&Double.parseDouble(prices[1])>price)){
                                continue;
                            }
                        }
                        o.setPrice(price+"");
                        o.setSaleNumMonth(gpList.get(0).getSaleNumMonth()+"");
                    }

                    productList.add(o);
                }
            }
            if(StringUtils.isNotEmpty(sortType)){
                Collections.sort(productList, new Comparator<Goods_main>() {
                    @Override
                    public int compare(Goods_main o1, Goods_main o2) {
                        if("priceDesc".equals(sortType)){
                            if (Double.parseDouble(o1.getPrice()) > Double.parseDouble(o2.getPrice())) {
                                return -1;
                            }
                            if (Double.parseDouble(o1.getPrice()) == Double.parseDouble(o2.getPrice())) {
                                return 0;
                            }
                            return 1;
                        }
                        if("priceAsc".equals(sortType)){
                            if (Double.parseDouble(o1.getPrice()) > Double.parseDouble(o2.getPrice())) {
                                return 1;
                            }
                            if (Double.parseDouble(o1.getPrice()) == Double.parseDouble(o2.getPrice())) {
                                return 0;
                            }
                            return -1;
                        }
                        if("numDesc".equals(sortType)){
                            if (Integer.parseInt(o1.getSaleNumMonth()) > Integer.parseInt(o2.getSaleNumMonth())) {
                                return -1;
                            }
                            if (Integer.parseInt(o1.getSaleNumMonth()) == Integer.parseInt(o2.getSaleNumMonth())) {
                                return 0;
                            }
                            return 1;
                        }
                        if("numAsc".equals(sortType)){
                            if (Integer.parseInt(o1.getSaleNumMonth()) > Integer.parseInt(o2.getSaleNumMonth())) {
                                return 1;
                            }
                            if (Integer.parseInt(o1.getSaleNumMonth()) == Integer.parseInt(o2.getSaleNumMonth())) {
                                return 0;
                            }
                            return -1;
                        }
                        return 0;
                    }
                });
            }

            return Result.success("ok",productList);
        } catch (Exception e) {
            log.error("获取商品列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 获得商品详情
     * @param id
     * @return
     */
    @RequestMapping("getProductDetail.html")
    @SJson
    public Result getProductDetail(String id){
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("delFlag", "=", 0 );
            cnd.and("id","=",id);
            Goods_main o = goodsService.fetch(id);
                    Cnd imgCnd = Cnd.NEW();
                    imgCnd.and("goodsId","=",o.getId());
                    List<Goods_image> imgList = goodsImageService.query(imgCnd);
                    o.setImageList(imgList);
                    if(imgList!=null&&imgList.size()>0){
                        o.setImgList(imgList.get(0).getImgAlbum());
                    }
                    Cnd proCnd = Cnd.NEW();
                    proCnd.and("goodsId","=",o.getId());
                    List<Goods_product> gpList = goodsProductService.query(proCnd);
                    if(gpList!=null&&gpList.size()>0){
                        Integer salePrice = gpList.get(0).getSalePrice();
                        Integer marketPrice = gpList.get(0).getMarketPrice();
                        double price = salePrice.doubleValue()/100;
                        double marketPrice2 = marketPrice.doubleValue()/100;
                        o.setPrice(price+"");
                        o.setMarketPrice(marketPrice2+"");
                        o.setSaleNumMonth(gpList.get(0).getSaleNumMonth()+"");
                    }
            return Result.success("ok",o);
        } catch (Exception e) {
            log.error("获取商品列表异常",e);
            return Result.error("fail");
        }
    }

    /**
     * 订单确认页获得商品列表
     * @param productList
     * @return
     */
    @RequestMapping("getOrderProductList.html")
    @SJson
    public Result getOrderProductList(String productList){
        try {
            List<Map<String,Object>> list = (List<Map<String, Object>>) JSON.parse(productList);
            List<Goods_main> goods_mainList = new ArrayList<>();
            for(Map<String,Object> map:list) {
                String id = (String) map.get("productId");
                Cnd cnd = Cnd.NEW();
                cnd.and("delFlag", "=", 0);
                cnd.and("id", "=", id);

                Goods_main o = goodsService.fetch(id);
                Cnd imgCnd = Cnd.NEW();
                imgCnd.and("goodsId", "=", o.getId());
                List<Goods_image> imgList = goodsImageService.query(imgCnd);
                o.setImageList(imgList);
                if (imgList != null && imgList.size() > 0) {
                    o.setImgList(imgList.get(0).getImgAlbum());
                }
                Cnd proCnd = Cnd.NEW();
                proCnd.and("goodsId", "=", o.getId());
                List<Goods_product> gpList = goodsProductService.query(proCnd);
                if (gpList != null && gpList.size() > 0) {
                    double salePrice = gpList.get(0).getSalePrice();
                    int marketPrice = gpList.get(0).getMarketPrice();
                    double price = CalculateUtils.div(salePrice,100,2);
                    o.setPrice(price + "");
                    o.setMarketPrice(marketPrice / 100 + "");
                    o.setSaleNumMonth(gpList.get(0).getSaleNumMonth() + "");
                }
                String num = (String) map.get("num");
                o.setNum(num);
                goods_mainList.add(o);
            }
            return Result.success("ok",goods_mainList);
        } catch (Exception e) {
            log.error("获取商品列表异常",e);
            return Result.error("fail");
        }
    }





}
