package com.aebiz.app.integral.modules.models;

import com.aebiz.app.acc.modules.models.Account_user;
import com.aebiz.app.acc.modules.services.AccountUserService;
import com.aebiz.app.integral.modules.services.MemberIntegralService;
import com.aebiz.baseframework.base.model.BaseModel;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.entity.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * 用户积分表
 * @author chengzhuming
 * @date 2019/4/16 9:13
 */
@Table("Member_Integral")
public class Member_Integral extends BaseModel implements Serializable {
    

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("会员UUID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String customerUuid;

    @Column
    @Comment("商户UUID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String storeUuid;


    @Column
    @Comment("可用积分")
    @ColDefine(type = ColType.INT, width = 11)
    private int useAbleIntegral;


    @Column
    @Comment("累计积分")
    @ColDefine(type = ColType.INT, width = 11)
    private int totalIntegral;

    @One(target = Account_user.class, field = "customerUuid")
    private Account_user account_user;

    private String customerName;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Account_user getAccount_user() {
        return account_user;
    }

    public void setAccount_user(Account_user account_user) {
        this.account_user = account_user;
    }

    private List<Member_Integral_Detail> details;

    public List<Member_Integral_Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Member_Integral_Detail> details) {
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerUuid() {
        return customerUuid;
    }

    public void setCustomerUuid(String customerUuid) {
        this.customerUuid = customerUuid;
    }

    public String getStoreUuid() {
        return storeUuid;
    }

    public void setStoreUuid(String storeUuid) {
        this.storeUuid = storeUuid;
    }

    public int getUseAbleIntegral() {
        return useAbleIntegral;
    }

    public void setUseAbleIntegral(int useAbleIntegral) {
        this.useAbleIntegral = useAbleIntegral;
    }

    public int getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(int totalIntegral) {
        this.totalIntegral = totalIntegral;
    }
}
