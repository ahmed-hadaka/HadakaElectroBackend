package com.hadaka_electro.customer;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

public class Utility {

    public static JavaMailSenderImpl prepareMailSender(Map<String, String> mailSittings) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(mailSittings.get("MAIL_HOST"));
        javaMailSender.setPort(Integer.parseInt(mailSittings.get("MAIL_PORT")));
        javaMailSender.setUsername(mailSittings.get("MAIL_USERNAME"));
        javaMailSender.setPassword(mailSittings.get("MAIL_PASSWORD"));

        Properties mailProperties = new Properties();

        mailProperties.setProperty("mail.smtp.auth", mailSittings.get("MAIL_SMTP_AUTH"));
        mailProperties.setProperty("mail.smtp.starttls.enable", mailSittings.get("MAIL_SMTP_SECURED"));

        javaMailSender.setJavaMailProperties(mailProperties);

        return javaMailSender;
    }

}
