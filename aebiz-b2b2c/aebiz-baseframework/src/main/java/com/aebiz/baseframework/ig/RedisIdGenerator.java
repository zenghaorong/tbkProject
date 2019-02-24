package com.aebiz.baseframework.ig;

import com.aebiz.baseframework.redis.RedisService;
import com.aebiz.commons.utils.DateUtil;
import org.nutz.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.List;

@Component
public class RedisIdGenerator implements IdGenerator {

    @Autowired
    protected RedisService redisService;

    public RedisIdGenerator() {
    }

    public String next(String tableName, String prefix) {
        String key = prefix.toUpperCase();
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        try (Jedis jedis = redisService.jedis()) {
            String ym = DateUtil.format(new Date(), "yyyyMM");
            String id = String.valueOf(jedis.incr("aebiz-ig:" + tableName.toUpperCase() + ym));
            return key + ym + Strings.alignRight(id, 10, '0');
        }
    }

    public Object run(List<Object> fetchParam) {
        return next((String) fetchParam.get(0), (String) fetchParam.get(1));
    }

    public String fetchSelf() {
        return "ig";
    }

}
