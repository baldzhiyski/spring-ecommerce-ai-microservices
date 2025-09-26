package org.baldzhiyski.notification.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender mailSender;


    public void send(@NonNull String to, @NonNull String subject, @NonNull String body) {
        List<String> recipients = Arrays.stream(to.split("[,;]"))
                .map(String::trim).filter(s -> !s.isBlank()).toList();
        if (recipients.isEmpty()) throw new IllegalArgumentException("Recipient email is required");

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("no-reply@notifications.baldzhiyski.org");
        msg.setTo(recipients.toArray(String[]::new));
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}
