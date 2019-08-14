package com.aebiz.app.tbk.modules.models;

import com.aebiz.baseframework.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * @Auther: zenghaorong
 * @Date: 2019/8/9  20:55
 * @Description: 淘宝客商品类目编号描述
 */
@Table("tbk_cat")
public class Tbk_cat extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("cid")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String cid;//类目编号

    @Column
    @Comment("isParent")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String isParent;//是否父级


    @Column
    @Comment("name")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String name;//类目名称

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getIsParent() {
        return isParent;
    }

    public void setIsParent(String isParent) {
        this.isParent = isParent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
