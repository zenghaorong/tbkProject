package com.aebiz.app.web.commons.utils;


import org.beetl.core.misc.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpRequestUtil {
	private static Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

	public static Map<String, String> getRequestParameters(Map<String,String[]> requestParams, boolean chDecode){
		Map<String, String> res = new HashMap<String, String>();
		if(requestParams == null){
			return res;
		}
		try {
			Iterator<String> iter = requestParams.keySet().iterator();
			while( iter.hasNext()) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				if(values == null){
					continue;
				}
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
				}
				if(chDecode){
					//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
					valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				}
				res.put(name, valueStr);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("解析request请求异常：：", e);
		}
		return res;
	}

	/**
	 * 发送POST请求，参数要用URLEncoder编码
	 * @param url 地址
	 * @param parameterData 数据
	 * @return String 返回的数据
	 */
	public static String sendPost(String url, String parameterData, String proxyHost, int proxyPort){
		StringBuilder sb = new StringBuilder();
		try{
		//	String param="name="+URLEncoder.encode(jo.toString(),"UTF-8");
			HttpURLConnection httpConn;
			//建立连接
			if(proxyHost != null && !proxyHost.isEmpty()){
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost , proxyPort));
				httpConn = (HttpURLConnection) new URL(url).openConnection(proxy);
			}else{
				httpConn = (HttpURLConnection) new URL(url).openConnection();
			}
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setUseCaches(false);
			httpConn.setRequestMethod("POST");
			
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpConn.setRequestProperty("Charset", "UTF-8");
			//连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
			httpConn.connect();
			//建立输入流，向指向的URL传入参数
			DataOutputStream dos=new DataOutputStream(httpConn.getOutputStream());
			dos.writeBytes(parameterData);
			dos.flush();
			dos.close();
			//获得响应状态
			int resultCode=httpConn.getResponseCode();
			if(HttpURLConnection.HTTP_OK==resultCode){
				String readLine;
				BufferedReader responseReader=new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"UTF-8"));
				while((readLine=responseReader.readLine())!=null){
				sb.append(readLine).append("\n");
				}
				responseReader.close();
				System.out.println(sb.toString());
			}
		} catch(Exception ex){
			logger.error("post请求提交失败:" + url, ex);
		}
		return sb.toString();
	}
	

}
