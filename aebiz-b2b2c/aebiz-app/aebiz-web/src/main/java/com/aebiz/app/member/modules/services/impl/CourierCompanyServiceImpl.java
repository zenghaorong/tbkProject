package com.aebiz.app.member.modules.services.impl;

import com.aebiz.baseframework.base.service.BaseServiceImpl;
import com.aebiz.app.member.modules.models.Courier_company;
import com.aebiz.app.member.modules.services.CourierCompanyService;
import org.nutz.dao.Dao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CourierCompanyServiceImpl extends BaseServiceImpl<Courier_company> implements CourierCompanyService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }
}
