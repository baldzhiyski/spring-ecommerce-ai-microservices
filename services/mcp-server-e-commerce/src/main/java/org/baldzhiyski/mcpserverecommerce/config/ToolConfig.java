package org.baldzhiyski.mcpserverecommerce.config;


import org.baldzhiyski.mcpserverecommerce.tool.OrderTools;
import org.baldzhiyski.mcpserverecommerce.tool.PaymentTools;
import org.baldzhiyski.mcpserverecommerce.tool.ProductTools;
import org.springframework.ai.support.ToolCallbacks;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class ToolConfig {

    @Bean
    public List<ToolCallback> toolCallbacks(OrderTools orderTools,
                                            ProductTools productTools,
                                            PaymentTools paymentTools) {
        return Stream.of(
                        ToolCallbacks.from(orderTools),
                        ToolCallbacks.from(productTools),
                        ToolCallbacks.from(paymentTools)

                )
                .flatMap(Arrays::stream)
                .toList();
    }
}
