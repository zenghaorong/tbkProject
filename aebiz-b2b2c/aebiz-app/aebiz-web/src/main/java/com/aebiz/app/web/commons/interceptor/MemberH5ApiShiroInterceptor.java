package com.aebiz.app.web.commons.interceptor;

import com.aebiz.app.member.modules.models.Member_user;
import com.aebiz.baseframework.base.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: zenghaorong
 * @Date: 2019/11/5  14:49
 * @Description:
 */
public class MemberH5ApiShiroInterceptor extends HandlerInterceptorAdapter {

    private static final Log log = Logs.get();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler)
            throws Exception {
        Member_user memberUser = null;
        try {
            String sessionId = Strings.sNull(request.getHeader("sessionId"));
            SessionKey key = new WebSessionKey(sessionId, request, response);
            Session se = SecurityUtils.getSecurityManager().getSession(key);
            Object obj = se.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            SimplePrincipalCollection coll = (SimplePrincipalCollection) obj;
            memberUser = (Member_user) coll.getPrimaryPrincipal();
        }catch (Exception e){
            log.info("当前会话失效");
        }
        if (memberUser == null) {
            response.reset();
            response.setCharacterEncoding(Encoding.UTF8);
            response.setContentType("application/json");
            response.getWriter().write(Result.error(9999, "H5端当前会话失效").toString());
            response.getWriter().flush();
            response.getWriter().close();
            return false;
        }
        return true;
    }


}
