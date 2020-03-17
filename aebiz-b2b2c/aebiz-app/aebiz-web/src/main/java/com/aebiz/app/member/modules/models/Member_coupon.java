package com.aebiz.app.member.modules.models;

import com.aebiz.app.sales.modules.models.Sales_coupon;
import com.aebiz.app.sales.modules.models.Sales_rule_order;
import com.aebiz.baseframework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by wizzer on 2017/6/7.
 */
@Table("member_coupon")
@TableIndexes({@Index(name = "INDEX_MEMBER_COUPON", fields = {"code"}, unique = true)})
public class Member_coupon extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("商城ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String storeId;

    @Column
    @Comment("帐号ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String accountId;

    @Column
    @Comment("优惠券ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String couponId;

    @One(target = Sales_coupon.class, field = "couponId")
    private Sales_coupon sales_coupon;

    @Column
    @Comment("编码")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String code;

    @Column
    @Comment("手机号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String mobile;

    @Column
    @Comment("订单赠送订单ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String source_orderId;

    @Column
    @Comment("推荐人获取的 推荐人id")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String sourceAccountId;

    @Column
    @Comment("来源")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String source;//来源:1自己领取 2分享获得

    @Column
    @Comment("核销状态")
    @ColDefine(type = ColType.INT)
    @Default("0")
    private Integer status;//核销状态 0待核销 1已核销

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(String sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public String getId() {
        return id;
    }

    public Sales_coupon getSales_coupon() {
        return sales_coupon;
    }

    public void setSales_coupon(Sales_coupon sales_coupon) {
        this.sales_coupon = sales_coupon;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSource_orderId() {
        return source_orderId;
    }

    public void setSource_orderId(String source_orderId) {
        this.source_orderId = source_orderId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
