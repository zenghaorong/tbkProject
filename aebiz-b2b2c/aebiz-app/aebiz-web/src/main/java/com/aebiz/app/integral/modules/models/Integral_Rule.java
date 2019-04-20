package com.aebiz.app.integral.modules.models;

import com.aebiz.baseframework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * 积分规则表
 * @author chengzhuming
 * @date 2019/4/16 9:13
 */
@Table("Integral_rule")
public class Integral_Rule extends BaseModel implements Serializable {
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    /**
     * 积分操作类型 --增加
     */
    public static final String OPE_TYPE_ADD = "1";

    /**
     * 积分操作类型--减少
     */
    public static final String OPE_TYPE_RED = "2";

    /**
     * 积分操作类型--无变化
     */
    public static final String OPE_TYPE_NONE = "3";

    @Column
    @Comment("规则名称")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    private String ruleName;

    @Column
    @Comment("规则编码")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String ruleCode;

    @Column
    @Comment("积分操作类型：1：一次性,2：一天，3：每天")
    @ColDefine(type = ColType.VARCHAR, width = 8)
    private String integralType;

    @Column
    @Comment("消费积分操作数量")
    @ColDefine(type = ColType.INT, width = 64)
    private int integralCount;


    @Column
    @Comment("行为积分操作类型：1：增加,2：减少")
    @ColDefine(type = ColType.VARCHAR, width = 8)
    private String pointsType;


    @Column
    @Comment("行为积分操作数量")
    @ColDefine(type = ColType.INT, width = 64)
    private int pointsCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getIntegralType() {
        return integralType;
    }

    public void setIntegralType(String integralType) {
        this.integralType = integralType;
    }

    public int getIntegralCount() {
        return integralCount;
    }

    public void setIntegralCount(int integralCount) {
        this.integralCount = integralCount;
    }

    public String getPointsType() {
        return pointsType;
    }

    public void setPointsType(String pointsType) {
        this.pointsType = pointsType;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(int pointsCount) {
        this.pointsCount = pointsCount;
    }
}
