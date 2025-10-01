package com.learning.blog.service.impl;

import com.learning.blog.model.User;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationEmail(User user) {
        String to = user.getEmail();
        String subject = "Email Verification";
        String body = "Your verification code is: " + user.getVerificationCode() +
                "\nThis code will expire in 15 minutes.";

        sendEmail(to, subject, body);
    }

    public void sendResetPasswordEmail(User user) {
        String to = user.getEmail();
        String subject = "Password Reset Request";
        String body = "Your password reset code is: " + user.getVerificationCode()
                + "\nThis code will expire in 15 minutes.";

        sendEmail(to, subject, body);
    }


    private void sendEmail(String to, String subject, String message) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            mimeMessage.setSubject(subject);
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, to);
            mimeMessage.setText(message);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email");
        }
    }
}
