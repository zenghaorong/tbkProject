package com.aebiz.app.tbk.modules.services.impl;

import com.aebiz.baseframework.base.service.BaseServiceImpl;
import com.aebiz.app.tbk.modules.models.Tbk_cat;
import com.aebiz.app.tbk.modules.services.TbkCatService;
import org.nutz.dao.Dao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TbkCatServiceImpl extends BaseServiceImpl<Tbk_cat> implements TbkCatService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }
}
