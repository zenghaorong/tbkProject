package com.aebiz.app.integral.modules.services;

import com.aebiz.baseframework.base.service.BaseService;
import com.aebiz.app.integral.modules.models.Member_Integral;

public interface MemberIntegralService extends BaseService<Member_Integral>{
    public void addMemberIntegral(String userId,String integralRuleType,String times,Double payMoney);
}
