package com.linhei.autoupdatednsrecord.server;

import jakarta.mail.MessagingException;

/**
 * @author linhei
 */
public interface MailServer {

    /**
     * 通过@Scheduled注解实现每隔5秒读取一次邮箱
     */
    void receiveEmails() throws MessagingException;
}
