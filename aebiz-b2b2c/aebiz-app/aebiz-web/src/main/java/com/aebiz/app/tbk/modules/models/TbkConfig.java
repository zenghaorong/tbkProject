package com.aebiz.app.tbk.modules.models;

/**
 * @Auther: zenghaorong
 * @Date: 2019/7/14  12:23
 * @Description: api接口调用配置文件
 */
public class TbkConfig {

    // TOP服务地址，正式环境需要设置为http://gw.api.taobao.com/router/rest
    public static final String serverUrl = "http://gw.api.taobao.com/router/rest";


    //-------------------------我的淘宝账号----------------------------//
//    public static final String appKey = "27666005"; // 可替换为您的沙箱环境应用的AppKey
//    public static final String appSecret = "3957722729962a767d5e69afffbb148d"; // 可替换为您的沙箱环境应用的AppSecret
//    public static final String sessionKey = "6101719197ebae75a13eb3db2d91f11f939b4f59a4d3cac74948387"; // 必须替换为沙箱账号授权得到的真实有效SessionKey
//
//    //淘宝客mm_xxx_xxx_12345678三段式的最后一段数字,推广位编号
//    public static final Long adzone_Id = 109108750020L;
//
//    //推广位编号
//    public static final String pid = "mm_102201089_588850317_109108750020";


    //-------------------------公共淘宝账号----------------------------//
    public static final String appKey = "27864021"; // 可替换为您的沙箱环境应用的AppKey
    public static final String appSecret = "a545399b38e2f46b76bcac3512219414"; // 可替换为您的沙箱环境应用的AppSecret
    public static final String sessionKey = null; // 必须替换为沙箱账号授权得到的真实有效SessionKey

    //淘宝客mm_xxx_xxx_12345678三段式的最后一段数字,推广位编号
    public static final Long adzone_Id = 109516700295L;

    //推广位编号
    public static final String pid = "mm_516040192_818900268_109516700295";

    //渠道关系编号
    public static final  String relation_id = "2367114130";

    /***
     * 方法一
     * 使用渠道编号
     * https://pub.alimama.com/manage/promotion/proxy.htm?spm=a219t.11816991.1998910419.d0136d927.42a575a5Ko6aDx&mode=rebate&type=3
     *  自己的商品链接 后面更上渠道编号  productUrl + "&relation_id="+"2367114130"
     *  如：
     */

    /***
     * 方法二
     * 进入这个页面 建立不同渠道的推广位 即可通过推广位区分订单来源 实现分销
     * https://pub.alimama.com/manage/promotion/proxy.htm?spm=a219t.11816991.1998910419.d0136d927.42a575a5Ko6aDx&mode=rebate&type=3
     */

}
