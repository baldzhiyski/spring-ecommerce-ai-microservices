package org.baldzhiyski.mcpserversse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record Session(

        String id,
        String code,
        String title,
        String description,
        String type,
        List<String> speakers,
        LocalDate date,
        LocalDateTime start,
        LocalDateTime end,
        Duration duration,
        String location
) {}
