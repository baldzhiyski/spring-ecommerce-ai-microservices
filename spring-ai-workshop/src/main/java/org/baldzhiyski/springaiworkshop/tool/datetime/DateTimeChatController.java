package org.baldzhiyski.springaiworkshop.tool.datetime;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DateTimeChatController{

    private final ChatClient openAIChatClient;

    public DateTimeChatController(@Qualifier("openAiChatClient") ChatClient openAIChatClient) {
        this.openAIChatClient = openAIChatClient;
    }

    @GetMapping("/tools")
    public String tools() {
        return openAIChatClient.
                prompt()
                .user("What day is tomorrow?")
                .tools(new DateTimeTool())
                .call()
                .content();
    }

}