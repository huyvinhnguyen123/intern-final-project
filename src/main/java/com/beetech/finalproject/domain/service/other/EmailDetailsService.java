package com.beetech.finalproject.domain.service.other;

import com.beetech.finalproject.web.dtos.email.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailDetailsService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    public String googleMail;

    public void sendSingleMailWithFormHTML(EmailDetails emailDetails) {
        // Creating a mime message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        // Try block to check for exception
        try{
            // Create mime message helper to send mail
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(googleMail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setText(emailDetails.getMsgBody(), true); // when set true this mean you can custom this text by html
            mimeMessageHelper.setSubject(emailDetails.getSubject());
            javaMailSender.send(mimeMessage);
            log.info("Send mail SUCCESS!");

        }catch(MessagingException e){
            log.error("Send mail FAIL!", e);
            log.trace("Make sure that your mail body is input and other mail property it's too!");
        }
    }

    public void sendMultipleMailWithFormHTML(EmailDetails emailDetails) {
        // Creating a mime message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        // Try block to check for exception
        try{
            // Create mime message helper to send mail
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(googleMail);
            mimeMessageHelper.setTo(emailDetails.getRecipients());
            mimeMessageHelper.setText(emailDetails.getMsgBody(), true); // when set true this mean you can custom this text by html
            mimeMessageHelper.setSubject(emailDetails.getSubject());
            javaMailSender.send(mimeMessage);
            log.info("Send mail SUCCESS!");

        }catch(MessagingException e){
            log.error("Send mail FAIL!", e);
            log.trace("Make sure that your mail body is input and other mail property it's too!");
        }
    }
}
