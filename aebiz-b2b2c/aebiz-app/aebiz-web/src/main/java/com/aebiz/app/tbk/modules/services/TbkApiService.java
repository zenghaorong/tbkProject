package com.aebiz.app.tbk.modules.services;

import com.aebiz.app.tbk.modules.models.TbkApiBaseModel;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.request.TbkDgOptimusMaterialRequest;
import com.taobao.api.request.TbkItemGetRequest;

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



}
