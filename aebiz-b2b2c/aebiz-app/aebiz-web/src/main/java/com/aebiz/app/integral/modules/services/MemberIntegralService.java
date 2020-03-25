package com.aebiz.app.integral.modules.services;

import com.aebiz.baseframework.base.service.BaseService;
import com.aebiz.app.integral.modules.models.Member_Integral;

public interface MemberIntegralService extends BaseService<Member_Integral>{
    /**
     * 购物商城使用接口-添加积分
     * @param userId
     * @param integralRuleType
     * @param times
     * @param payMoney
     */
    public void addMemberIntegral(String userId,String integralRuleType,String times,Double payMoney);

    /**
     * 领取优惠券引流活动获取积分
     * @param storeId
     * @param ruleCode
     * @param accountId
     */
    public void saveMemberIntegral(String storeId,String ruleCode,int integralType,String accountId);

    /**
     * 扣除会员积分接口
     */
    public void minusPoints(String storeId,int im,String accountId,String desc);

}
