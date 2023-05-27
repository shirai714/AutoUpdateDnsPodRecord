package com.linhei.autoupdatednsrecord.server.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.linhei.autoupdatednsrecord.entity.Records;
import com.linhei.autoupdatednsrecord.server.DnsPodServer;
import com.linhei.autoupdatednsrecord.server.MailServer;
import com.linhei.autoupdatednsrecord.utils.RedisUtil;
import com.linhei.autoupdatednsrecord.utils.Utils;
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
import java.util.LinkedHashMap;
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
    @Autowired
    private RedisUtil redisUtil;

    private final Utils util = new Utils();

    @Value("${mailConfig.lastSize:-1}")
    private String lastSize;
    @Value("${mailConfig.redisConfig.lastIpaddrKey:}")
    private String lastIpaddrKey;

    @Value("${spring.config.import[0]:}")
    private String path;
    /**
     * 收件箱持续连接
     * File 打开文件
     * D监控提醒
     * 纪录列表
     * 上次更改的ip
     */
    Folder inbox = null;
    //    File file = new File("./external.yml");
    File file;
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
            editLastSize(n);
        } else if (n < Integer.parseInt(lastSize)) editLastSize(n);

        inbox.close();
    }

    private void editLastSize(int n) throws IOException {
        lastSize = String.valueOf(n);
        updateConfigProperty("mailConfig.lastSize", lastSize);
    }

    private String getLastIpAddr() throws IOException {
        String json = dnsPodServer.getRecordJson("@", "A", "默认");
        JSONArray jsonArray = JSON.parseArray(json);
        String ip = "";
        for (Object o : jsonArray) {
            ip = new Records(JSONObject.parseObject(String.valueOf(o)).toString()).getValue();
            if (util.isValidIpv4Address(ip)) break;
        }
        return ip;
    }

    /**
     * 更新yml配置文件
     *
     * @param key   key
     * @param value value
     */
    @Override
    public void updateConfigProperty(String key, String value) throws IOException {
        if (file == null) file = new File("./" + path.split(":")[1]);
        String yamlContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        // 解析 YAML 内容为 Map 对象
        Yaml yaml = new Yaml();
        Map<String, Object> yamlMap = yaml.load(yamlContent);

        // 递归修改指定键的值
        updatePropertyValue(yamlMap, key, value);

        // 转换为更新后的 YAML 字符串
        String updatedYamlContent = yaml.dump(yamlMap);

        FileUtils.writeStringToFile(file, updatedYamlContent, StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    private void updatePropertyValue(Map<String, Object> yamlMap, String key, String value) {
        String[] split = key.split("\\.");
        String mapKey = split[0];
        if (split.length == 1)
            // 最后一层键，直接修改值或新建键值对
            yamlMap.put(mapKey, value);
        else
            // 非最后一层键，递归调用更新子层或新建子层
            if (yamlMap.containsKey(mapKey)) {
                Object childObject = yamlMap.get(mapKey);
                if (childObject instanceof Map) {
                    Map<String, Object> childMap = (Map<String, Object>) childObject;
                    updatePropertyValue(childMap, key.substring(key.indexOf('.') + 1), value);
                }
            } else {
                Map<String, Object> childMap = new LinkedHashMap<>();
                yamlMap.put(mapKey, childMap);
                updatePropertyValue(childMap, key.substring(key.indexOf('.') + 1), value);
            }

    }
}
