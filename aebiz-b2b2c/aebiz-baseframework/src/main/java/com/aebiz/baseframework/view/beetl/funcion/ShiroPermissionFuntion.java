package com.aebiz.baseframework.view.beetl.funcion;

import org.apache.shiro.SecurityUtils;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 判断是否有权限
 * Created by wizzer on 2016/12/20.
 */
public class ShiroPermissionFuntion implements Function {
    @Override
    public Object call(Object[] permission, Context context) {
        if (permission == null || permission.length < 1) {
            return false;
        }
        return SecurityUtils.getSubject() != null && SecurityUtils.getSubject().isPermitted(permission[0].toString());
    }

}
