package com.aebiz.app.store.modules.models;

import com.aebiz.app.sales.modules.models.Activity_coupon;
import com.aebiz.baseframework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author THINK
 */
@Table("store_activity")
public class Store_activity extends BaseModel implements Serializable {

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
    @Comment("活动名称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String name;

    @Column
    @Comment("活动开始时间")
    @ColDefine(type = ColType.INT)
    private Integer startTime;

    @Column
    @Comment("活动结束时间")
    @ColDefine(type = ColType.INT)
    private Integer endTime;

    @Column
    @Comment("推荐人获取券的券编号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String recommendCouponId;

    @Column
    @Comment("是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @Column
    @Comment("列表封面图")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String listImg;

    @Column
    @Comment("列表封面图")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String content;

    @Column
    @Comment("详情")
    @ColDefine(type = ColType.TEXT)
    private String details;

    @Column
    @Comment("排序")
    @ColDefine(type = ColType.INT)
    private Integer index;

    @Column
    @Comment("已报名用户数")
    @ColDefine(type = ColType.INT)
    private Integer ybmyhs;

    @Column
    @Comment("已分享人数")
    @ColDefine(type = ColType.INT)
    private Integer yfxrs;

    @Column
    @Comment("已浏览人数")
    @ColDefine(type = ColType.INT)
    private Integer yllrs;

    @Column
    @Comment("参与人数")
    @ColDefine(type = ColType.INT)
    private Integer cyrs;

    @Column
    @Comment("剩余礼品数")
    @ColDefine(type = ColType.INT)
    private Integer sylps;

    private String activityCouponList;

    public Integer getYbmyhs() {
        return ybmyhs;
    }

    public void setYbmyhs(Integer ybmyhs) {
        this.ybmyhs = ybmyhs;
    }

    public Integer getYfxrs() {
        return yfxrs;
    }

    public void setYfxrs(Integer yfxrs) {
        this.yfxrs = yfxrs;
    }

    public Integer getYllrs() {
        return yllrs;
    }

    public void setYllrs(Integer yllrs) {
        this.yllrs = yllrs;
    }

    public Integer getCyrs() {
        return cyrs;
    }

    public void setCyrs(Integer cyrs) {
        this.cyrs = cyrs;
    }

    public Integer getSylps() {
        return sylps;
    }

    public void setSylps(Integer sylps) {
        this.sylps = sylps;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getActivityCouponList() {
        return activityCouponList;
    }

    public void setActivityCouponList(String activityCouponList) {
        this.activityCouponList = activityCouponList;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getId() {
        return id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public String getRecommendCouponId() {
        return recommendCouponId;
    }

    public void setRecommendCouponId(String recommendCouponId) {
        this.recommendCouponId = recommendCouponId;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getListImg() {
        return listImg;
    }

    public void setListImg(String listImg) {
        this.listImg = listImg;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
