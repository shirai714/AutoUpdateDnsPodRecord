package com.linhei.autoupdatednsrecord.server.impl;

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

    @Value("${mail_server.last_size}")
    private String lastSize = "-1";
    /**
     * 收件箱持续连接
     * File 打开文件
     */
    Folder inbox = null;
    File file = new File("./external.yml");

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
/*            this.store = Session.getDefaultInstance(properties).getStore();
            this.host = mailProperties.getHost();
            this.user = mailProperties.getUsername();
            this.password = mailProperties.getPassword();
            store.connect(this.host, this.user, this.password);*/
            Store store = Session.getDefaultInstance(properties).getStore();
            store.connect(mailProperties.getHost(), mailProperties.getUsername(), mailProperties.getPassword());
            inbox = store.getFolder("INBOX");
            store.close();
        }


        inbox.open(Folder.READ_ONLY);
        Message[] messages = inbox.getMessages();
        int n = messages.length;
        if (n > 0 && n > Integer.parseInt(lastSize)) {
            Message latestMessage = messages[n - 1];
            log.info(latestMessage.getSubject());
            lastSize = String.valueOf(n);
            log.info(lastSize);
            updateConfigProperty("mail_server.last_size", String.valueOf(lastSize));
        }
        inbox.close();
//        store.close();
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
        yamlMap.put("mail_server.last_size", "1");

        // 转换为更新后的 YAML 字符串
        String updatedYamlContent = yaml.dump(yamlMap);

        FileUtils.writeStringToFile(file, updatedYamlContent, StandardCharsets.UTF_8);
    }
}
