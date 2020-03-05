package com.aebiz.app.web.modules.controllers.store.tlj;

import com.aebiz.app.tbk.modules.models.TbkConfig;
import com.aebiz.app.tbk.modules.services.TbkApiService;
import com.aebiz.baseframework.base.Result;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.request.TbkDgVegasTljCreateRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/store/tlj/cms")
public class StoreTljController {

    @Autowired
    private TbkApiService tbkApiService;

    @RequestMapping("/add")
    public String index() {
        return "pages/store/tlj/add";
    }

    @RequestMapping("/addDa")
    @ResponseBody
    public Object addDa(String campaign_type,Long item_id,
                        Long total_num,String name,Long user_total_win_num_limit,
                        String security_switch,String per_face,String send_start_time,
                        String send_end_time,String use_end_time,String use_end_time_mode,
                        String use_start_time) {
        TbkDgVegasTljCreateRequest req = new TbkDgVegasTljCreateRequest();
        try {
            req.setCampaignType(campaign_type);
            req.setItemId(item_id);
            req.setTotalNum(total_num);
            req.setName(name);
            req.setUserTotalWinNumLimit(user_total_win_num_limit);
            if(StringUtils.isNotEmpty(security_switch)){
                req.setSecuritySwitch(true);
            }else {
                req.setSecuritySwitch(false);
            }
            req.setPerFace(per_face);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            req.setSendStartTime(simpleDateFormat.parse(send_start_time));
            req.setSendEndTime(simpleDateFormat.parse(send_end_time));
            req.setAdzoneId(TbkConfig.adzone_Id);
            JSONObject jsonObject = tbkApiService.tbkCreateTljVegas(req);

            return Result.success("ok",jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("fail");
        }
    }

}
