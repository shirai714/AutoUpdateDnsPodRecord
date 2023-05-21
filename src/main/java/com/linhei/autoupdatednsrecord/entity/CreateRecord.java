package com.linhei.autoupdatednsrecord.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建解析记录
 *
 * @author linhei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CreateRecord extends Record {

    /**
     * subDomain:   子域      例:www
     * recordType:  记录类型   例:A
     * recordLine:  线路类型   例:默认
     * value:       记录值     例:IP:200.200.200.200, CNAME: cname.dnspod.com., MX: mail.dnspod.com.
     * mx:          MX优先级,当记录类型是 MX 时有效，范围1-20, MX记录必选。
     * ttl:         TTL
     */

    String subDomain;
    String recordType;
    String recordLine;
    String value;
    String mx;
    String ttl;

    public Records convertToRecords() {
        return new Records(this.subDomain, this.recordType, this.ttl, this.value, this.mx);
    }

    public CreateRecord() {
    }


    public CreateRecord(String subDomain, String recordType, String recordLine, String value, String mx, String ttl) {
        this.subDomain = subDomain;
        this.recordType = recordType;
        this.recordLine = recordLine;
        this.value = value;
        this.mx = mx;
        this.ttl = ttl;
    }

    @Override
    public String toJson() {
        return "{\"sub_domain\":\"" + subDomain +
                "\",\"record_type\":\"" + recordType +
                "\",\"record_line\":\"" + recordLine +
                "\",\"value\":\"" + value +
                "\",\"mx\":" + mx +
                ",\"ttl\":" + ttl + "}";
    }

}
