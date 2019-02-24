package com.aebiz.commons.utils;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by wizzer on 2016/12/20.
 */
@Component
public class StringUtil {
    /**
     * 从seesion获取uid
     *
     * @return
     */
    public static String getUid() {
        try {
            HttpServletRequest request = SpringUtil.getRequest();
            if (request != null) {
                return Strings.sNull(request.getSession(true).getAttribute("uid"));
            }
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 从seesion获取用户名
     *
     * @return
     */
    public static String getUsername() {
        try {
            HttpServletRequest request = SpringUtil.getRequest();
            if (request != null) {
                return Strings.sNull(request.getSession(true).getAttribute("username"));
            }
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 从seesion获取店铺用户uid
     *
     * @return
     */
    public static String getStoreUid() {
        try {
            HttpServletRequest request = SpringUtil.getRequest();
            if (request != null) {
                return Strings.sNull(request.getSession(true).getAttribute("storeUid"));
            }
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 从seesion获取店铺用户名
     *
     * @return
     */
    public static String getStoreUsername() {
        try {
            HttpServletRequest request = SpringUtil.getRequest();
            if (request != null) {
                return Strings.sNull(request.getSession(true).getAttribute("storeUsername"));
            }
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 从seesion获取登录会员用户uid
     *
     * @return
     */
    public static String getMemberUid() {
        try {
            HttpServletRequest request = SpringUtil.getRequest();
            if (request != null) {
                return Strings.sNull(request.getSession(true).getAttribute("memberUid"));
            }
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 从seesion获取登录会员用户名
     *
     * @return
     */
    public static String getMemberUsername() {
        try {
            HttpServletRequest request = SpringUtil.getRequest();
            if (request != null) {
                return Strings.sNull(request.getSession(true).getAttribute("memberUsername"));
            }
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 从seesion获取店铺id
     *
     * @return
     */
    public static String getStoreId() {
        try {
            HttpServletRequest request = SpringUtil.getRequest();
            if (request != null) {
                return Strings.sNull(request.getSession(true).getAttribute("storeId"));
            }
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 从seesion获取店铺名称
     *
     * @return
     */
    public static String getStoreName() {
        try {
            HttpServletRequest request = SpringUtil.getRequest();
            if (request != null) {
                return Strings.sNull(request.getSession(true).getAttribute("storeName"));
            }
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 去掉URL中?后的路径
     *
     * @param p
     * @return
     */
    public static String getPath(String p) {
        if (Strings.sNull(p).contains("?")) {
            return p.substring(0, p.indexOf("?"));
        }
        return Strings.sNull(p);
    }

    /**
     * 获得父节点ID
     *
     * @param s
     * @return
     */
    public static String getParentId(String s) {
        if (!Strings.isEmpty(s) && s.length() > 4) {
            return s.substring(0, s.length() - 4);
        }
        return "";
    }

    /**
     * 得到n位随机数
     *
     * @param s
     * @return
     */
    public static String getRndNumber(int s) {
        Random ra = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s; i++) {
            sb.append(String.valueOf(ra.nextInt(8)));
        }
        return sb.toString();
    }

    /**
     * 得到n位随机字符串
     * a-z,A-Z,0-9等组合，不包括特殊字符和汉字
     *
     * @param n 随机字符串的长度
     * @return 随机字符串
     */
    public static String getRndString(int n) {
        // 50个数字，52个字母 数字重复是为了和字母差不多，不然大多数情况下生成的都是字母居多，甚至没有数字。
        String rnd ="0123456789abcdefghijklmn0123456789opqrstuvwxyz0123456789ABCDEFGHIGKLMN0123456789OPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = random.nextInt(rnd.length());
            sb.append(rnd.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 判断是否以字符串开头
     *
     * @param str
     * @param s
     * @return
     */
    public boolean startWith(String str, String s) {
        return Strings.sNull(str).startsWith(Strings.sNull(s));
    }

    /**
     * 判断是否包含字符串
     *
     * @param str
     * @param s
     * @return
     */
    public boolean contains(String str, String s) {
        return Strings.sNull(str).contains(Strings.sNull(s));
    }

    /**
     * 将对象转为JSON字符串（页面上使用）
     *
     * @param obj
     * @return
     */
    public String toJson(Object obj) {
        return Json.toJson(obj, JsonFormat.compact());
    }

    public static boolean isAjax(HttpServletRequest request) {
        boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        String ajaxFlag = null == request.getParameter("ajax") ? "false" : request.getParameter("ajax");
        return ajax || ajaxFlag.equalsIgnoreCase("true");
    }

    public static byte[] getBytesFromObject(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bo = null;
        ObjectOutputStream oo = null;
        try {
            bo = new ByteArrayOutputStream();
            oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            bytes = bo.toByteArray();
        } finally {
            if (bo != null) {
                bo.close();
            }
            if (oo != null) {
                oo.close();
            }
        }
        return bytes;
    }

    public static Object getObjectFromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj = null;
        ByteArrayInputStream bi = null;
        ObjectInputStream oi = null;
        try {
            if (bytes == null) {
                return null;
            }
            bi = new ByteArrayInputStream(bytes);
            oi = new ObjectInputStream(bi);
            obj = oi.readObject();
        } finally {
            if (bi != null) {
                bi.close();
            }
            if (oi != null) {
                oi.close();
            }
        }
        return obj;
    }

    /**
     * 根据生日的月和日获取星座
     *
     * @param month 生日的月
     * @param day   生日的日
     * @return 对应的星座，无就返回空字符串
     */
    public static String getConstellationByBirthday(Integer month, Integer day) {
        final String[] constellationArr = {"水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"};
        final int[] constellationEdgeDay = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};
        String constellation = "";
        if (month != null && day != null) {
            int index = month - 1;
            if (day < constellationEdgeDay[index]) {
                index = index - 1;
            }
            if (index >= 0) {
                constellation = constellationArr[index];
            } else {
                // 如果是一月20号之前，index等于-1，此时应该是算12月份，下标也就是11。
                constellation = constellationArr[11];
            }
        }
        return constellation;
    }

    /**
     * 获取不同尺寸图片
     * @param str 原始地址
     * @param client 客户端(pc/wap/tv)
     * @param type 图片尺寸(thumb/list/alumb)
     * @return
     */
    public static String getImg(String str, String client, String type) {
        String fileUrl = str.substring(0, str.lastIndexOf("."));
        String fileExtName = str.substring(str.lastIndexOf("."));
        return fileUrl + "_" + client + "_" + type + fileExtName;
    }
}
