package com.aebiz.app.web.commons.interceptor;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.sys.modules.services.SysApiService;
import com.aebiz.app.sys.modules.services.impl.SysApiServiceImpl;
import com.aebiz.baseframework.base.Result;
import com.aebiz.commons.utils.SpringUtil;
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
 * Created by wizzer on 2017/4/4.
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {
    private static final Log log = Logs.get();

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler)
            throws Exception {
        String sessionId = Strings.sNull(request.getParameter("sessionId"));
        SessionKey key = new WebSessionKey(sessionId,request,response);
        Session se = SecurityUtils.getSecurityManager().getSession(key);
        Object obj = se.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        SimplePrincipalCollection coll = (SimplePrincipalCollection) obj;
        Account_user account_user = (Account_user)coll.getPrimaryPrincipal();
        if (account_user == null) {
            response.reset();
            response.setCharacterEncoding(Encoding.UTF8);
            response.setContentType("application/json");
            response.getWriter().write(Result.error("token invalid").toString());
            response.getWriter().flush();
            response.getWriter().close();
            return false;
        }
        return true;
    }
}
