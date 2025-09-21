package org.baldzhiyski.springaiworkshop.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModelController {

    private final ChatClient openAIChatClient;
    private final VectorStore vectorStore;

    public ModelController(@Qualifier("openAiChatClient") ChatClient openAIChatClient, VectorStore vectorStore) {
        this.openAIChatClient = openAIChatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/rag/models")
    public Models faq(@RequestParam(value = "message", defaultValue = "Give me a list of all the models from OpenAI along with their context window.") String message) {
        return openAIChatClient.prompt()
                .user(message)
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .entity(Models.class);
    }
}
