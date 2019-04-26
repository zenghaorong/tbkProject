package com.aebiz.app.alipay.modules.models;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/19  10:20
 * @Description: 支付宝配置类
 */
public class AlipayConfig {


    /***********************************测试环境使用信息************************************************/
    // 商户appid
    public static String APPID = "2019042264282233";
    // 私钥 pkcs8格式的
    public static String RSA_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDGy3yapFQYDRk5gyaAXt6eFE1nrFAiHRLHd59mWa/jFg18HhP9fCkxSrzGXiDH4/ELNqWfUWqg0Xs73U65/WAMqgvNftzn+AfdxTjq7pEqEXcT2b70FYAn/DXB1FTf4e2CeJcA/3GHpAeKFtmw+fIhMzbvDu0qn1BIzJ4H+fr+xBFtQrvXA3liOv2YqCFu8ipsy5GDd8VyC3EMQVdbfAhvMdx1qqtYp/NpsEaCqxcFv0Gou60MyRKz8CdQvzpxcMpvhX4IqnYpEYNod0ZE0FrZScQ+X3vcKXpP/4tF/+sYCAccPRReYtx8KaGAeByMJRVyHtb5Xd9TJ7XplTKWwrstAgMBAAECggEAJQr96QL9+I1YSfM6VWlYKjS2R79AH1sziJ+twDCuKa6OTLZ+GxnrwxeeHYikqxq6a9B/BDTznYvrXrhaVyXzo+h70or1NvRs4tJBTOWvfxtKCRTmFC19d/XzPm8GccFztIAsAy+WRCQ4k262mde5Wp5keh9Z6jtNhoM6X1ohfx9NfnytXWZTb8CyNNpwLPqq6EMri0vM4Z3g9I6iZQsg3Iy3PzlJs4vmgxwlbeD2Ji9oXaHbuegjZOPCVo/zJOMOTuJ74L3lJlZqAVWd+k9Y/rU9NZGzwidZ6NM/JriideDaeg4IQw7Uw3O44Xy7YkoV791topWlsKguVDJxWTaZsQKBgQD3xMN28Cq9bjXsi20V/HBrvCu2ZN4Nre93fSdqKIz/rn6VgJxT/v6epvQWl7O3I92YmrTaqtGYYF/tE9v3ZbGgU8dZU/3fgqJ/nEjegafFpcPQcur3hqQ9uF+kdbiESRu+0f+BbxTPZv0hC0B9WUqv9AkZ6Wthpa0P+TZvfQY7bwKBgQDNZjVmEEalhWqR+8ai+t/UQydy1wEv+uP93kQgRYRVptdBAUmFc4nCdbQK0BVkmI/vYoC2zOF7OiyLMK5mPwScW58PbHDDK678l3JBhwJG7J8cEZQEdn4xGdKMdrrB0Z7hLMw9zV0l8FpHKWGdCfyE+t6zQFhIDEbD3b0loj+VIwKBgBynMZ+gmKC9jRjk6uGfBvU+lqOKOK/GTKo53AQH2n57FIJ/lBSWqaV5U0MFmi/0wtyyD3Y2SPThOFa3dDnBFfCfn68x6msdtWMilL6+qcNOrz18/LVRac8FcnIXdxY9SisgJ/c0/Ggb1ewrW/LjD6MXirHnb5akTUZKoMP15nGbAoGBAMWWzkWp4Jw4NSXMI/EDD7E9DMTvyiei3hKI79ZvqXdP8YCKQXTMibh0zEcel4XXMZeilZnEjCCj/NfFc+/OLZxQkzoeInfOHR7GfdZFj041MHaq/k4wwwJvCdmwGcZ75bBODQhi93mYtvAAAfQ0+CPgxb2AZ6yfiq+/Lsb2Rt4rAoGAQ7pNlNXYHZKB1y48fRkXe/S9I/RT84m7qAZZ3v8ZMogpC0JsOybfsvor9Lgru+lx4KGnSxpJOrfdOw7o46I5E2h7e3o3q/eDoI6dNiOCNVmTAFffZ52LI7HoztGgitSvIPRrv4FfXkMH3WnZ9vGiZlHkfVWkyfOEIMVt2lNpDg4=";
    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
//    public static String notify_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/notify_url.jsp";
    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
//    public static String return_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/return_url.jsp";
    // 请求网关地址
    public static String URL = "https://openapi.alipay.com/gateway.do";
    // 编码
    public static String CHARSET = "UTF-8";
    // 返回格式
    public static String FORMAT = "json";
    // 支付宝公钥
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlWTmNXHNe+vdLbXifyU7BH0TcQ+vwS7XrW+lpyWw7LP/mzfzPRqj80efKBPHLf7sUgct5a6xjl0GhWSiMmhpVj3NvehhWm/CVleu5/lClIccnmVTDpXPClYmufH7FnSn5o4+1oT360kcRmWaiAoNjkIzwteJ9cIZdQ8Giu+ImIm3aS5cBQWna4V/2HfPfg+U6JjXzEhtQtrJjzyMXQ4hKzJd8z0MoW8iyRiOD56YG/OKmsuyrpSDMRfeEbUYnin9FIj+e0AR8oDcYdhyHh8KU8QQa417rqgCNm/yysY5XQ4HvEd7npMF6Bt2eL+2wCpFfWdy1+esDKHamZsXmgy2lwIDAQAB";
    // 日志记录目录
    public static String log_path = "/log";
    // RSA2
    public static String SIGNTYPE = "RSA2";



    /***********************************正式环境使用信息************************************************/
    // 商户appid
//    public static String APPID = "2019042264300093";
//    // 私钥 pkcs8格式的
//    public static String RSA_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC883aF430k8+LFu//hV3HZvrvw9GCHORRZ6TcB8J1oqGJNh+GlZuhSrnAu1jnOWN+Chup79ALkg0nlWI56YKLzJJaxZAB7fQBzF5neJzFNz8RH4pIYzbezZeZWgqBYLXCctervlHXv39UmppN5KRFBT1BqemKZmI3V3fZKj6c16WL95ifN1mZWH+SZ7ikb5lbnmAO+HnzBoiGnfX7px/p19IzCgfX7s05X0oIQmnUtsJy/1Dnsv2oGPjiWm1IlCZzI7coX5/r3hUAqgX7iqjopXlpW1jDl0T5MjdIKwlGqJbJ5uOA3KFm2cVv4J9xa6F9DrPwbnuZc2yM7+E+lIDcrAgMBAAECggEAZKfNm8V0BRtm0q8zwHc0zyosNE/6nA5sKPlztDTZpccNlv+r1NDdMi561HiKksIFTyNQBngWkmTeGK70N30DUUNMg3MiTqZoduc7sHxwZ2MmzKSF73CnS4odCjjL8i1CsDi4zqfnq8Exa0WRihm2d3QpAo/vtqLt6+AyM8YS7URcKjsjZTJ7XarnPGMmy77IENQxA5NC4FEl6pfn3Fjmi4KlsiNNUvhOCXPRko6rahdGiDLkR6mDXZy0PDiB8C+ugvAtB2o0n3qL+0fMDhmvZO4U0sAcLPloUHPgry1xU8P0MdV4hQCp2z72GdGz4/i0b0VprtNpLvawJ1FHiLnnAQKBgQDoUyaRJV9khgU5FHlRs1Ae9hsNhTyoFSPSL4+qGMirfJ0yJcfw33nUaZmSCGEGHTK+esrDFPMTTdft3TuIJVGI/M2zL5Ha/nNoRizwHJqpM608R/edHvjk9TxhviXYqjM/w4fPY+GfIcyYs4ilbBrz6NaKEWfOtbeSTwlZj2DsvQKBgQDQNMjf3ZHAsn4f2lKPQzysNryJpBWj9VJivaKb4h728vA3u2ZBQFGjNy8ecgxlfzmy3SLzAt9DqWGz5d0ap1zPJNTipJZyf2lIXMs8QIEnYPGw8OpvT3xyJDFmqAUfHv/DvWUU2tXBLkXWj+LtVE/wV1zzLptYZehxBzQSwX+WBwKBgQDln6HD3iNt0MUCyMisRsXPBEJy15i/LL3N9fJSFdLpI+6e7Ra0bb0VhBYkbyGukFdiSy1zKZajItJkKMC5bOEsu2l4THEl+U8J5ipuXGdFC22S4jLL2hv4+KZomS8A+iBTSqhGBBuxVA/Lcc+CaNou75h0uIVYmuTIuFOucHdFJQKBgAfL0jG/xDNo+5wJusmuhNkV+51TzZOkHf+ghjQ8FVi95AzSMZQD6oqoCFWlg51RVEHEUgizdNS9xPDNjKfw2GLRSD9sVk3XQ4P4JYwOBkgJM0Oi4cRlP42umUL0y6xMptsXfm93ekWPjAHqxV6Jp6yiONLKTfrTZg7FbEe/gx2FAoGBAIISNOz4srVvAx/+ze9JaF7g1s+mei/9riaa4qabNbU9KxUBwt6Qjfkt9sraxf54AM5zYA7YLovpmEIJavZ3jw+iJlY3KSq553m1eNJK5KFC4P9afBDS4bc3crvqbMF/c0+p7tX763J1WjOYMiOeDpj73sMSqjhhTXowbKUu8ARC";
//    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
////    public static String notify_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/notify_url.jsp";
//    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
////    public static String return_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/return_url.jsp";
//    // 请求网关地址
//    public static String URL = "https://openapi.alipay.com/gateway.do";
//    // 编码
//    public static String CHARSET = "UTF-8";
//    // 返回格式
//    public static String FORMAT = "json";
//    // 支付宝公钥
//    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlWTmNXHNe+vdLbXifyU7BH0TcQ+vwS7XrW+lpyWw7LP/mzfzPRqj80efKBPHLf7sUgct5a6xjl0GhWSiMmhpVj3NvehhWm/CVleu5/lClIccnmVTDpXPClYmufH7FnSn5o4+1oT360kcRmWaiAoNjkIzwteJ9cIZdQ8Giu+ImIm3aS5cBQWna4V/2HfPfg+U6JjXzEhtQtrJjzyMXQ4hKzJd8z0MoW8iyRiOD56YG/OKmsuyrpSDMRfeEbUYnin9FIj+e0AR8oDcYdhyHh8KU8QQa417rqgCNm/yysY5XQ4HvEd7npMF6Bt2eL+2wCpFfWdy1+esDKHamZsXmgy2lwIDAQAB";
//    // 日志记录目录
//    public static String log_path = "/log";
//    // RSA2
//    public static String SIGNTYPE = "RSA2";



}
