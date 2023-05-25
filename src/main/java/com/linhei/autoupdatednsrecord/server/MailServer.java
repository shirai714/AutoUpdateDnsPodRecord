package com.linhei.autoupdatednsrecord.server;

import jakarta.mail.MessagingException;

import java.io.IOException;

/**
 * @author linhei
 */
public interface MailServer {

    /**
     * 通过@Scheduled注解实现每隔5秒读取一次邮箱
     *
     * @throws MessagingException 消息异常
     * @throws IOException        IO异常
     */
    void receiveEmails() throws MessagingException, IOException;


    /**
     * 更新yml配置文件
     *
     * @param key   key
     * @param value value
     * @throws IOException io异常
     */
    void updateConfigProperty(String key, String value) throws IOException;
}
