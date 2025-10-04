package org.baldzhiyski.aichatbotservice.chat;

import jakarta.validation.constraints.NotBlank;


public class CustomerReq {

    public String getCustomerId() {
        return customerId;
    }

    public String getMessage() {
        return message;
    }

    public CustomerReq setCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public CustomerReq setMessage(String message) {
        this.message = message;
        return this;
    }

    @NotBlank
    private String message;

    @NotBlank
    private String customerId;
}
