package com.aebiz.app.web.commons.utils;

import com.aebiz.app.acc.modules.models.em.PasswordStrengthEnum;

public class CheckPasswordUtil {

    /**
     * 检测密码强度
     *
     * @return Z = 字母 S = 数字 T = 特殊字符
     */
    public static Integer checkPassword(String passwordStr) {
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (passwordStr.matches(regexZ) || passwordStr.matches(regexS)||passwordStr.matches(regexT)) {
            return PasswordStrengthEnum.WEAK.getKey();
        }
        if (passwordStr.matches(regexZT) || passwordStr.matches(regexST) || passwordStr.matches(regexZS)) {
            return PasswordStrengthEnum.MIDDLE.getKey();
        }
        if (passwordStr.matches(regexZST)) {
            return PasswordStrengthEnum.STRONG.getKey();
        }

        return PasswordStrengthEnum.WEAK.getKey();
    }
}
