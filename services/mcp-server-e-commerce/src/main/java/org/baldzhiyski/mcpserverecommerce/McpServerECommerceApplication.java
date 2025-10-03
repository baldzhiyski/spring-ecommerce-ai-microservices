package org.baldzhiyski.mcpserverecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class McpServerECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerECommerceApplication.class, args);
    }

}
