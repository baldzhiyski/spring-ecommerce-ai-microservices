package org.baldzhiyski.notification.email;

import lombok.RequiredArgsConstructor;
import org.baldzhiyski.notification.service.TemplateRegistry;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplatedEmailService {
    private final SpringTemplateEngine emailTemplateEngine;
    private final EmailSender emailSender;
    private final TemplateRegistry templates;

    public void send(String to, String eventTypeOrRoutingKey, Map<String, Object> model) {
        String template = templates.templateFor(eventTypeOrRoutingKey);
        String subject  = templates.subjectFor(eventTypeOrRoutingKey, model);

        Context ctx = new Context(Locale.getDefault());
        ctx.setVariables(model);
        ctx.setVariable("subject", subject);
        ctx.setVariable("brand", model.getOrDefault("brand", "MyShop"));
        ctx.setVariable("preheader", model.getOrDefault("preheader", "You have a new update"));

        String html = emailTemplateEngine.process(template, ctx);
        emailSender.sendHtml(to, subject, html);
    }
}
