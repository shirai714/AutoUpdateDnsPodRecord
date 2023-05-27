package com.linhei.autoupdatednsrecord.server.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.linhei.autoupdatednsrecord.entity.Records;
import com.linhei.autoupdatednsrecord.server.DnsPodServer;
import com.linhei.autoupdatednsrecord.server.MailServer;
import com.linhei.autoupdatednsrecord.utils.RedisUtil;
import com.linhei.autoupdatednsrecord.utils.Utils;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
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
    @Value("${mailConfig.redisConfig.lastIpaddrKey:'last_ip_addr'}")
    private String lastIpaddrKey;

    @Value("${mailConfig.redisConfig.lastSizeKey:'last_size'}")
    private String lastSizeKey;

    @Value("${spring.config.import[0]:}")
    private String path;


    /**
     * 收件箱持续连接
     * File 打开文件
     * D监控提醒
     * 纪录列表
     * 上次更改的ip
     * redis配置状态
     * 上次执行是否未完成
     */
    Folder inbox = null;
    File file = new File("./external.yml");

    private static final String ERROR = "【D监控】网站故障提醒";
    private String recordListJson;
    private String lastIpaddr;
    private boolean redisStatus;
    private boolean isTaskRunning;

    @PostConstruct
    private void getRedisStatus() {
        redisStatus = redisUtil.isRedisConfigured();
        log.info(String.valueOf(redisStatus));
        if (redisStatus) {
            if (lastIpaddr == null) lastIpaddr = String.valueOf(redisUtil.get(lastIpaddrKey));
            if ("-1".equals(lastSize)) lastSize = String.valueOf(redisUtil.get(lastSizeKey));
        }
    }

    @Override
    @Scheduled(fixedDelay = 5000)
    public void receiveEmails() throws MessagingException, IOException {
        if (isTaskRunning) return;
        isTaskRunning = true;

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
        isTaskRunning = false;
    }

    private void editLastSize(int n) throws IOException {
        lastSize = String.valueOf(n);
        if (file == null) file = new File("./" + path.split(":")[1]);
        // 当redis正常配置时将信息写入redis中备份
        if (redisStatus) {
            redisUtil.set(lastIpaddrKey, lastIpaddr);
            redisUtil.set(lastSizeKey, lastSize);
        }
        util.updateConfigProperty("mailConfig.lastSize", lastSize, file);
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


}
