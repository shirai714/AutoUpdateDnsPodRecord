package com.linhei.autoupdatednsrecord.server.impl;

import com.alibaba.fastjson2.JSONObject;
import com.linhei.autoupdatednsrecord.entity.Records;
import com.linhei.autoupdatednsrecord.server.DnsPodServer;
import com.linhei.autoupdatednsrecord.server.MailServer;
import jakarta.mail.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author linhei
 */
@Component
@EnableConfigurationProperties
@Slf4j
public class MailServerImpl implements MailServer {

    @Autowired
    private MailProperties mailProperties;
    @Autowired
    private DnsPodServer dnsPodServer;

    @Value("${mail_server.last_size:-1}")
    private String lastSize;
    /**
     * 收件箱持续连接
     * File 打开文件
     * Map用于处理子字段
     * D监控提醒
     */
    Folder inbox = null;
    File file = new File("./external.yml");
    private Map<String, Object> valueMap;
    private static final String ERROR = "【D监控】网站故障提醒";
    private String recordListJson;
    private String lastIpaddr;

    @Override
    @Scheduled(fixedDelay = 5000)
    public void receiveEmails() throws MessagingException, IOException {
        if (inbox == null) {
            Properties properties = new Properties();
            properties.setProperty("mail.store.protocol", mailProperties.getProtocol());
            properties.setProperty("mail.pop3s.host", mailProperties.getHost());
            properties.setProperty("mail.pop3s.port", String.valueOf(mailProperties.getPort()));
            properties.setProperty("mail.pop3s.username", mailProperties.getUsername());
            properties.setProperty("mail.pop3s.password", mailProperties.getPassword());
            mailProperties.getProperties().forEach(properties::setProperty);
            Store store = Session.getDefaultInstance(properties).getStore();
            store.connect(mailProperties.getHost(), mailProperties.getUsername(), mailProperties.getPassword());
            inbox = store.getFolder("INBOX");
            store.close();
        }


        inbox.open(Folder.READ_ONLY);
        Message[] messages = inbox.getMessages();
        int n = messages.length;
        if (n > 0 && n > Integer.parseInt(lastSize)) {
            if (messages[n - 1].getSubject().contains(ERROR)) {
                if (recordListJson == null) recordListJson = dnsPodServer.getRecordListJson();
                if (this.lastIpaddr == null) lastIpaddr = getLastIpAddr();
                String publicIp = dnsPodServer.getPublicIp();
                recordListJson = recordListJson.replaceAll(lastIpaddr, publicIp);
                // 记录日志
                log.info(dnsPodServer.modifyRecord(recordListJson));
                // 更新上次的IP
                lastIpaddr = publicIp;
            }
            // 修改上次读取邮箱时的邮箱大小
            lastSize = String.valueOf(n);
            updateConfigProperty("mail_server.last_size", lastSize);
        }
        inbox.close();
    }

    private String getLastIpAddr() throws IOException {
        return new Records(JSONObject.parseObject(dnsPodServer.getRecordJson("@", "A", "默认"))
                .get("records").toString())
                .getValue();
    }

    /**
     * 更新yml配置文件
     *
     * @param key   key
     * @param value value
     */
    @Override
    public void updateConfigProperty(String key, String value) throws IOException {
        String yamlContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        // 解析 YAML 内容为 Map 对象
        Yaml yaml = new Yaml();
        Map<String, Object> yamlMap = yaml.load(yamlContent);

        // 更新指定键的值
        String[] split = key.split("\\.");
        String mapKey = split[0];
        if (this.valueMap == null)
            valueMap = (Map<String, Object>) yamlMap.getOrDefault(mapKey, new HashMap<>(1));
        valueMap.put(split[1], value);
        yamlMap.put(split[0], valueMap);

        // 转换为更新后的 YAML 字符串
        String updatedYamlContent = yaml.dump(yamlMap);

        FileUtils.writeStringToFile(file, updatedYamlContent, StandardCharsets.UTF_8);
    }
}
