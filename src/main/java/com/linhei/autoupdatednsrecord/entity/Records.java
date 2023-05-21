package com.linhei.autoupdatednsrecord.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author linhei
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Records extends Record {
    /**
     * id:              记录id
     * name:            子域
     * line:            线路类型
     * lineId:          线路类型的id
     * type:            记录类型
     * ttl:             ttl
     * value:           记录值
     * weight:          权重信息
     * mx:              MX优先级
     * enabled:         是否开启
     * status:          状态
     * monitorStatus:   监控状态
     * remark:          标记
     * updatedOn:       最后更新时间
     * useAqb:
     */
    String id;
    String name;
    String line;
    String lineId;
    String type;
    String ttl;
    String value;
    String weight;
    String mx;
    String enabled;
    String status;
    String monitorStatus;
    String remark;
    LocalDateTime updatedOn;
    String useAqb;

    public CreateRecord convertToCreateRecord() {
        return new CreateRecord(this.name, this.type, this.line, this.value, this.mx, this.ttl);
    }

    public Records() {
    }

    public Records(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        this.id = (String) jsonObject.get("id");
        this.name = (String) jsonObject.get("sub_domain");
        this.type = (String) jsonObject.get("record_type");
        this.line = (String) jsonObject.get("record_line");
        this.lineId = (String) jsonObject.get("record_line_id");
        this.value = (String) jsonObject.get("value");
        this.weight = (String) jsonObject.get("weight");
        this.mx = (String) jsonObject.get("mx");
        this.ttl = (String) jsonObject.get("ttl");
        this.enabled = (String) jsonObject.get("enabled");
        this.monitorStatus = (String) jsonObject.get("monitor_status");
        this.remark = (String) jsonObject.get("remark");
        this.updatedOn = LocalDateTime.parse(String.valueOf(jsonObject.get("updated_on")),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.useAqb = (String) jsonObject.get("use_aqb");
    }

    public Records(String name, String type, String ttl, String value, String mx) {
        this.name = name;
        this.type = type;
        this.ttl = ttl;
        this.value = value;
        this.mx = mx;
    }

    public Records(String id, String name, String line, String lineId, String type, String ttl, String value, String weight, String mx, String enabled, String status, String monitorStatus, String remark, LocalDateTime updatedOn, String useAqb) {
        this.id = id;
        this.name = name;
        this.line = line;
        this.lineId = lineId;
        this.type = type;
        this.ttl = ttl;
        this.value = value;
        this.weight = weight;
        this.mx = mx;
        this.enabled = enabled;
        this.status = status;
        this.monitorStatus = monitorStatus;
        this.remark = remark;
        this.updatedOn = updatedOn;
        this.useAqb = useAqb;
    }

    @Override
    public String toJson() {
        return "{\"" + "record_id\":\"" + id + "\",\"" +
                "sub_domain\":\"" + name + "\",\"" +
                "line\":\"" + line + "\",\"" +
                "record_line_id\":\"" + lineId + "\",\"" +
                "record_type\":\"" + type + "\",\"" +
                "ttl\":\"" + ttl + "\",\"" +
                "value\":\"" + value + "\",\"" +
                "mx\":\"" + mx + "\",\"" +
                "status\":\"" + status +
                "\"}";
    }
}
