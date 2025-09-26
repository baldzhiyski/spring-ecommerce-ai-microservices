package org.baldzhiyski.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class ThymeleafEmailConfig {
    @Bean
    public SpringTemplateEngine emailTemplateEngine() {
        var engine = new SpringTemplateEngine();
        engine.setTemplateResolver(emailTemplateResolver());
        return engine;
    }

    @Bean
    public SpringResourceTemplateResolver emailTemplateResolver() {
        var r = new SpringResourceTemplateResolver();
        r.setPrefix("classpath:/mail/"); // src/main/resources/mail/
        r.setSuffix(".html");
        r.setTemplateMode("HTML");
        r.setCharacterEncoding("UTF-8");
        r.setCacheable(false); // dev
        return r;
    }
}
