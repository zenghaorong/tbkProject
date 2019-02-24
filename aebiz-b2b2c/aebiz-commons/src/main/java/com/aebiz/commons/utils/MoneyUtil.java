package com.aebiz.commons.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by wizzer on 2017/06/08.
 */
@Component
public class MoneyUtil {

    /**
     * 分转换为元
     *
     * @param fen 分
     * @return 元
     */
    public static String fenToYuan(int fen) {
        try {
            return new BigDecimal(fen).divide(new BigDecimal(100)).setScale(2).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    /**
     * 元转换为分
     *
     * @param yuan 元
     * @return 分
     */
    public static int yuanToFen(String yuan) {
        try {
            return BigDecimal.valueOf(Double.valueOf(yuan)).multiply(new BigDecimal(100)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 分转换为万元
     *
     * @param fen 分
     * @return 元
     */
    public static String fenToWan(long fen) {
        try {
            return new BigDecimal(fen).divide(new BigDecimal(1000000)).setScale(2).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    /**
     * 万元转换为分
     *
     * @param yuan 元
     * @return 分
     */
    public static long wanToFen(String yuan) {
        try {
            return BigDecimal.valueOf(Double.valueOf(yuan)).multiply(new BigDecimal(1000000)).longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 计算两个价格折扣率
     *
     * @param price1
     * @param price2
     * @return
     */
    public static String rate(int price1, int price2) {
        try {
            BigDecimal num1 = new BigDecimal(price1 * 10);
            BigDecimal num2 = new BigDecimal(price2);
            BigDecimal num3 = num1.divide(num2, 1, BigDecimal.ROUND_HALF_UP);
            return num3.toString().replace(".0", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "无";
    }

    /**
     * 计算折扣后价格
     *
     * @param price
     * @param rate
     * @return
     */
    public static int getRatePrice(int price, int rate) {
        if (rate > 0 && rate < 100) {
            try {
                BigDecimal num1 = new BigDecimal(price * rate);
                BigDecimal num2 = new BigDecimal(100);
                BigDecimal num3 = num1.divide(num2, 0, BigDecimal.ROUND_HALF_UP);
                return num3.intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return price;
    }
}
