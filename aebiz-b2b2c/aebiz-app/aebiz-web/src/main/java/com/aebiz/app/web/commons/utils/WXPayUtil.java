package com.aebiz.app.web.commons.utils;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;


public class WXPayUtil {

    private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final Random RANDOM = new SecureRandom();

    private static final Log log = Logs.get();

    /**
     * XML格式字符串转换为Map
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     */
    public static Map<String, String> xmlToMap(String strXML) {
        Map<String, String> data = new HashMap<String, String>();
        if(strXML == null || strXML.length() == 0){
            WXPayUtil.getLogger().error("xmlToMap XML is null");
            return data;
        }
        try {
            DocumentBuilder documentBuilder = WXPayXmlUtil.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            WXPayUtil.getLogger().warn("Invalid XML, can not convert to map. Error message: {}. XML content: {}", ex.getMessage(), strXML);
            return data;
        }

    }

    /**
     * 将Map转换为XML格式的字符串
     * @param data Map类型数据
     * @return XML格式的字符串
     */
    public static String mapToXml(Map<String, String> data){
        if(data == null || data.size() == 0){
            return null;
        }
    	try {
	    	org.w3c.dom.Document document = WXPayXmlUtil.newDocument();
	        org.w3c.dom.Element root = document.createElement("xml");
	        document.appendChild(root);
	        for (String key: data.keySet()) {
	            String value = data.get(key);
	            if (value == null) {
	                value = "";
	            }
	            value = value.trim();
	            org.w3c.dom.Element filed = document.createElement(key);
	            filed.appendChild(document.createTextNode(value));
	            root.appendChild(filed);
	        }
        
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
	        DOMSource source = new DOMSource(document);
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        StringWriter writer = new StringWriter();
	        StreamResult result = new StreamResult(writer);
	        transformer.transform(source, result);
	        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
            writer.close();
            return output;
        } catch (Exception ex) {
        	getLogger().error("微信支付MAP转XML异常::", ex);
        }
        return "";
    }


    /**
     * 生成带有 sign 的 XML 格式字符串
     * @param data Map类型数据
     * @param key API密钥
     * @return 含有sign字段的XML
     */
    public static String generateSignedXml(final Map<String, String> data, String key) {
        return generateSignedXml(data, key, WXPayConstants.SignType.MD5);
    }

    /**
     * 生成带有 sign 的 XML 格式字符串
     * @param data Map类型数据
     * @param key API密钥
     * @param signType 签名类型
     * @return 含有sign字段的XML
     */
    public static String generateSignedXml(final Map<String, String> data, String key, WXPayConstants.SignType signType) {
        String sign = generateSignature(data, key, signType);
        data.put(WXPayConstants.FIELD_SIGN, sign);
        return mapToXml(data);
    }


    /**
     * 判断签名是否正确
     * @param xmlStr XML格式数据
     * @param key API密钥
     * @return 签名是否正确
     */
    public static boolean isSignatureValid(String xmlStr, String key) {
        Map<String, String> data = xmlToMap(xmlStr);
        if (!data.containsKey(WXPayConstants.FIELD_SIGN) ) {
            return false;
        }
        String sign = data.get(WXPayConstants.FIELD_SIGN);
        return generateSignature(data, key).equals(sign);
    }

    /**
     * 判断签名是否正确，必须包含sign字段，否则返回false。使用MD5签名。
     * @param data Map类型数据
     * @param key API密钥
     * @return 签名是否正确
     */
    public static boolean isSignatureValid(Map<String, String> data, String key) {
        return isSignatureValid(data, key, WXPayConstants.SignType.MD5);
    }

    /**
     * 判断签名是否正确，必须包含sign字段，否则返回false。
     * @param data Map类型数据
     * @param key API密钥
     * @param signType 签名方式
     * @return 签名是否正确
     */
    public static boolean isSignatureValid(Map<String, String> data, String key, WXPayConstants.SignType signType) {
        if (!data.containsKey(WXPayConstants.FIELD_SIGN) ) {
            return false;
        }
        String sign = data.get(WXPayConstants.FIELD_SIGN);
        return generateSignature(data, key, signType).equals(sign);
    }

    /**
     * 生成签名
     * @param data 待签名数据
     * @param key API密钥
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String key) {
        return generateSignature(data, key, WXPayConstants.SignType.MD5);
    }

    /**
     * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
     * @param data 待签名数据
     * @param key API密钥
     * @param signType 签名方式
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String key, WXPayConstants.SignType signType) {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(WXPayConstants.FIELD_SIGN)) {
                continue;
            }
            if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("key=").append(key);
        if (WXPayConstants.SignType.MD5.equals(signType)) {
            return MD5(sb.toString()).toUpperCase();
        } else if (WXPayConstants.SignType.HMACSHA256.equals(signType)) {
            return HMACSHA256(sb.toString(), key);
        } else {
        	getLogger().error(String.format("Invalid sign_type: %s", signType));
        	return "";
        }
    }


    /**
     * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     */
    public static String createSign(SortedMap<String, String> packageParams,String key) {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)
                    && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + key);
        String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8")
                .toUpperCase();
        return sign;

    }


    /**
     * 获取随机字符串 Nonce Str
     * @return String 随机字符串
     */
    public static String generateNonceStr() {
        char[] nonceChars = new char[32];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }


    /**
     * 生成 MD5
     * @param data 待处理数据
     * @return MD5结果
     */
    public static String MD5(String data) {
    	if(data == null){
    		return "";
    	}
    	StringBuilder sb = new StringBuilder();
    	try{
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] array = md.digest(data.getBytes("UTF-8"));
	        for (byte item : array) {
	            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
	        }
	        return sb.toString().toUpperCase();
    	}catch(Exception e){
        	return "";
        }
    }

    /**
     * 生成 HMACSHA256
     * @param data 待处理数据
     * @param key 密钥
     * @return 加密结果
     */
    public static String HMACSHA256(String data, String key) {
    	if(data == null || key == null || key.length() == 0){
    		return "";
    	}
    	StringBuilder sb = new StringBuilder();
    	try{
	        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
	        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
	        sha256_HMAC.init(secret_key);
	        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
	        
	        for (byte item : array) {
	            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
	        }
	        return sb.toString().toUpperCase();
    	}catch(Exception e){
    		return "";
    	}
    }

    /**
     * 日志
     * @return Logger
     */
    public static Logger getLogger() {
        Logger logger = LoggerFactory.getLogger("wxpay java sdk");
        return logger;
    }

    /**
     * 获取当前时间戳，单位秒
     * @return long
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis()/1000;
    }

    /**
     * 获取当前时间戳，单位毫秒
     * @return long
     */
    public static long getCurrentTimestampMs() {
        return System.currentTimeMillis();
    }

    public static String getPaymentChannel(String tradeType){
    	return WXPayConstants.TRADE_TYPE_NATIVE.equals(tradeType) ? "1" : 
    		WXPayConstants.TRADE_TYPE_APP.equals(tradeType) ? "2" :
    		WXPayConstants.TRADE_TYPE_JSAPI.equals(tradeType) ? "4" : "3";
    }


    /**
     * xml转json
     * @param xml
     * @return
     */
    public static JSONObject xml2Json(String xml){
        XMLSerializer xmlSerializer = new XMLSerializer();
        //将xml转为json（注：如果是元素的属性，会在json里的key前加一个@标识）
        String  json =  xmlSerializer.read(xml).toString();
        return JSONObject.fromObject(json);
    }

    /**
     * json转xml
     * @param json
     * @return
     * @throws DocumentException
     */
    public static String json2Xml(JSONObject json) throws DocumentException {
        String sXml = "";
        XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setTypeHintsEnabled(false);
        xmlSerializer.setRootName("xml");
        String sContent = xmlSerializer.write(json,"utf-8");
        try {
            Document docCon = DocumentHelper.parseText(sContent);
            sXml = docCon.getRootElement().asXML();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return sXml;
    }

    public static InputStream getCertStream() {
        String certPath = "/Server/cer/wx/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = null;
        ByteArrayInputStream certBis = null;
        if(!file.exists()){
            return null;
        }
        try {
            certStream = new FileInputStream(file);
            byte[] certData = new byte[(int) file.length()];
            certStream.read(certData);
            certBis = new ByteArrayInputStream(certData);
            certStream.close();
        } catch (FileNotFoundException e) {
            log.error("加载微信证书异常1:", e);
        } catch (IOException e) {
            log.error("加载微信证书异常2:", e);
        } finally{
            try {
                if(certStream != null){
                    certStream.close();
                }
                if(certBis != null){
                    certBis.close();
                }
            } catch (IOException e) {
                log.error("加载微信证书异常3:", e);
            }
        }
        return certBis;
    }

}
