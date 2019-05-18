package com.aebiz.app.cms.modules.models;

import com.aebiz.app.acc.modules.models.Account_info;
import org.nutz.dao.entity.annotation.*;

/**
 * @Auther: zenghaorong
 * @Date: 2019/3/31  14:13
 * @Description: 回复实体类
 */
public class ReplyVO {

    @Column
    @Comment("评论人名称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String reviewOpName;

    @Column
    @Comment("回复父级发布人名称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String reviewFatherName;

    @Column
    @Comment("回复父级发布人编号")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String reviewFatherId;

    @Column
    @Comment("评论回复内容")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String content;

    @One(field = "reviewOpId")
    private Account_info account_info;

    public Account_info getAccount_info() {
        return account_info;
    }

    public void setAccount_info(Account_info account_info) {
        this.account_info = account_info;
    }


    public String getReviewOpName() {
        return reviewOpName;
    }

    public void setReviewOpName(String reviewOpName) {
        this.reviewOpName = reviewOpName;
    }

    public String getReviewFatherName() {
        return reviewFatherName;
    }

    public void setReviewFatherName(String reviewFatherName) {
        this.reviewFatherName = reviewFatherName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReviewFatherId() {
        return reviewFatherId;
    }

    public void setReviewFatherId(String reviewFatherId) {
        this.reviewFatherId = reviewFatherId;
    }
}
