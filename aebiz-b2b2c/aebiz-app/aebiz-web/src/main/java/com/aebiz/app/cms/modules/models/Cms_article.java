package com.aebiz.app.cms.modules.models;

import java.io.Serializable;

import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.SQL;
import org.nutz.dao.entity.annotation.Table;
import com.aebiz.baseframework.base.model.BaseModel;


/**
 * Created by Wizzer on 2016/7/18.
 */
@Table("cms_article")
public class Cms_article extends BaseModel implements Serializable {
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
    @Comment("文章标题")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String title;

    @Column
    @Comment("文章简介")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String info;

    @Column
    @Comment("文章作者")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String author;

    @Column
    @Comment("标题图")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String picurl;

    @Column
    @Comment("文章内容")
    @ColDefine(type = ColType.TEXT)
    private String content;

    @Column
    @Comment("用户发布图片地址列表用英文逗号隔开")
    @ColDefine(type = ColType.TEXT)
    private String imageUrlStrs;

    @Column
    @Comment("是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @Column
    @Comment("发布时间")
    @ColDefine(type = ColType.INT)
    private Integer publishAt;

    @Column
    @Comment("浏览量")
    @ColDefine(type = ColType.INT, width = 6)
    private Integer pageViews;

    @Column
    @Comment("点赞量")
    @ColDefine(type = ColType.INT, width = 6)
    private Integer likeNum;

    @Column
    @Comment("评论数量")
    @ColDefine(type = ColType.INT, width = 6)
    private Integer evaluateNum;

    @Column
    @Comment("排序字段")
    @Prev({
            @SQL(db= DB.MYSQL,value = "SELECT IFNULL(MAX(location),0)+1 FROM cms_article"),
            @SQL(db= DB.ORACLE,value = "SELECT COALESCE(MAX(location),0)+1 FROM cms_article")
    })
    private Integer location;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String channelId;

    @One(field = "channelId")
    private Cms_channel channel;


    public Integer getPageViews() {
        return pageViews;
    }

    public void setPageViews(Integer pageViews) {
        this.pageViews = pageViews;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

    public Integer getEvaluateNum() {
        return evaluateNum;
    }

    public void setEvaluateNum(Integer evaluateNum) {
        this.evaluateNum = evaluateNum;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(Integer publishAt) {
        this.publishAt = publishAt;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Cms_channel getChannel() {
        return channel;
    }

    public void setChannel(Cms_channel channel) {
        this.channel = channel;
    }

    public String getImageUrlStrs() {
        return imageUrlStrs;
    }

    public void setImageUrlStrs(String imageUrlStrs) {
        this.imageUrlStrs = imageUrlStrs;
    }
}
