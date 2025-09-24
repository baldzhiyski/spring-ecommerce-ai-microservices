package org.baldzhiyski.springaiworkshop.memory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MemoryController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public MemoryController(@Qualifier("openAiChatClient") ChatClient chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    @GetMapping("/memory")
    public String memory(@RequestParam String message,
                         @RequestHeader(name = "X-Conversation-Id", required = false) String convId) {

        String conversationId = (convId != null ? convId : "default-session"); // scope per user/session

        return chatClient
                .prompt()
                .advisors( MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(conversationId).build())
                .user(message)
                .call()
                .content();
    }
    @DeleteMapping("/memory")
    public void clear(@RequestHeader("X-Conversation-Id") String convId) {
        chatMemory.clear(convId);
    }
}
