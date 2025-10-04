package org.baldzhiyski.aichatbotservice.chat;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;

    public ChatController(ChatClient chatClient, VectorStore vectorStore, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.chatMemory = chatMemory;
    }

    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestBody CustomerReq req,
                                              @RequestHeader(name = "X-Thread-Id", required = false) String threadId) {

        String tid = (threadId == null || threadId.isBlank()) ? "default" : threadId;
        String conversationId = "u:" + req.getCustomerId() + ":t:" + tid;




        String systemPrompt = SystemPrompts.generalAssistantWithUser(
                req.getCustomerId()
        );

        String answer = chatClient
                .prompt()
                .system(systemPrompt)
                .advisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId(conversationId)
                                .build(),
                        new QuestionAnswerAdvisor(vectorStore)
                )
                .user(req.getMessage())
                .call()
                .content();

        return ResponseEntity.ok(answer);
    }
}
