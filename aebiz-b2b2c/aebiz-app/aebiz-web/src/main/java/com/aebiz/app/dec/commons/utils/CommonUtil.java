package com.aebiz.app.dec.commons.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;

public class CommonUtil {

	/**
	 * 获取商品详情页的 URL
	 * @param contextPath
	 * @param url
	 * @param productUuid
	 * @return
	 */
	public static String getProductDetailUrl(String contextPath,String url,String productUuid){
		if(url == null){
			return "";
		}
		if(url.indexOf("?")!=-1){
			return MessageFormat.format("{0}{1}&productUuid={2}", new Object[]{contextPath,url,productUuid});
		}else{
			return MessageFormat.format("{0}{1}?productUuid={2}", new Object[]{contextPath,url,productUuid});
		}
	}
	
	/**
	 * 获取商品列表页Url
	 * @param contextPath
	 * @param url
	 * @param categoryUuid
	 * @return
	 * String
	 */
	public static String getProductListUrl(String contextPath,String url,String categoryUuid){
		if(url == null){
			return "";
		}
		if(url.indexOf("?")!=-1){
			return MessageFormat.format("{0}{1}&categoryUuid={2}", new Object[]{contextPath,url,categoryUuid});
		}else{
			return MessageFormat.format("{0}{1}?categoryUuid={2}", new Object[]{contextPath,url,categoryUuid});
		}
	}

	public static String getFullPath(String contextPath,String url,String key,String value){
		if(url == null){
			return "";
		}
		if(url.indexOf("?")!=-1){
			return MessageFormat.format("{0}{1}&"+key+"={2}", new Object[]{contextPath,url,value});
		}else{
			return MessageFormat.format("{0}{1}?"+key+"={2}", new Object[]{contextPath,url,value});
		}
	}
	
	/**
	 * 获取文章详情Url
	 * @param contextPath
	 * @param url
	 * @param categoryUuid
	 * @return
	 * String
	 */
	public static String getContentUrl(String contextPath,String url,String contentUuid){
		if(url == null){
			return "";
		}
		if(url.indexOf("?")!=-1){
			return MessageFormat.format("{0}{1}&contentUuid={2}", new Object[]{contextPath,url,contentUuid});
		}else{
			return MessageFormat.format("{0}{1}?contentUuid={2}", new Object[]{contextPath,url,contentUuid});
		}
	}


	/**
	 * GET请求
	 */
	public static String httpCallGet(String url) {
		String ret = "";

		CloseableHttpClient client = HttpClientBuilder.create().build();

		HttpGet get = new HttpGet(url);
		StringBuffer sb = new StringBuffer();
		try {
			HttpResponse resp = client.execute(get);

			HttpEntity entity = resp.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent(), HTTP.UTF_8));

			String result = br.readLine();
			while (result != null) {
				sb.append(result);
				result = br.readLine();
			}

			String str = "";
			if(StringUtils.isNotBlank(sb.toString())) {
				str = sb.toString();
			}
			ret = str.trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
