package com.beetech.finalproject.domain.service.other;

import com.beetech.finalproject.web.dtos.email.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailDetailsService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    public String googleMail;

    @Async
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

    @Async
    public void sendMultipleMailWithFormHTML(EmailDetails emailDetails, String sku, String productName, Double price) {
        // Try block to check for exception
        try{
            // Creating a mime message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper;
            // Create mime message helper to send mail
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(googleMail);
            mimeMessageHelper.setTo(emailDetails.getRecipients());

            // Create a Thymeleaf context
            Context context = new Context();
            context.setVariable("sku", sku);
            context.setVariable("productName", productName);
            context.setVariable("price", price);

            // Process the Thymeleaf template to generate HTML content
            String htmlContent = templateEngine.process("email-template", context);
            emailDetails.setMsgBody(htmlContent);

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
