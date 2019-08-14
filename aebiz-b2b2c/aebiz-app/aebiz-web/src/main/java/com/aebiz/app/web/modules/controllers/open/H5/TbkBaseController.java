package com.aebiz.app.web.modules.controllers.open.H5;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @Auther: zenghaorong
 * @Date: 2019/8/11  17:09
 * @Description: 工具控制层
 */
public class TbkBaseController {


    public Map<String,Long> getTbkPage(HttpServletRequest request){
        String pageNoStr = (String) request.getParameter("pageNo"); //获取当前页码
        Long pageNo = 1L;
        if(StringUtils.isNotEmpty(pageNoStr)){
            pageNo = Long.valueOf(pageNoStr);
        }
        Long pageSize = 20L; //一页多少条
        Map<String,Long> map = new HashedMap();
        map.put("pageNo",pageNo);
        map.put("pageSize",pageSize);
        return map;
    }

    public static Date getNextDate(Date d) {
        long addTime = 1;//用1为乘的基数
        addTime *= 1;
        addTime *= 24; //1天24小时
        addTime *= 60; //一小时60分钟
        addTime *= 60; //一分钟60秒
        addTime *= 1000; //一秒1000毫秒

        Date date = new Date(d.getTime() + addTime);

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

//        return sdf.format(date);
        return date;
    }


}
