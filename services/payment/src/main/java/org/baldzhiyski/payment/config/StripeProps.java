package org.baldzhiyski.payment.config;

public record StripeProps(String secretKey, String webhookSecret, String successUrl, String cancelUrl,
                          String currency) {
}
