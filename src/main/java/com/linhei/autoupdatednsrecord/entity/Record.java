package com.linhei.autoupdatednsrecord.entity;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author linhei
 */
@Data
//@JsonIgnoreProperties({"login_token", "format", "lang", "domain", "domain_id"})
public class Record {

    /**
     * loginToken:  登录token
     * format:      返回值类型
     * lang:        返回语言
     * domain:      域名
     * domainId:    域名id
     * subDomain:   子域      例:www
     * recordType:  记录类型   例:A
     * recordLine:  线路类型   例:默认
     * keyword:     搜索关键字
     * length:      最大返回长度
     * recordLineId:记录线路ID
     */
    String loginToken;
    String format;
    String lang;
    String domain;
    Long domainId;
    String subDomain;
    String recordType;
    String recordLine;
    String keyword;
    Integer length;
    Integer recordLineId;

    public Record() {
    }

    public Record(String loginToken, String format, String lang, String domain) {
        this.loginToken = loginToken;
        this.format = format;
        this.lang = lang;
        this.domain = domain;
    }

    public Record(String loginToken, String format, String lang, Long domainId) {
        this.loginToken = loginToken;
        this.format = format;
        this.lang = lang;
        this.domainId = domainId;
    }

    public Record(String loginToken, String format, String lang, String domain, String keyword, Integer length) {
        this.loginToken = loginToken;
        this.format = format;
        this.lang = lang;
        this.domain = domain;
        this.keyword = keyword;
        this.length = length;
    }

    public Record(String loginToken, String format, String lang, Long domainId, String keyword, Integer length) {
        this.loginToken = loginToken;
        this.format = format;
        this.lang = lang;
        this.domainId = domainId;
        this.keyword = keyword;
        this.length = length;
    }

    public CreateRecord convertToCreateRecord(String subDomain, String recordType, String recordLine, String value, String mx, String ttl) {
        CreateRecord createRecord = new CreateRecord(subDomain, recordType, recordLine, value, mx, ttl);
        createRecord.setFormat(this.format);
        createRecord.setLang(this.lang);
        if (this.domain != null) createRecord.setDomain(this.domain);
        else createRecord.setDomainId(this.domainId);
        return createRecord;
    }

    public Records convertToRecords(String name, String type, String ttl, String value, String mx) {
        Records recordList = new Records(name, type, ttl, value, mx);
        recordList.setDomainId(this.domainId);
        recordList.setLang(this.lang);
        if (this.domain != null) recordList.setDomain(this.domain);
        else recordList.setDomainId(this.domainId);
        return recordList;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"login_token\":\"").append(loginToken).append("\", \"format\":\"").append(format).append("\", \"lang\":\"").append(lang);
        if (domainId != null) sb.append("\", \"domain_id\":\"").append(domainId);
        if (domain != null) sb.append("\", \"domain\":\"").append(domain);
        if (length != null) sb.append("\",\"keyword\":\"").append(length);
        if (subDomain != null) sb.append("\",\"sub_domain\":\"").append(subDomain);
        if (recordType != null) sb.append("\",\"record_type\":\"").append(recordType);
        if (recordLine != null) sb.append("\",\"record_line\":\"").append(recordLine);
        if (recordLineId != null) sb.append("\",\"record_line_id\":\"").append(recordLineId);
        if (keyword != null) sb.append("\",\"keyword\":\"").append(keyword);
        sb.append("\"}");
        return sb.toString();
    }

}
