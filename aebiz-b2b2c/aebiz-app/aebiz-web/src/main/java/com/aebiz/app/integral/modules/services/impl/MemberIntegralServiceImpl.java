package com.aebiz.app.integral.modules.services.impl;

import com.aebiz.baseframework.base.service.BaseServiceImpl;
import com.aebiz.app.integral.modules.models.Member_Integral;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import org.nutz.dao.Dao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MemberIntegralServiceImpl extends BaseServiceImpl<Member_Integral> implements MemberIntegralService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }
}
