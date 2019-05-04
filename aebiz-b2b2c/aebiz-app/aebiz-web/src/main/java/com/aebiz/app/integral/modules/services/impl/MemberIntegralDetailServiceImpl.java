package com.aebiz.app.integral.modules.services.impl;

import com.aebiz.app.integral.modules.models.Member_Integral;
import com.aebiz.app.integral.modules.models.Member_Integral_Detail;
import com.aebiz.app.integral.modules.services.MemberIntegralDetailService;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.baseframework.base.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MemberIntegralDetailServiceImpl extends BaseServiceImpl<Member_Integral_Detail> implements MemberIntegralDetailService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }

}
