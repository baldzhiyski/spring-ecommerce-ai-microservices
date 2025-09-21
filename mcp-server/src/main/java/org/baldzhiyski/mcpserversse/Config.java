package org.baldzhiyski.mcpserversse;

import io.modelcontextprotocol.server.McpServerFeatures;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Config {

    @Bean
    public List<ToolCallback> springOneSessionTools(SessionTools sessionTools) {
        return List.of(ToolCallbacks.from(sessionTools));
    }

    @Bean
    public List<McpServerFeatures.SyncResourceSpecification> springOneResources(
            SessionsResource sessionsResource // <- add more providers here
    ) {
        return sessionsResource.listResources(); // immutable is nice
    }


}
