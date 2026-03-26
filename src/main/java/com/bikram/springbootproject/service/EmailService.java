package com.bikram.springbootproject.service;
import com.bikram.springbootproject.dto.EmailPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void mailSend(EmailPayload emailPayload){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailPayload.getTo());
            message.setSubject(emailPayload.getSubject());
            message.setText(emailPayload.getBody());
            mailSender.send(message);
            log.info("emailPayLoad = {}",emailPayload.getTo());
        } catch (Exception e) {
            log.error("email failed ",e);
        }

    }
}
