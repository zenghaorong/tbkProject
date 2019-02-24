package com.aebiz.baseframework.view.beetl.funcion;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.nutz.json.Json;

/**
 * 对象转JSON字符串
 * Created by wizzer on 2016/12/20.
 */
public class JsonFunction implements Function {
    @Override
    public Object call(Object[] paras, Context arg1) {
        if (paras == null) {
            return "";
        }
        return Json.toJson(paras[0]);
    }
}
