package org.baldzhiyski.springaiworkshop.chat;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")

public class ChatController {

    private final  ChatClient openAiChatClient;
    private final  ChatClient ollamaChatClient;

    public ChatController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,@Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @GetMapping("/chat")
    public String chat(){
        return ollamaChatClient
                .prompt()
                .user("Tell me an interesting fact about ML")
                .call()
                .content();
    }

    @GetMapping("/chat/stream")
    public Flux<String> chatStream(){
        return openAiChatClient
                .prompt()
                .user("Tell me an interesting fact about ML")
                .stream()
                .content();
    }
}
