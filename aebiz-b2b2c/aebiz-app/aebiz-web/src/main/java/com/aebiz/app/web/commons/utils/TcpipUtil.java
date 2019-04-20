package com.aebiz.app.web.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * tcp/ip 工具类
* Description: 
* @ClassName: TcpipUtil 
* @author Jalf
* @since 2016年5月31日 上午9:20:28 
* Copyright  foxtail All right reserved.
 */
public class TcpipUtil {
    protected static final Logger logger = LoggerFactory.getLogger(TcpipUtil.class);
	/**
	* Description:获得ip地址
	* @Title: getIpAddr  
	* @author Jalf
	* @since 2016年5月31日 上午9:20:45
	* @param request
	* @return
	* Copyright  foxtail All right reserved.
	 */
	public static String getClientRealIp(HttpServletRequest request){
		String ip = request.getHeader("X-Forwarded-For");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
        if(ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                ip = ip.substring(0,index);
            }
        }
		return ip;
	}

}
