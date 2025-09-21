package org.baldzhiyski.mcpserversse;


import java.util.List;

public record Conference(
        String name,
        int year,
        List<String> dates,
        String location,
        List<Session> sessions
) {}
