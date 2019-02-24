package com.aebiz.baseframework.shiro.filter;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;

/**
 * Created by wizzer on 2017/1/12.
 */
public class AebizShiroFilterFactoryBean extends ShiroFilterFactoryBean {
    private static final Logger log = LoggerFactory.getLogger(AebizShiroFilter.class);

    @Override
    protected AbstractShiroFilter createInstance() throws Exception {
        log.debug("Creating Shiro Filter instance.");
        SecurityManager securityManager = this.getSecurityManager();
        String manager1;
        if (securityManager == null) {
            manager1 = "SecurityManager property must be set.";
            throw new BeanInitializationException(manager1);
        } else if (!(securityManager instanceof WebSecurityManager)) {
            manager1 = "The security manager does not implement the WebSecurityManager interface.";
            throw new BeanInitializationException(manager1);
        } else {
            FilterChainManager manager = this.createFilterChainManager();
            PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
            chainResolver.setFilterChainManager(manager);
            return new AebizShiroFilter((WebSecurityManager) securityManager, chainResolver);
        }
    }
}
