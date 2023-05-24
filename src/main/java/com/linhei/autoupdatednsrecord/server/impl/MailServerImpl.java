package com.linhei.autoupdatednsrecord.server.impl;

import com.linhei.autoupdatednsrecord.server.MailServer;
import jakarta.mail.*;
import org.jetbrains.annotations.TestOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * @author linhei
 */
@Component
public class MailServerImpl implements MailServer {

    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    private Integer lastSize = -1;

    /**
     * 配置邮件服务器连接信息
     */
/*    Store store = null;
    private String host;
    private String user;
    private String password;*/
    Folder inbox = null;

    @Override
    @Scheduled(fixedDelay = 5000)
    public void receiveEmails() throws MessagingException {
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
        if (n > 0 && n > lastSize) {
            Message latestMessage = messages[n - 1];
            System.out.println(latestMessage.getSubject());
            lastSize = n;
        }
        inbox.close();
//        store.close();
    }
}
