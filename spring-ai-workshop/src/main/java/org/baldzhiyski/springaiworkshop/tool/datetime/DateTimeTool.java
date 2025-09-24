package org.baldzhiyski.springaiworkshop.tool.datetime;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;

public class DateTimeTool {

    @Tool(description = "Get the current date and time in the user's timezone")
    public String getCurrentDateAndTime(){
        return LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }
}
