package org.baldzhiyski.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;

@Configuration
public class AuditingConfig {
    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> java.util.Optional.of(java.time.OffsetDateTime.now());
    }
}
