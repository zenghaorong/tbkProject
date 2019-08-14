package com.aebiz.app.tbk.modules.services;

import com.alibaba.fastjson.JSONObject;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.request.TbkDgOptimusMaterialRequest;
import com.taobao.api.request.TbkItemGetRequest;
import com.taobao.api.request.TbkJuTqgGetRequest;

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


}
