package com.aebiz.app.web.modules.controllers.open.H5;

import com.aebiz.app.store.modules.models.Store_goodsclass;
import com.aebiz.app.store.modules.services.StoreGoodsclassService;
import com.aebiz.app.tbk.modules.models.TbkConfig;
import com.aebiz.baseframework.base.Result;
import com.aebiz.baseframework.view.annotation.SJson;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.request.TbkDgOptimusMaterialRequest;
import org.nutz.dao.Cnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: zenghaorong
 * @Date: 2019/10/1  17:10
 * @Description:
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping("/open/h5/tbkMain")
public class TbkMainController {

    @Autowired
    private StoreGoodsclassService storeGoodsclassService;

    /***
     * 查询分类
     */
    @RequestMapping("/getProductClass")
    @SJson
    public Object getProductClass() {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("parentId","=","");
            cnd.asc("location").asc("path");
            //获取父级分类
            List<Store_goodsclass> storeGoodsClassList = storeGoodsclassService.query(cnd);
//            List<Map<String,Object>> returnList = new ArrayList<>();
//            for (Store_goodsclass s : storeGoodsClassList) {
//                Map<String,Object> map = new HashMap<>();
//                List<Store_goodsclass> list = storeGoodsclassService.query(Cnd.where("parentId", "=", s.getId()).asc("location").asc("path"));
//                map.put("parent",s);
//                map.put("cList",list);
//                returnList.add(map);
//            }
            return Result.success("ok",storeGoodsClassList);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }


    /***
     * 查询子分类
     */
    @RequestMapping("/getProductChildClass")
    @SJson
    public Object getProductChildClass(String pId) {
        try {
           List<Store_goodsclass> list = storeGoodsclassService.query(Cnd.where("parentId", "=", pId).asc("location").asc("path"));
            return Result.success("ok",list);
        } catch (Exception e) {
            return Result.error("fail");
        }
    }

}
