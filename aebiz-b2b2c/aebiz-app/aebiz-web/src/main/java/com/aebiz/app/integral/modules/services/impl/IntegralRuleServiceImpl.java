package com.aebiz.app.integral.modules.services.impl;

import com.aebiz.baseframework.base.service.BaseServiceImpl;
import com.aebiz.app.integral.modules.models.Integral_Rule;
import com.aebiz.app.integral.modules.services.IntegralRuleService;
import org.nutz.dao.Dao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IntegralRuleServiceImpl extends BaseServiceImpl<Integral_Rule> implements IntegralRuleService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }
}
