package org.baldzhiyski.notification.email;

import lombok.RequiredArgsConstructor;
import org.baldzhiyski.notification.service.TemplateRegistry;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplatedEmailService {
    private final SpringTemplateEngine emailTemplateEngine;
    private final EmailSender emailSender;
    private final TemplateRegistry templates;

    public void send(String to, String eventTypeOrRoutingKey, Map<String, Object> model) {
        String template = templates.templateFor(eventTypeOrRoutingKey);
        String subject  = templates.subjectFor(eventTypeOrRoutingKey, model);

        log.info("📧 Preparing email [eventType={}, to={}, template={}, subject={}]",
                eventTypeOrRoutingKey, to, template, subject);

        try {
            Context ctx = new Context(Locale.getDefault());
            ctx.setVariables(model);
            ctx.setVariable("subject", subject);
            ctx.setVariable("brand", model.getOrDefault("brand", "MyShop"));
            ctx.setVariable("preheader", model.getOrDefault("preheader", "You have a new update"));

            String html = emailTemplateEngine.process(template, ctx);

            emailSender.sendHtml(to, subject, html);

            log.info("✅ Email sent successfully [to={}, subject={}, template={}]", to, subject, template);

        } catch (Exception ex) {
            log.error("❌ Failed to send email [eventType={}, to={}, subject={}]. Reason: {}",
                    eventTypeOrRoutingKey, to, subject, ex.getMessage(), ex);
            throw ex; // rethrow so caller knows it failed
        }
    }
}
