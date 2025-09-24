package org.baldzhiyski.springaiworkshop.tool.action;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskManagementController {

    private final ChatClient openAIChatClient;
    private final TaskManagementTools taskManagementTools;

    public TaskManagementController(@Qualifier("openAiChatClient") ChatClient openAIChatClient, TaskManagementTools taskManagementTools) {
        this.openAIChatClient = openAIChatClient;
        this.taskManagementTools = taskManagementTools;
    }

    @GetMapping("/tasks")
    public String createTask(@RequestParam String message) {
        return openAIChatClient.prompt()
                .tools(taskManagementTools)
                .user(message)
                .call()
                .content();
    }
}