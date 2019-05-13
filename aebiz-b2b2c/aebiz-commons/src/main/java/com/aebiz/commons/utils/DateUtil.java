package com.aebiz.commons.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wizzer on 2016/12/21.
 */
@Component
public class DateUtil {
    private static final Locale DEFAULT_LOCALE = Locale.CHINA;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 获取当前时间(HH:mm:ss)
     *
     * @return
     */
    public static String getDate() {
        return DateFormatUtils.format(new Date(), "yyyy-MM-dd", DEFAULT_LOCALE);
    }

    /**
     * 获取当前时间(HH:mm:ss)
     *
     * @return
     */
    public static String getTime() {
        return DateFormatUtils.format(new Date(), "HH:mm:ss", DEFAULT_LOCALE);
    }

    /**
     * 获取当前时间(yyyy-MM-dd HH:mm:ss)
     *
     * @return
     */
    public static String getDateTime() {
        return DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss", DEFAULT_LOCALE);
    }

    /**
     * 转换日期格式(yyyy-MM-dd HH:mm:ss)
     *
     * @param date
     * @return
     */
    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss", DEFAULT_LOCALE);
    }

    /**
     * 转换日期格式(yyyy-MM-dd HH:mm:ss)
     *
     * @param date
     * @param f
     * @return
     */
    public static String format(Date date, String f) {
        if (date == null) return "";
        return DateFormatUtils.format(date, f, DEFAULT_LOCALE);
    }

    /**
     * 时间戳日期
     *
     * @param time
     * @return
     */
    public static String getDate(long time) {
        return DateFormatUtils.format(new Date(time * 1000), "yyyy-MM-dd HH:mm:ss", DEFAULT_LOCALE);
    }

    /**
     * 时间戳日期
     *
     * @param time
     * @param f
     * @return
     */
    public static String getDate(long time, String f) {
        return DateFormatUtils.format(new Date(time * 1000), f, DEFAULT_LOCALE);
    }

    /**
     * 通过字符串时间获取时间戳
     *
     * @param date
     * @return
     */
    public static int getTime(String date) {
        try {
            return (int) (sdf.parse(date).getTime() / 1000);
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * 获取日期时间戳
     *
     * @param date
     * @return
     */
    public static int getTime(Date date) {
        try {
            return (int) (date.getTime() / 1000);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取x月第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date, int amount) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, amount);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取x月最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date, int amount) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, amount);
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取上个月第一天
     *
     * @return
     */
    public static Date getFirstDayOfPervMonth() {
        return getFirstDayOfMonth(new Date(), -1);
    }

    /**
     * 获取上个月最后一天
     *
     * @return
     */
    public static Date getLastDayOfPervMonth() {
        return getLastDayOfMonth(new Date(), 0);
    }

    /**
     * 获取下个月第一天
     *
     * @return
     */
    public static Date getFirstDayOfNextMonth() {
        return getFirstDayOfMonth(new Date(), 1);
    }

    /**
     * 获取下个月最后一天
     *
     * @return
     */
    public static Date getLastDayOfNextMonth() {
        return getLastDayOfMonth(new Date(), 2);
    }


    /**
     * 判断当前会员是否到期
     * @param payAt
     * @param monthlyNum
     * @return
     */
    public static boolean videoMonthlyTime(String payAt,int monthlyNum){
        //到期时间
        String nowDate = null;
        String date = payAt;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = format.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            calendar.add(Calendar.MONTH, monthlyNum);
            nowDate = format.format(calendar.getTime());
            System.out.println(nowDate);

            //到期时间转化为时间戳
            int endTime = DateUtil.getTime(nowDate);

            //获取当前时间戳
            int thisTime = DateUtil.getTime(new Date());

            if(endTime<thisTime){
                System.out.println("当前会员已到期");
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

}
