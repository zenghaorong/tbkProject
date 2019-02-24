package com.aebiz.baseframework.view.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by wizzer on 2017/2/13.
 */
public class SJsonReturnHandler implements HandlerMethodReturnValueHandler {
    private static String JSONP_CT = "application/javascript";
    private static final Log log= Logs.get();
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(SJson.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        SJson json = returnType.getMethodAnnotation(SJson.class);
        if (json != null && json.jsonp()) {
            response.setContentType(JSONP_CT);
        } else {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        }
        String str;
        if (json != null) {
            if (json.value().startsWith("{")) {
                //如果是{ 开头则转成格式化对象
                str = Json.toJson(returnValue, Json.fromJson(JsonFormat.class,
                        json.value()));
            } else if ("nice".equalsIgnoreCase(json.value())) {
                str = Json.toJson(returnValue, JsonFormat.nice());
            } else if ("forlook".equalsIgnoreCase(json.value())) {
                str = Json.toJson(returnValue, JsonFormat.forLook());
            } else if ("full".equalsIgnoreCase(json.value())) {
                str = Json.toJson(returnValue, JsonFormat.full());
            } else if ("compact".equalsIgnoreCase(json.value())) {
                str = Json.toJson(returnValue, JsonFormat.compact());
            } else if ("tidy".equalsIgnoreCase(json.value())) {
                str = Json.toJson(returnValue, JsonFormat.tidy());
            } else {
                str = Json.toJson(returnValue);
            }
        } else {
            str = Json.toJson(returnValue);
        }
        try (PrintWriter out = response.getWriter()) {
            if (json != null && json.jsonp())
                out.write(request.getParameter(Strings.isEmpty(json.jsonpParam()) ? "callback" : json.jsonpParam()) + "(");
            out.write(str);
            if (json != null && json.jsonp())
                out.write(");");
            out.flush();
        } catch (IOException e) {
            throw e;
        }
    }

}
