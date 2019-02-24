package com.aebiz.baseframework.base;

import com.aebiz.commons.utils.SpringUtil;
import com.google.common.collect.Maps;
import org.nutz.lang.Strings;
import org.nutz.mvc.impl.NutMessageLoader;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;


/**
 * 国际化字符串
 * Created by wizzer on 2016/12/27.
 */
public class Message {
    private static Map<Locale, Properties> messageMap = Maps.newConcurrentMap();

    private String basePath;

    public Message(String basePath) {
        this.basePath = basePath;
    }

    public static String getLanguage(Locale locale) {
        return locale.toLanguageTag();
    }

    public static boolean isExist(Locale locale) {
        return messageMap.get(locale) != null;
    }

    public static Properties getMessage(Locale locale) {
        return messageMap.get(locale);
    }

    public static String getMessage(String key) {
        if(SpringUtil.getRequest()==null)
            return key;
        Properties properties = messageMap.get(RequestContextUtils.getLocale(SpringUtil.getRequest()));
        if (properties == null) {
            return key;
        }
        String value = properties.getProperty(key);
        if (!Strings.isBlank(value)) {
            return value;
        }
        return key;
    }

    @PostConstruct
    public void init() throws IOException {
        NutMessageLoader loader = new NutMessageLoader();
        Map<String, Map<String, Object>> loadMap = loader.load(basePath);
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            Map<String, Object> m = loadMap.get(locale.toString());
            if (m != null) {
                Properties properties = new Properties();
                for (Map.Entry<String, Object> e : m.entrySet()) {
                    properties.put(e.getKey(), e.getValue());
                }
                messageMap.put(locale, properties);
            }
        }
    }
}
