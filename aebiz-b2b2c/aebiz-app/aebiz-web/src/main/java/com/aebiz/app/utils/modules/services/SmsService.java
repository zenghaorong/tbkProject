package com.aebiz.app.utils.modules.services;


import com.aebiz.app.utils.modules.models.SmsYdLoginQO;

/***
 * 短信模块接口
 */
public interface SmsService{


    /**
     * 发送短信
     */
    String sendMessages(String msg,String mobile);

    /**
     * 新移动发送短信接口
     * 登录接口 获取sessionId
     */
    String SmsYdLogin();


}
