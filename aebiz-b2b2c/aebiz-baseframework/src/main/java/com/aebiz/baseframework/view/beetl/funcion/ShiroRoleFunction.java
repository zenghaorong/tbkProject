package com.aebiz.baseframework.view.beetl.funcion;

import org.apache.shiro.SecurityUtils;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 判断是否包含角色
 * Created by wizzer on 2016/12/20.
 */
public class ShiroRoleFunction implements Function {
    @Override
    public Object call(Object[] role, Context context) {
        if (role == null || role.length < 1) {
            return false;
        }
        return SecurityUtils.getSubject() != null && SecurityUtils.getSubject().hasRole(role[0].toString());
    }

}
