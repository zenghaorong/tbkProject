/**
 * NumberUtil.java
 * 
 * Copyright(C)2008 Founder Corporation.
 * written by Founder Corp.
 */
package com.aebiz.app.web.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * [类名]<br>
 * NumberUtil<br>
 * [功能概要]<br>
 * <br>
 * <br>
 * [変更履歴]<br>
 * 2009-5-12 ver1.00 新建 zhaoxinsheng<br>
 * 
 * @author FOUNDER CORPORATION
 * @version 1.00
 */
public class NumberUtil {
	protected static final Logger logger = LoggerFactory.getLogger(NumberUtil. class);
    /**
     * 转换成整型
     * @param str
     * @return
     */
    public static int convertToInt(String str) {
        return Integer.parseInt(str.replaceAll(",", ""));
    }
    
    /**
     * 转换成BigDecimal
     */
    public static BigDecimal convertToBigDecimal(String str) {
        return new BigDecimal(str.replaceAll(",", ""));
    }
    
    /**
     * 非数字判断
     * @param str
     * @return
     */
	public static boolean isNumeric(String str){
		if(str ==null || str.length()==0){
			return false;
		}
	    Pattern pattern = Pattern.compile("[0-9]+");
	    return pattern.matcher(str).matches();    
	}
	/**
	 * 判断字符串是否是整型，没有增加长度校验，转换可能溢出；
	 * @param str
	 * @return
	 */
	public static boolean isIntegerNumeric(String str){
		if(str ==null || str.length()==0){
			return false;
		}
	    return str.matches("^[1-9]{1}[0-9]*$");
	}
	/**
	 * 字符串转为数字，如果不能转换或转换异常则返回默认值
	 * @param str
	 * @param defaultValue
	 * @return
	 */
	public static int getIntNumber(String str, int defaultValue){
		int res = defaultValue;
		if(str != null && str.length() > 0 && str.length() <= 12 && str.matches("^\\d+$")){
			try{
				res = Integer.parseInt(str);
			} catch(Exception ex){
				logger.error("字符串转int错误：", ex);
			}
		}
		return res;
	} 
	public static long getLong(String str, long defaultValue){
		long res = defaultValue;
		if(str != null && str.length() > 0 && str.matches("^\\d+$")){
			try{
				res = Long.parseLong(str);
			} catch(Exception ex){
				logger.error("字符串转int错误：", ex);
			}
		}
		return res;
	}
	public static BigDecimal getBigDecimal(String str, BigDecimal defaultValue){
		BigDecimal res = defaultValue;
		if(str != null && str.length() > 0 && (str.matches("^[\\-\\+]?\\d+$") || str.matches("^[\\-\\+]?\\d+\\.\\d+$"))){
			try{
				res = new BigDecimal(str);
			} catch(Exception ex){
				logger.error("字符串转BigDecimal错误：", ex);
			}
		}
		return res;
	}
	/**
     * 保留2位小数
     * @param number
     * @return
     */
	public static Double formatNumer(Double number){
		DecimalFormat df = new DecimalFormat("###.00");
	    return Double.valueOf(df.format(number));
	} 
	/**
     * 保留2位小数
     */
	public static String getString(BigDecimal number){
		if(number == null){
			return "";
		}
		DecimalFormat df = new DecimalFormat("###.00");
	    return df.format(number);
	} 
	
	public static String formatNumber(Double number, String format){
		DecimalFormat df = new DecimalFormat(format);
	    return df.format(number);
	}
	/**
	 * 格式化 BigDecimal 字符显示，没有格式则按两位小数方式展示
	 * @param number
	 * @param format
	 * @return
	 */
	public static String getNumberString(BigDecimal number, String format){
		if(number == null){
			return "";
		}
		if(format == null || format.length() == 0){
			format = "###.00";
		}
		DecimalFormat df = new DecimalFormat(format);
	    return df.format(number);
	}
	
	public static String leftPadTime(Integer number){
		if(number == null){
			return "";
		}
		if(number<10){
			return "0"+number;
		}else{
			return number.toString();
		}
	}
	
	//把金额用逗号隔开
	public static String numToStr(Integer money){
		String moneyStr=money.toString();
		if(moneyStr==null){
			return null;
		}else if(moneyStr.length()<=3){
			return moneyStr;
		}else{
			//把金额反转，每3位加一个逗号
			char[] momey = moneyStr.toCharArray();
			StringBuffer moneyReverse=new StringBuffer();
			for(int i=momey.length-1;i>=0;i--){
				moneyReverse.append(momey[i]);
			}
			String first=moneyReverse.substring(0,3);//前三位
			String second="";//剩余位
			if(moneyReverse.toString().length()<=6){
				second=moneyReverse.substring(3);
			}else if(moneyReverse.toString().length()<=9){
				second=moneyReverse.substring(3,6)+","+moneyReverse.substring(6);
			}else if(moneyReverse.toString().length()<=12){
				second=moneyReverse.substring(3,6)+","+moneyReverse.substring(6,9)+","+moneyReverse.substring(9);
			}
			//反转恢复
			char[] momey_ = (first+","+second).toCharArray();
			StringBuffer moneyHuanyuan=new StringBuffer();
			for(int i=momey_.length-1;i>=0;i--){
				moneyHuanyuan.append(momey_[i]);
			}
			return moneyHuanyuan.toString();
		}
	}
	
}
