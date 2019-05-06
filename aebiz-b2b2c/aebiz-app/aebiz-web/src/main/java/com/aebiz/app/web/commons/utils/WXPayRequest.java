//package com.aebiz.app.web.commons.utils;
//
//
//import com.github.wxpay.sdk.WXPayConfig;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpHost;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.config.RegistryBuilder;
//import org.apache.http.conn.ConnectTimeoutException;
//import org.apache.http.conn.socket.ConnectionSocketFactory;
//import org.apache.http.conn.socket.PlainConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
//import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
//import org.apache.http.util.EntityUtils;
//import org.nutz.ioc.impl.PropertiesProxy;
//import org.springframework.beans.factory.annotation.Autowired;
//import sun.net.www.protocol.https.DefaultHostnameVerifier;
//
//import javax.net.ssl.KeyManagerFactory;
//import javax.net.ssl.SSLContext;
//import java.io.InputStream;
//import java.net.SocketTimeoutException;
//import java.net.UnknownHostException;
//import java.security.KeyStore;
//import java.security.SecureRandom;
//
//public class WXPayRequest {
//
//
//    @Autowired
//    private PropertiesProxy config;
//
//    private WXPayConfig wxconfig;
//
//    public static final String WXPAYSDK_VERSION = "WXPaySDK/3.0.9";
//    public static final String USER_AGENT = WXPAYSDK_VERSION +
//            " (" + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version") +
//            ") Java/" + System.getProperty("java.version") + " HttpClient/" + HttpClient.class.getPackage().getImplementationVersion();
//
//
//    /**
//     * 请求，只请求一次，不做重试
//     * @param domain 域名
//     * @param urlSuffix urlSuffix
//     * @param uuid uuid
//     * @param data data
//     * @param connectTimeoutMs connectTimeoutMs
//     * @param readTimeoutMs readTimeoutMs
//     * @param useCert 是否使用证书，针对退款、撤销等操作
//     * @return response string
//     */
//    private String requestOnce(final String domain, String urlSuffix, String uuid, String data, int connectTimeoutMs, int readTimeoutMs, boolean useCert) throws Exception {
//        BasicHttpClientConnectionManager connManager;
//        if (useCert) {
//            // 证书
//            char[] password =  config.get("wx.pay.mchid").toCharArray();
//            InputStream certStream = WXPayUtil.getCertStream();
//            KeyStore ks = KeyStore.getInstance("PKCS12");
//            ks.load(certStream, password);
//
//            // 实例化密钥库 & 初始化密钥工厂
//            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            kmf.init(ks, password);
//
//            // 创建 SSLContext
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());
//
//            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
//                    sslContext,
//                    new String[]{"TLSv1"},
//                    null,
//                    new DefaultHostnameVerifier());
//
//            connManager = new BasicHttpClientConnectionManager(
//                    RegistryBuilder.<ConnectionSocketFactory>create()
//                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                            .register("https", sslConnectionSocketFactory)
//                            .build(),
//                    null,
//                    null,
//                    null
//            );
//        }
//        else {
//            connManager = new BasicHttpClientConnectionManager(
//                    RegistryBuilder.<ConnectionSocketFactory>create()
//                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                            .register("https", SSLConnectionSocketFactory.getSocketFactory())
//                            .build(),
//                    null,
//                    null,
//                    null
//            );
//        }
//        HttpClient httpClient;
//        if (Env.ProxyHost != null && Env.ProxyHost.length() > 0) {
//        	HttpHost proxy = new HttpHost(Env.ProxyHost, Env.ProxyPort);
//        	DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//        	httpClient = HttpClientBuilder.create()
//                .setConnectionManager(connManager)
//                .setRoutePlanner(routePlanner)
//                .build();
//	    } else {
//	        httpClient = HttpClientBuilder.create()
//                .setConnectionManager(connManager)
//                .build();
//	    }
//
//        String url = "https://" + domain + urlSuffix;
//        HttpPost httpPost = new HttpPost(url);
//
//        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs).setConnectTimeout(connectTimeoutMs).build();
//        httpPost.setConfig(requestConfig);
//
//        StringEntity postEntity = new StringEntity(data, "UTF-8");
//        httpPost.addHeader("Content-Type", "text/xml");
//        httpPost.addHeader("User-Agent", USER_AGENT + " " +config.get("wx.pay.mchid"));
//        httpPost.setEntity(postEntity);
//
//        HttpResponse httpResponse = httpClient.execute(httpPost);
//        HttpEntity httpEntity = httpResponse.getEntity();
//        return EntityUtils.toString(httpEntity, "UTF-8");
//    }
//
//
//    private String request(String urlSuffix, String uuid, String data, int connectTimeoutMs, int readTimeoutMs, boolean useCert, boolean autoReport) {
//        //Exception exception = null;
//        long elapsedTimeMillis = 0;
//        long startTimestampMs = WXPayUtil.getCurrentTimestampMs();
//        //boolean firstHasDnsErr = false;
//        //boolean firstHasConnectTimeout = false;
//        //boolean firstHasReadTimeout = false;
//        /*IWXPayDomain.DomainInfo domainInfo = config.getWXPayDomain().getDomain(config);
//        if(domainInfo == null){
//            throw new Exception("WXPayConfig.getWXPayDomain().getDomain() is empty or null");
//        }*/
//        String domain = config.getWXPayDomain();
//        try {
//            String result = requestOnce(domain, urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, useCert);
//            elapsedTimeMillis = WXPayUtil.getCurrentTimestampMs()-startTimestampMs;
//            //config.getWXPayDomain().report(domainInfo.domain, elapsedTimeMillis, null);
//            /*WXPayReport.getInstance(config).report(
//                    uuid,
//                    elapsedTimeMillis,
//                    domain,
//                    true,
//                    connectTimeoutMs,
//                    readTimeoutMs,
//                    firstHasDnsErr,
//                    firstHasConnectTimeout,
//                    firstHasReadTimeout);*/
//            WXPayUtil.getLogger().info("WXPay request time elapse::{}", elapsedTimeMillis);
//            return result;
//        }
//        catch (UnknownHostException ex) {  // dns 解析错误，或域名不存在
//            //exception = ex;
//            //firstHasDnsErr = true;
//            elapsedTimeMillis = WXPayUtil.getCurrentTimestampMs()-startTimestampMs;
//            WXPayUtil.getLogger().warn("UnknownHostException for domainInfo {}", domain);
//            /*WXPayReport.getInstance(config).report(
//                    uuid,
//                    elapsedTimeMillis,
//                    domainInfo.domain,
//                    domainInfo.primaryDomain,
//                    connectTimeoutMs,
//                    readTimeoutMs,
//                    firstHasDnsErr,
//                    firstHasConnectTimeout,
//                    firstHasReadTimeout
//            );*/
//        }
//        catch (ConnectTimeoutException ex) {
//            // exception = ex;
//            //firstHasConnectTimeout = true;
//            elapsedTimeMillis = WXPayUtil.getCurrentTimestampMs()-startTimestampMs;
//            WXPayUtil.getLogger().warn("connect timeout happened for domainInfo {}"+domain);
//            /*WXPayReport.getInstance(config).report(
//                    uuid,
//                    elapsedTimeMillis,
//                    domainInfo.domain,
//                    domainInfo.primaryDomain,
//                    connectTimeoutMs,
//                    readTimeoutMs,
//                    firstHasDnsErr,
//                    firstHasConnectTimeout,
//                    firstHasReadTimeout
//            );*/
//        }
//        catch (SocketTimeoutException ex) {
//            //exception = ex;
//            //firstHasReadTimeout = true;
//            elapsedTimeMillis = WXPayUtil.getCurrentTimestampMs()-startTimestampMs;
//            WXPayUtil.getLogger().warn("timeout happened for domainInfo {}", domain);
//            /*WXPayReport.getInstance(config).report(
//                    uuid,
//                    elapsedTimeMillis,
//                    domainInfo.domain,
//                    domainInfo.primaryDomain,
//                    connectTimeoutMs,
//                    readTimeoutMs,
//                    firstHasDnsErr,
//                    firstHasConnectTimeout,
//                    firstHasReadTimeout);*/
//        }
//        catch (Exception ex) {
//            // exception = ex;
//            elapsedTimeMillis = WXPayUtil.getCurrentTimestampMs()-startTimestampMs;
//            WXPayUtil.getLogger().error("WxPay request error", ex);
//            /*WXPayReport.getInstance(config).report(
//                    uuid,
//                    elapsedTimeMillis,
//                    domainInfo.domain,
//                    domainInfo.primaryDomain,
//                    connectTimeoutMs,
//                    readTimeoutMs,
//                    firstHasDnsErr,
//                    firstHasConnectTimeout,
//                    firstHasReadTimeout);*/
//        }
//        WXPayUtil.getLogger().info("WxPay elapse millisecond time: {}", elapsedTimeMillis);
//        //config.getWXPayDomain().report(domain, elapsedTimeMillis, exception);
//        return "";
//    }
//
//
//    /**
//     * 可重试的，非双向认证的请求
//     * @param urlSuffix url
//     * @param uuid uuid
//     * @param data 数据
//     * @return response string
//     */
//    public String requestWithoutCert(String urlSuffix, String uuid, String data, boolean autoReport) {
//        return this.request(urlSuffix, uuid, data,6*1000, 8*1000, false, autoReport);
//    }
//
//    /**
//     * 可重试的，非双向认证的请求
//     * @param urlSuffix url
//     * @param uuid uuid
//     * @param data 数据
//     * @param connectTimeoutMs 链接超时时间
//     * @param readTimeoutMs 读取超时时间
//     * @return response string
//     */
//    public String requestWithoutCert(String urlSuffix, String uuid, String data, int connectTimeoutMs, int readTimeoutMs, boolean autoReport) {
//        return this.request(urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, false, autoReport);
//    }
//
//    /**
//     * 可重试的，双向认证的请求
//     * @param urlSuffix url
//     * @param uuid uuid
//     * @param data 数据
//     * @return response string
//     */
//    public String requestWithCert(String urlSuffix, String uuid, String data, boolean autoReport) {
//        return this.request(urlSuffix, uuid, data, 6*1000, 8*1000, true, autoReport);
//    }
//
//    /**
//     * 可重试的，双向认证的请求
//     * @param urlSuffix url
//     * @param uuid uuid
//     * @param data 数据
//     * @param connectTimeoutMs 链接超时时间
//     * @param readTimeoutMs 读取超时时间
//     * @return string
//     */
//    public String requestWithCert(String urlSuffix, String uuid, String data, int connectTimeoutMs, int readTimeoutMs, boolean autoReport) {
//        return this.request(urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, true, autoReport);
//    }
//}
