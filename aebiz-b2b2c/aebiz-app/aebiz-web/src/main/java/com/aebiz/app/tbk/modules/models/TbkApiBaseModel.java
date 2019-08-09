package com.aebiz.app.tbk.modules.models;


import com.taobao.api.ApiRuleException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.internal.util.RequestCheckUtils;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.request.TbkItemGetRequest;
import com.taobao.api.response.TbkItemGetResponse;

import java.util.Map;

/**
 * @Auther: zenghaorong
 * @Date: 2019/7/14  9:58
 * @Description:
 */
public  class TbkApiBaseModel extends BaseTaobaoRequest<TbkItemGetResponse> {

    private String method;
    private String cat;
    private Long endPrice;
    private Long endTkRate;
    private String fields;
    private Boolean isOverseas;
    private Boolean isTmall;
    private String itemloc;
    private Long pageNo;
    private Long pageSize;
    private Long platform;
    private String q;
    private String sort;
    private Long startPrice;
    private Long startTkRate;

    //猜你喜欢
    private String adzoneId;
    private String userNick;
    private String userId;
    private String os;
    private String idfa;
    private String imei;
    private String imeiMd5;
    private String ip;
    private String ua;
    private String apnm;
    private String net;
    private String mn;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public Long getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(Long endPrice) {
        this.endPrice = endPrice;
    }

    public Long getEndTkRate() {
        return endTkRate;
    }

    public void setEndTkRate(Long endTkRate) {
        this.endTkRate = endTkRate;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public Boolean getOverseas() {
        return isOverseas;
    }

    public void setOverseas(Boolean overseas) {
        isOverseas = overseas;
    }

    public Boolean getTmall() {
        return isTmall;
    }

    public void setTmall(Boolean tmall) {
        isTmall = tmall;
    }

    public String getItemloc() {
        return itemloc;
    }

    public void setItemloc(String itemloc) {
        this.itemloc = itemloc;
    }

    public Long getPageNo() {
        return pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getPlatform() {
        return platform;
    }

    public void setPlatform(Long platform) {
        this.platform = platform;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Long getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Long startPrice) {
        this.startPrice = startPrice;
    }

    public Long getStartTkRate() {
        return startTkRate;
    }

    public void setStartTkRate(Long startTkRate) {
        this.startTkRate = startTkRate;
    }

    public String getAdzoneId() {
        return adzoneId;
    }

    public String getUserNick() {
        return userNick;
    }

    public String getUserId() {
        return userId;
    }

    public String getOs() {
        return os;
    }

    public String getIdfa() {
        return idfa;
    }

    public String getImei() {
        return imei;
    }

    public String getImeiMd5() {
        return imeiMd5;
    }

    public String getIp() {
        return ip;
    }

    public String getUa() {
        return ua;
    }

    public String getApnm() {
        return apnm;
    }

    public String getNet() {
        return net;
    }

    public String getMn() {
        return mn;
    }

    public void setAdzoneId(String adzoneId) {
        this.adzoneId = adzoneId;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setImeiMd5(String imeiMd5) {
        this.imeiMd5 = imeiMd5;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public void setApnm(String apnm) {
        this.apnm = apnm;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public void setMn(String mn) {
        this.mn = mn;
    }

    public Map<String, String> getTextParams() {
        TaobaoHashMap txtParams = new TaobaoHashMap();
        txtParams.put("method", this.method);
        txtParams.put("cat", this.cat);
        txtParams.put("end_price", this.endPrice);
        txtParams.put("end_tk_rate", this.endTkRate);
        txtParams.put("fields", this.fields);
        txtParams.put("is_overseas", this.isOverseas);
        txtParams.put("is_tmall", this.isTmall);
        txtParams.put("itemloc", this.itemloc);
        txtParams.put("page_no", this.pageNo);
        txtParams.put("page_size", this.pageSize);
        txtParams.put("platform", this.platform);
        txtParams.put("q", this.q);
        txtParams.put("sort", this.sort);
        txtParams.put("start_price", this.startPrice);
        txtParams.put("start_tk_rate", this.startTkRate);

        txtParams.put("adzone_id", this.adzoneId);
        txtParams.put("user_nick", this.userNick);
        txtParams.put("user_id", this.userId);
        txtParams.put("os", this.os);
        txtParams.put("idfa", this.idfa);
        txtParams.put("imei", this.imei);
        txtParams.put("imei_md5", this.imeiMd5);
        txtParams.put("ip", this.ip);
        txtParams.put("ua", this.ua);
        txtParams.put("apnm", this.apnm);
        txtParams.put("net", this.net);
        txtParams.put("mn", this.mn);
        txtParams.put("sort", this.sort);
        txtParams.put("start_price", this.startPrice);
        txtParams.put("start_tk_rate", this.startTkRate);

        if (this.udfParams != null) {
            txtParams.putAll(this.udfParams);
        }

        return txtParams;
    }

    public Class<TbkItemGetResponse> getResponseClass() {
        return TbkItemGetResponse.class;
    }

    public void check() throws ApiRuleException {
        RequestCheckUtils.checkNotEmpty(this.fields, "fields");
    }
}
