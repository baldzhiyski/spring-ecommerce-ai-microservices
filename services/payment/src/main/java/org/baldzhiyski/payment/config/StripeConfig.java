package org.baldzhiyski.payment.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "stripe")
@Getter
@Setter // <- REQUIRED for binding
class StripeConfig {
    private String secretKey;
    private String webhookSecret;
    private String successUrl;
    private String cancelUrl;
    private String currency;

    @Bean StripeProps stripeProps() {
        return new StripeProps(secretKey, webhookSecret, successUrl, cancelUrl, currency);
    }
    
}
