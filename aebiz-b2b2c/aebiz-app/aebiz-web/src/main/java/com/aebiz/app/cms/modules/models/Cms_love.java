package com.aebiz.app.cms.modules.models;

import com.aebiz.baseframework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/21  19:21
 * @Description: 内容点赞记录表
 */
@Table("cms_love")
public class Cms_love extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("ig(view.tableName,'')")})
    private String id;

    @Column
    @Comment("帐号ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String accountId;

    @Column
    @Comment("内容ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String cmsId;

    @Column
    @Comment("内容类型 1 文章 2视频")
    @ColDefine(type = ColType.VARCHAR, width = 2)
    private String cmsType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCmsId() {
        return cmsId;
    }

    public void setCmsId(String cmsId) {
        this.cmsId = cmsId;
    }

    public String getCmsType() {
        return cmsType;
    }

    public void setCmsType(String cmsType) {
        this.cmsType = cmsType;
    }
}
