package com.linhei.autoupdatednsrecord.listener;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


/**
 * @author linhei
 */
@Component
public class EmailListener implements MessageListener {


    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            MimeMessage mimeMessage = (MimeMessage) message;
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            String subject = mimeMessage.getSubject();
//            String from = helper.getFrom()[0].toString();
//            String to = helper.getTo()[0].toString();
            System.out.println(subject);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
