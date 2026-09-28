package com.hadaka_electro.customer;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

public class MailUtility {

    public static JavaMailSenderImpl prepareMailSender(Map<String, String> mailSettings) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(mailSettings.get("MAIL_HOST"));
        javaMailSender.setPort(Integer.parseInt(mailSettings.get("MAIL_PORT")));
        javaMailSender.setUsername(mailSettings.get("MAIL_USERNAME"));
        javaMailSender.setPassword(mailSettings.get("MAIL_PASSWORD"));

        Properties mailProperties = new Properties();

        mailProperties.setProperty("mail.smtp.auth", mailSettings.get("MAIL_SMTP_AUTH"));
        mailProperties.setProperty("mail.smtp.starttls.enable", mailSettings.get("MAIL_SMTP_SECURED"));

        javaMailSender.setJavaMailProperties(mailProperties);

        return javaMailSender;
    }

}
