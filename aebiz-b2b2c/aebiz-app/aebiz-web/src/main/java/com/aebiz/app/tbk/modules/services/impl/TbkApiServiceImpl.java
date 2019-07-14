package com.aebiz.app.tbk.modules.services.impl;

import com.aebiz.app.tbk.modules.models.TbkConfig;
import com.aebiz.app.tbk.modules.services.TbkApiService;
import com.alibaba.fastjson.JSON;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkItemGetRequest;
import com.taobao.api.response.TbkItemGetResponse;


/**
 * 淘宝客API调用接口
 */
public class TbkApiServiceImpl implements TbkApiService {


    /**
     *  taobao.tbk.item.get( 淘宝客商品查询 )
     */
    @Override
    public String tbkGetProductList() {

        TaobaoClient client = new DefaultTaobaoClient(TbkConfig.serverUrl, TbkConfig.appKey, TbkConfig.appSecret);
        TbkItemGetRequest req = new TbkItemGetRequest();
        req.setFields("num_iid,title,nick,price,num");
        req.setTopHttpMethod("taobao.tbk.item.get");
        req.setQ("女装");
        req.setCat("16,18");
        TbkItemGetResponse rsp = null;
        try {
            rsp = client.execute(req, TbkConfig.sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(JSON.toJSONString(rsp.getBody()));
        if (rsp.isSuccess()) {
            System.out.println(rsp.getResults());
        }
        return null;
    }


    public static void main(String[] args) {
        TbkApiServiceImpl tbkApiService = new TbkApiServiceImpl();
        tbkApiService.tbkGetProductList();
    }
}
