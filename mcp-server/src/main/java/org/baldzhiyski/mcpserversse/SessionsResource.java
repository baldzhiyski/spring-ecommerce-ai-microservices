package org.baldzhiyski.mcpserversse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SessionsResource {

    private static final Logger log = LoggerFactory.getLogger(SessionsResource.class);

    private final ObjectMapper om;
    private Conference conference;

    public SessionsResource(ObjectMapper om) { this.om = om; }

    @PostConstruct
    void load() {
        log.info("Loading /data/sessions.json for MCP resources");
        try (InputStream is = getClass().getResourceAsStream("/data/sessions.json")) {
            if (is == null) throw new IllegalStateException("Missing /data/sessions.json");
            var root = om.readTree(is);

            this.conference = new Conference(
                    root.path("conference").path("name").asText(),
                    root.path("conference").path("year").asInt(),
                    om.convertValue(root.path("conference").path("dates"), new TypeReference<List<String>>(){}),
                    root.path("conference").path("location").asText(),
                    om.convertValue(root.path("sessions"), new TypeReference<List<Session>>() {})
            );

            log.info("Loaded {} sessions across {} dates", conference.sessions().size(), conference.dates().size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sessions.json", e);
        }
    }

    /** Build all MCP resource specs (immutable). */
    public List<McpServerFeatures.SyncResourceSpecification> listResources() {
        var specs = new ArrayList<McpServerFeatures.SyncResourceSpecification>();

        specs.add(spec(
                "baldzhiyski://sessions/index",
                "Overview and pointers to sessions resources",
                () -> Map.of(
                        "name", conference.name(),
                        "year", conference.year(),
                        "location", conference.location(),
                        "dates", conference.dates(),
                        "resources", List.of(
                                "baldzhiyski://sessions/catalog",
                                "baldzhiyski://sessions/dates",
                                "baldzhiyski://sessions/speakers",
                                "baldzhiyski://sessions/stats"
                        )
                )
        ));

        specs.add(spec(
                "baldzhiyski://sessions/catalog",
                "Full sessions catalog as JSON array",
                conference::sessions
        ));

        specs.add(spec(
                "baldzhiyski://sessions/dates",
                "List of conference dates (yyyy-MM-dd)",
                conference::dates
        ));

        specs.add(spec(
                "baldzhiyski://sessions/speakers",
                "Distinct speakers (sorted)",
                () -> conference.sessions().stream()
                        .flatMap(s -> s.speakers() == null ? Stream.of() : s.speakers().stream())
                        .filter(Objects::nonNull)
                        .map(String::trim).filter(s -> !s.isEmpty())
                        .distinct()
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .toList()
        ));

        specs.add(spec(
                "baldzhiyski://sessions/stats",
                "Aggregate stats: counts by type, counts by date, total minutes by date",
                () -> Map.of(
                        "countByType", conference.sessions().stream()
                                .collect(Collectors.groupingBy(
                                        s -> s.type() == null ? "unknown" : s.type(),
                                        TreeMap::new, Collectors.counting())),
                        "countByDate", conference.sessions().stream()
                                .collect(Collectors.groupingBy(s -> s.date().toString(), TreeMap::new, Collectors.counting())),
                        "totalMinutesByDate", conference.sessions().stream()
                                .collect(Collectors.groupingBy(s -> s.date().toString(),
                                        TreeMap::new,
                                        Collectors.summingLong(s -> Optional.ofNullable(s.duration()).orElse(Duration.ZERO).toMinutes())))
                )
        ));

        // Per-date slices
        for (String date : conference.dates()) {
            specs.add(spec(
                    "baldzhiyski://sessions/date/" + date,
                    "Sessions scheduled for " + date,
                    () -> conference.sessions().stream()
                            .filter(s -> s.date().toString().equals(date))
                            .sorted(Comparator.comparing(Session::start))
                            .toList()
            ));
        }

        return List.copyOf(specs);
    }

    /* ----------------- helper ----------------- */
    private McpServerFeatures.SyncResourceSpecification spec(String uri, String description, Supplier<?> supplier) {
        var resource = new McpSchema.Resource(uri, description, "application/json", uri, null);
        return new McpServerFeatures.SyncResourceSpecification(
                resource,
                (exchange, request) -> {
                    String json;
                    try {
                        json = om.writeValueAsString(supplier.get());
                    } catch (Exception e) {
                        json = "{}";
                    }
                    return new McpSchema.ReadResourceResult(
                            List.of(new McpSchema.TextResourceContents(uri, "application/json", json))
                    );
                }
        );
    }

    @FunctionalInterface
    private interface Supplier<T> { T get() throws Exception; }
}
