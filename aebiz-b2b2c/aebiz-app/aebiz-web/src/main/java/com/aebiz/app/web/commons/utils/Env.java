package com.aebiz.app.web.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 引入环境配置项，项目的基本配置直接通过对象属性方式直接使用，
 * spring 版本低没有使用spring自动配置的逻辑，后期有更好的方法再用其他方法
 * @author liuliang
 *
 */
public class Env {
	protected static final Logger logger = LoggerFactory.getLogger(Env. class);
	private static final String pro_file_name = "env.properties";
	
	public static boolean IsDebug;
	private static Map<String, Map<String, String>> config = new HashMap<String, Map<String, String>>();
	public static String ProxyHost;
    public static int ProxyPort;
    
    public static Map<String, String> getConfig(String key) {
    	Map<String, String> map = config.get(key);
		if(map == null){
			logger.error("++++ env config null:" + key);
		} 
		return map;
	}
	public void _loadPropertiesFromSrc() {
		InputStream in = null;
		try {
			in = Env.class.getClassLoader().getResourceAsStream(pro_file_name);
			if (null != in) {
				BufferedReader bf = new BufferedReader(new InputStreamReader(in, "utf-8"));
				Properties properties = new Properties();
				try {
					properties.load(bf);
					loadProperties(properties);
				} catch (IOException e) {
					throw e;
				}
			} else {
				logger.error(pro_file_name + "属性文件未能在classpath指定的目录找到!");
				return;
			}
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	private void loadProperties(Properties pro) {
    	Env.IsDebug = "true".equalsIgnoreCase(pro.getProperty("debug", "false"));
    	Env.ProxyHost = pro.getProperty("proxy.host");
        Env.ProxyPort = NumberUtil.getIntNumber(pro.getProperty("proxy.port", "8080"), 8080) ;
    	Map<String, String> configMap;
    	for(Entry<Object, Object> entry : pro.entrySet()){
    		String key = (String)entry.getKey();
    		if(key == null || key.length()==0){
    			continue;
    		}
    		String[] keyArray = key.split("\\.");
    		if(keyArray.length == 1){
    			configMap = new HashMap<String, String>();
    			configMap.put(keyArray[0], (String)entry.getValue());
    			config.put(keyArray[0], configMap);
    		}else if(keyArray.length == 2){
    			configMap = config.get(keyArray[0]);
    			if(configMap == null){
    				configMap = new HashMap<String, String>();
    			}
    			configMap.put(keyArray[1], (String)entry.getValue());
    			config.put(keyArray[0], configMap);
    		}
    	}
    	
       
       
	}
}
