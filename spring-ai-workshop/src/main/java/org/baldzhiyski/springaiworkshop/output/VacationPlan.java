package org.baldzhiyski.springaiworkshop.output;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VacationPlan {

    private final ChatClient ollamaClient;

    public VacationPlan(@Qualifier("ollamaChatClient") ChatClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }


    @GetMapping("/vacation/structured")
    public Itinerary getItinerary() {
        return ollamaClient
                .prompt()
                .user("I want a trip plan to Hawaii . Give me a list of things to do")
                .call()
                .entity(Itinerary.class);
    }
}

