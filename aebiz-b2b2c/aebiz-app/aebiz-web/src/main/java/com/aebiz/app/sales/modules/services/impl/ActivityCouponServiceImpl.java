package com.aebiz.app.sales.modules.services.impl;

import com.aebiz.baseframework.base.service.BaseServiceImpl;
import com.aebiz.app.sales.modules.models.Activity_coupon;
import com.aebiz.app.sales.modules.services.ActivityCouponService;
import org.nutz.dao.Dao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ActivityCouponServiceImpl extends BaseServiceImpl<Activity_coupon> implements ActivityCouponService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }
}
