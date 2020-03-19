package com.aebiz.app.store.modules.services.impl;

import com.aebiz.baseframework.base.service.BaseServiceImpl;
import com.aebiz.app.store.modules.models.Store_activity;
import com.aebiz.app.store.modules.services.StoreActivityService;
import org.nutz.dao.Dao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StoreActivityServiceImpl extends BaseServiceImpl<Store_activity> implements StoreActivityService {
    @Resource(name = "nutDao", type = Dao.class)
    public void init(Dao dao) {
        super.setDao(dao);
    }
}
