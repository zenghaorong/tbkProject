package com.aebiz.app.tbk.modules.services.impl;

import com.aebiz.app.tbk.modules.models.TbkApiBaseModel;
import com.aebiz.app.tbk.modules.models.TbkConfig;
import com.aebiz.app.tbk.modules.services.TbkApiService;
import com.aebiz.app.web.commons.utils.TcpipUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.request.TbkDgOptimusMaterialRequest;
import com.taobao.api.request.TbkItemGetRequest;
import com.taobao.api.response.TbkDgMaterialOptionalResponse;
import com.taobao.api.response.TbkDgOptimusMaterialResponse;
import com.taobao.api.response.TbkItemGetResponse;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.stereotype.Service;


/**
 * 淘宝客API调用接口
 */
@Service
public class TbkApiServiceImpl implements TbkApiService {

    private static final Log log = Logs.get();

    /**
     *   taobao.tbk.dg.material.optional( 淘宝客-推广者-物料搜索  )
     */
    @Override
    public JSONObject tbkGetProductList(TbkDgMaterialOptionalRequest req) {

        TaobaoClient client = new DefaultTaobaoClient(TbkConfig.serverUrl, TbkConfig.appKey, TbkConfig.appSecret);
        TbkDgMaterialOptionalResponse rsp = null;
        try {
            rsp = client.execute(req, TbkConfig.sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        String str = rsp.getBody();
        JSONObject jsonObject = JSON.parseObject(str);
        if (rsp.isSuccess()) {
            return jsonObject;
        }
        return jsonObject;
    }

    /**
     *  taobao.tbk.dg.optimus.material( 淘宝客-推广者-物料精选 )
     *  代替了 猜你喜欢等api
     */
    @Override
    public JSONObject tbkGetLikeProductList(TbkDgOptimusMaterialRequest req) {
        TaobaoClient client = new DefaultTaobaoClient(TbkConfig.serverUrl, TbkConfig.appKey, TbkConfig.appSecret);

        TbkDgOptimusMaterialResponse rsp = null;
        try {
            rsp = client.execute(req, TbkConfig.sessionKey);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        String str = rsp.getBody();
        log.info(str);
        JSONObject jsonObject = JSON.parseObject(str);
        if (rsp.isSuccess()) {
            return jsonObject;
        }
        return jsonObject;
    }

    /**
     * 测试main
     * @param args
     */
    public static void main(String[] args) {
        TbkApiServiceImpl tbkApiService = new TbkApiServiceImpl();
        TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();

        //搜索
//        req.setQ("女装");
//        req.setCat("16,18");
//        JSONObject o = tbkApiService.tbkGetProductList(req);

        //猜你喜欢
//        req.setMethod("taobao.tbk.item.guess.like");
//        req.setAdzoneId("109108750020");
//        req.setOs("ios");
//        req.setIp("172.0.0.1");
//        req.setUa("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");
//        req.setNet("wifi");
//        JSONObject o = tbkApiService.tbkGetLikeProductList(req);

        //物料搜索
        req.setAdzoneId(109108750020L);
        req.setQ("女士性感情趣内衣");
        req.setCat("16,18");//类目
        JSONObject o = tbkApiService.tbkGetProductList(req);
        System.out.println(JSON.toJSONString(o));
    }
}
