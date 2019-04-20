package com.aebiz.app.integral.modules.models;

import com.aebiz.baseframework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

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
