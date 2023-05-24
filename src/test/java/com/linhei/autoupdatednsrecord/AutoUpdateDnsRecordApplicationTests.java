package com.linhei.autoupdatednsrecord;

import com.alibaba.fastjson2.JSONObject;

import com.linhei.autoupdatednsrecord.entity.CreateRecord;
import com.linhei.autoupdatednsrecord.entity.RecordConfig;
import com.linhei.autoupdatednsrecord.entity.Records;
import com.linhei.autoupdatednsrecord.server.DnsPodServer;
import com.linhei.autoupdatednsrecord.server.impl.DnsPodServerImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

//@SpringBootTest
@Slf4j
class AutoUpdateDnsRecordApplicationTests {

    @Test
    void contextLoads() {
        DnsPodServer api = new DnsPodServerImpl();
        long start = System.currentTimeMillis();
        File file = new File("records.json");

        CreateRecord test = new CreateRecord("test", RecordConfig.A, "默认", "ip", "1", "60");
        try {

            // 使用删除方法删除刚刚添加的记录
            String recordId = api.createRecord(test);
            // 将查询的结果写入本地文件
            FileUtils.writeStringToFile(file, api.getRecordListJson(), "UTF-8");
            Records tmp = new Records("""
                    {
                    "id": "record id",
                    "sub_domain": "test",
                    "record_type": "A",
                    "record_line": "默认",
                    "record_line_id": "0",
                    "value": "ip",
                    "weight": null,
                    "mx": "0",
                    "ttl": "120",
                    "enabled": "1",
                    "monitor_status": "",
                    "remark": "",
                    "updated_on": "2023-05-18 21:48:43",
                    "domain_id": "domain id"
                    }""");
            tmp.setValue("ip");
            tmp.setId(recordId);
//            System.out.println(tmp);
            log.info(tmp.toString());

            api.modifyRecord(tmp);
            System.out.println(api.removeRecord(recordId));

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    void test() {
        String json = """
                {
                "id": "record id",
                "sub_domain": "test",
                "record_type": "A",
                "record_line": "默认",
                "record_line_id": "0",
                "value": "ip",
                "weight": null,
                "mx": "0",
                "ttl": "120",
                "enabled": "1",
                "monitor_status": "",
                "remark": "",
                "updated_on": "2023-05-18 21:48:43",
                "domain_id": "domain id"
                }""";


        Records test = JSONObject.parseObject(json, Records.class);
        System.out.println(test);
        log.info(test.toJson());


    }

}
