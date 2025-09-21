package org.baldzhiyski.springaiworkshop.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModellConfig {

    // OpenAI client
    @Bean("openAiChatClient")
    public ChatClient openAiChatClient(OpenAiChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    // Ollama client
    @Bean("ollamaChatClient")
    public ChatClient ollamaAiChatClient(OllamaChatModel chatModel) {
        return ChatClient.create(chatModel);
    }
}