package com.aebiz.baseframework.view.json;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by wizzer on 2016/12/23.
 */
public class JsonView extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.getWriter().write(Json.toJson(map, JsonFormat.compact()));
    }
}