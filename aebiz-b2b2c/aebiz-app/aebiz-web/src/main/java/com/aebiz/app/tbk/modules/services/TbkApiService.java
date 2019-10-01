package com.aebiz.app.tbk.modules.services;

import com.alibaba.fastjson.JSONObject;
import com.taobao.api.request.*;

/**
 * 淘宝客API调用接口
 */

public interface TbkApiService  {


    /**
     *  taobao.tbk.dg.material.optional( 淘宝客-推广者-物料搜索  )
     */
    JSONObject tbkGetProductList(TbkDgMaterialOptionalRequest req);


    /**
     *  taobao.tbk.dg.optimus.material( 淘宝客-推广者-物料精选 )
     *  代替了 猜你喜欢等api
     */
    JSONObject tbkGetLikeProductList(TbkDgOptimusMaterialRequest req);

    /**
     *  taobao.tbk.ju.tqg.get( 淘抢购api )
     */
    JSONObject tbkGetTqgProductList(TbkJuTqgGetRequest req);

    /**
     *  taobao.ju.items.search( 聚划算商品搜索接口 )
     */
    JSONObject tbkGetJhsProductList(JuItemsSearchRequest req);

    /**
     * taobao.tbk.content.get( 淘宝客-推广者-图文内容输出 )
     */
    JSONObject tbkGetContentList(TbkContentGetRequest req);

    /**
     * taobao.tbk.uatm.favorites.get( 淘宝客-推广者-选品库宝贝列表 )
     */
    JSONObject tbkGetFavoritesList(TbkUatmFavoritesGetRequest req);

    /**
     * taobao.tbk.uatm.favorites.item.get( 淘宝客-推广者-选品库宝贝信息 )
     */
    JSONObject tbkGetFavoritesProductList(TbkUatmFavoritesItemGetRequest req);


}
