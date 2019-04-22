package com.aebiz.app.utils.modules.models;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/22  14:50
 * @Description: 移动短信api登录接口传参
 */
public class SmsYdLoginQO {

    private String enterAccount;//		√	集团客户账号
    private String userName;	//string	√	用户名
    private String userPwd;	//string	√	密码
    private String srcExtCode;	//string	√	扩展码（登录后不可修改，自定义三位数字）
    private int userType;	//int	√	用户类型（固定填0）


    public String getEnterAccount() {
        return enterAccount;
    }

    public void setEnterAccount(String enterAccount) {
        this.enterAccount = enterAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getSrcExtCode() {
        return srcExtCode;
    }

    public void setSrcExtCode(String srcExtCode) {
        this.srcExtCode = srcExtCode;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
