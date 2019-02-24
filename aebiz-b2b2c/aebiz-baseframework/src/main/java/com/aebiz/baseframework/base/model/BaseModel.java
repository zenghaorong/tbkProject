package com.aebiz.baseframework.base.model;

import com.aebiz.commons.utils.StringUtil;
import org.nutz.dao.entity.annotation.*;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.random.R;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by wizzer on 2016/6/21.
 */
public abstract class BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column
    @Comment("操作人")
    @Prev(els = @EL("$me.uid()"))
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String opBy;

    @Column
    @Comment("操作时间")
    @Prev(els = @EL("$me.now()"))
    @ColDefine(type = ColType.INT)
    private Integer opAt;

    @Column
    @Comment("是否删除")
    @Prev(els = @EL("$me.flag()"))
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean delFlag;


    public Boolean flag() {
        return false;
    }

    public Integer now() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public String uid() {
        return StringUtil.getUid();
    }

    public String getOpBy() {
        return opBy;
    }

    public void setOpBy(String opBy) {
        this.opBy = opBy;
    }

    public Integer getOpAt() {
        return opAt;
    }

    public void setOpAt(Integer opAt) {
        this.opAt = opAt;
    }

    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    public String uuid() {
        return R.UU32().toLowerCase();
    }

    @Override
    public String toString() {
        return String.format("/*%s*/%s", super.toString(), Json.toJson(this, JsonFormat.compact()));
    }
}
