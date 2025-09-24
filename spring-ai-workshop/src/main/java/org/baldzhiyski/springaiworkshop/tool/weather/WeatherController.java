package org.baldzhiyski.springaiworkshop.tool.weather;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final ChatClient openAIChatClient;
    private final WeatherTools weatherTools;

    public WeatherController(@Qualifier("openAiChatClient") ChatClient openAIChatClient, WeatherTools weatherTools) {
        this.openAIChatClient = openAIChatClient;

        this.weatherTools = weatherTools;
    }

    @GetMapping("/weather/alerts")
    public String getAlerts(@RequestParam(defaultValue = "NY") String message) {
        return openAIChatClient.prompt()
                .tools(weatherTools)
                .user(message)
                .call()
                .content();
    }
}