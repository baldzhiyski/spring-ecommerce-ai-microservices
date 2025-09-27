package org.baldzhiyski.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "stripe")
class StripeConfig {
    private String secretKey;
    private String webhookSecret;
    private String successUrl;
    private String cancelUrl;
    private String currency;
    @Bean
    StripeProps stripeProps() { return new StripeProps(secretKey, webhookSecret, successUrl, cancelUrl, currency); }
    // setters omitted for brevity
}
