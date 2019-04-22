/**
 * 
 */
package com.aebiz.app.web.commons.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClientYdUtils {
	
	private static final ResponseHandler<String> HANDLER = new ResponseHandler<String>() {
		public String handleResponse(final HttpResponse response)
				throws ClientProtocolException, IOException {
			HttpEntity entity = response.getEntity();
			return entity != null ? EntityUtils.toString(entity) : null;
		}
	};
	
	public static String get(String url) throws Exception {
		return get(url, null);
	}

	public static String get(String url, List<NameValuePair> headers) throws Exception {
		
		HttpGet get = new HttpGet(url);
		if(headers != null) {
			for(NameValuePair nvp : headers) {
				get.addHeader(nvp.getName(), nvp.getValue());
			}
		}
		return HttpClients.createDefault()
				.execute(get, HANDLER);
	}
	
	public static String post(String url, List<NameValuePair> data) throws Exception {
		return post(url, data, null, null);
	}
	
	public static String post(String url, List<NameValuePair> data, String charset) throws Exception {
		return post(url, data, null, charset);
	}
	
	public static String post(String url, List<NameValuePair> data, List<NameValuePair> headers) throws Exception {
		return post(url, data, headers, null);
	}

	public static String post(String url, List<NameValuePair> data, List<NameValuePair> headers, String charset) throws Exception {
		
		HttpPost post = new HttpPost(url);
		if(data != null) {
			post.setEntity(new UrlEncodedFormEntity(data, Charset.forName(charset == null ? "UTF-8" : charset)));
		}
		if(headers != null) {
			for(NameValuePair nvp : headers) {
				post.addHeader(nvp.getName(), nvp.getValue());
			}
		}
		return HttpClients.createDefault()
				.execute(post, HANDLER);
	}
}
