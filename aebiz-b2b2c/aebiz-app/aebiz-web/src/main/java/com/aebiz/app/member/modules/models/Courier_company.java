package com.aebiz.app.member.modules.models;

import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * @Auther: zenghaorong
 * @Date: 2019/6/14  22:33
 * @Description:
 */
@Table("courier_company")
public class Courier_company implements Serializable {

    private static final long serialVersionUID = 1L;


    @Column
    @Comment("物流公司key")
    @ColDefine(type = ColType.VARCHAR, width = 225)
    private String courierCompanyKey;

    @Column
    @Comment("物流公司名称")
    @ColDefine(type = ColType.VARCHAR, width = 225)
    private String courierCompanyName;


    public String getCourierCompanyKey() {
        return courierCompanyKey;
    }

    public void setCourierCompanyKey(String courierCompanyKey) {
        this.courierCompanyKey = courierCompanyKey;
    }

    public String getCourierCompanyName() {
        return courierCompanyName;
    }

    public void setCourierCompanyName(String courierCompanyName) {
        this.courierCompanyName = courierCompanyName;
    }
}
