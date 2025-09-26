package org.baldzhiyski.notification.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import java.util.Arrays;
import java.util.List;


import jakarta.mail.MessagingException;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.nio.charset.StandardCharsets;


@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;

    /** Send a simple plain-text email. */
    public void send(@NonNull String to, @NonNull String subject, @NonNull String body) {
        List<String> recipients = splitRecipients(to);
        if (recipients.isEmpty()) throw new IllegalArgumentException("Recipient email is required");

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("no-reply@notifications.baldzhiyski.org");
        msg.setTo(recipients.toArray(String[]::new));
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    /** Send an HTML email (use with Thymeleaf-rendered HTML). */
    public void sendHtml(@NonNull String to, @NonNull String subject, @NonNull String htmlBody) {
        sendHtml(to, subject, htmlBody, null, null, null);
    }

    /** HTML with optional cc/bcc/replyTo (all CSV; any can be null/blank). */
    public void sendHtml(@NonNull String to,
                         @NonNull String subject,
                         @NonNull String htmlBody,
                         String cc,
                         String bcc,
                         String replyTo) {
        List<String> toList = splitRecipients(to);
        if (toList.isEmpty()) throw new IllegalArgumentException("Recipient email is required");

        try {
            var mime = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(
                    mime,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom("no-reply@notifications.baldzhiyski.org");
            helper.setTo(toList.toArray(String[]::new));
            if (cc  != null && !cc.isBlank())  helper.setCc(splitRecipients(cc).toArray(String[]::new));
            if (bcc != null && !bcc.isBlank()) helper.setBcc(splitRecipients(bcc).toArray(String[]::new));
            if (replyTo != null && !replyTo.isBlank()) helper.setReplyTo(replyTo);

            helper.setSubject(subject);
            helper.setText(htmlBody, true); // HTML = true
            mailSender.send(mime);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send HTML email", e);
        }
    }

    private List<String> splitRecipients(String csv) {
        return Arrays.stream(csv.split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
