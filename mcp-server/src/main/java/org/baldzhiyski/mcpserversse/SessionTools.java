package org.baldzhiyski.mcpserversse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class SessionTools {

    private static final Logger log = LoggerFactory.getLogger(SessionTools.class);

    private final ObjectMapper objectMapper;
    private Conference conference; // holds sessions

    public SessionTools(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /* -------------------------------------------------------
     * Core finders (updated to new Session fields)
     * ----------------------------------------------------- */

    // All sessions on a specific date (YYYY-MM-DD)
    @Tool(name = "springone-sessions-by-date", description = "Returns all sessions for a given ISO date (e.g., '2025-08-26').")
    public List<Session> findSessionsByDate(String date) {
        LocalDate d = LocalDate.parse(date);
        return conference.sessions().stream()
                .filter(s -> d.equals(s.date()))
                .sorted(Comparator.comparing(Session::start))
                .toList();
    }

    // Find sessions by speaker (partial match, case-insensitive)
    @Tool(name = "springone-sessions-by-speaker", description = "Returns sessions for a given speaker (partial name match).")
    public List<Session> findSessionsBySpeaker(String speakerName) {
        String q = speakerName.trim().toLowerCase();
        return conference.sessions().stream()
                .filter(s -> s.speakers() != null && s.speakers().stream().anyMatch(sp -> sp != null && sp.toLowerCase().contains(q)))
                .sorted(Comparator.comparing(Session::start))
                .toList();
    }

    // Keyword search on title + description with optional limit
    @Tool(name = "springone-search", description = "Keyword search across title and description. Optional 'limit'.")
    public List<Session> searchSessions(String query, Integer limit) {
        String q = query.trim().toLowerCase();
        var matches = conference.sessions().stream()
                .filter(s -> (s.title() != null && s.title().toLowerCase().contains(q)) ||
                        (s.description() != null && s.description().toLowerCase().contains(q)))
                .sorted(Comparator.comparing(Session::start))
                .toList();
        if (limit == null || limit <= 0 || limit >= matches.size()) return matches;
        return matches.subList(0, limit);
    }

    // Count of sessions by 'type'
    @Tool(name = "springone-sessions-count-by-type", description = "Counts sessions grouped by type.")
    public Map<String, Long> countByType() {
        return conference.sessions().stream()
                .collect(Collectors.groupingBy(s -> s.type() == null ? "unknown" : s.type(), Collectors.counting()));
    }

    // Distinct speakers (alphabetically)
    @Tool(name = "springone-speakers", description = "Returns the distinct list of all speakers.")
    public List<String> listSpeakers() {
        return conference.sessions().stream()
                .flatMap(s -> s.speakers() == null ? StreamSupport.stream(Spliterators.emptySpliterator(), false) : s.speakers().stream())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !((String) s).isEmpty())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    // Sessions in a given location (partial match)
    @Tool(name = "springone-sessions-by-location", description = "Returns sessions held in a room/location (partial match).")
    public List<Session> findSessionsByLocation(String location) {
        String q = location.trim().toLowerCase();
        return conference.sessions().stream()
                .filter(s -> s.location() != null && s.location().toLowerCase().contains(q))
                .sorted(Comparator.comparing(Session::start))
                .toList();
    }

    // Lookup by exact session code
    @Tool(name = "springone-session-by-code", description = "Returns a single session by exact session 'code'.")
    public Session findByCode(String code) {
        return conference.sessions().stream()
                .filter(s -> s.code() != null && s.code().equalsIgnoreCase(code))
                .findFirst().orElse(null);
    }

    // Time-window filter for a given date (HH:mm .. HH:mm)
    @Tool(name = "springone-sessions-time-window", description = "Sessions for a date within [startTime,endTime] (HH:mm).")
    public List<Session> findByTimeWindow(String date, String startTime, String endTime) {
        LocalDate d = LocalDate.parse(date);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        return conference.sessions().stream()
                .filter(s -> d.equals(s.date()))
                .filter(s -> {
                    LocalTime st = s.start().toLocalTime();
                    return !st.isBefore(start) && !st.isAfter(end);
                })
                .sorted(Comparator.comparing(Session::start))
                .toList();
    }

    // Sorted plan for a date
    @Tool(name = "springone-day-plan", description = "Returns a sorted plan of sessions for a given date.")
    public List<Session> buildDayPlan(String date) {
        LocalDate d = LocalDate.parse(date);
        return conference.sessions().stream()
                .filter(s -> d.equals(s.date()))
                .sorted(Comparator.comparing(Session::start))
                .toList();
    }

    // Top frequent words in titles (toy)
    @Tool(name = "springone-top-title-words", description = "Top N frequent words in titles (stopwords removed).")
    public Map<String, Long> topTitleWords(Integer topN) {
        Set<String> stop = Set.of("the","a","an","and","of","to","for","in","on","with","at","by","from","is","are","how","what","into");
        Map<String, Long> freq = conference.sessions().stream()
                .map(Session::title)
                .filter(Objects::nonNull)
                .flatMap(t -> Arrays.stream(t.toLowerCase().split("[^a-z0-9]+")))
                .filter(w -> w.length() > 2 && !stop.contains(w))
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        return freq.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topN == null || topN <= 0 ? 10 : topN)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b) -> a, LinkedHashMap::new));
    }

    /* -------------------------------------------------------
     * Extra experimental tools
     * ----------------------------------------------------- */

    // Count sessions by date
    @Tool(name = "springone-sessions-count-by-date", description = "Returns {date -> count} for all dates.")
    public Map<String, Long> countSessionsByDate() {
        return conference.sessions().stream()
                .collect(Collectors.groupingBy(s -> s.date().toString(), TreeMap::new, Collectors.counting()));
    }

    // Sum of durations by date (minutes)
    @Tool(name = "springone-total-minutes-by-date", description = "Total scheduled minutes per date.")
    public Map<String, Long> totalMinutesByDate() {
        return conference.sessions().stream()
                .collect(Collectors.groupingBy(s -> s.date().toString(),
                        TreeMap::new,
                        Collectors.summingLong(s -> Optional.ofNullable(s.duration()).orElse(Duration.ZERO).toMinutes())));
    }

    // First start and last end per date
    @Tool(name = "springone-day-bounds", description = "First start & last end per date.")
    public Map<String, Map<String, String>> dayBounds() {
        return conference.sessions().stream()
                .collect(Collectors.groupingBy(s -> s.date().toString(), TreeMap::new, Collectors.collectingAndThen(
                        Collectors.toList(), list -> {
                            var first = list.stream().min(Comparator.comparing(Session::start)).map(Session::start).map(Object::toString).orElse(null);
                            var last  = list.stream().max(Comparator.comparing(Session::end)).map(Session::end).map(Object::toString).orElse(null);
                            return Map.of("firstStart", first, "lastEnd", last);
                        })));
    }

    // Overlaps in the same room for a date
    @Tool(name = "springone-overlaps-in-room", description = "Detects overlapping sessions for a date & location.")
    public List<Map<String, Object>> overlapsInRoom(String date, String location) {
        LocalDate d = LocalDate.parse(date);
        String loc = location.toLowerCase();
        var sameRoom = conference.sessions().stream()
                .filter(s -> d.equals(s.date()))
                .filter(s -> s.location() != null && s.location().toLowerCase().contains(loc))
                .sorted(Comparator.comparing(Session::start))
                .toList();

        List<Map<String,Object>> overlaps = new ArrayList<>();
        for (int i = 0; i < sameRoom.size(); i++) {
            for (int j = i + 1; j < sameRoom.size(); j++) {
                var a = sameRoom.get(i);
                var b = sameRoom.get(j);
                boolean overlap = !a.end().isBefore(b.start()) && !b.end().isBefore(a.start());
                if (overlap) {
                    overlaps.add(Map.of(
                            "aCode", a.code(), "aTitle", a.title(), "aStart", a.start().toString(), "aEnd", a.end().toString(),
                            "bCode", b.code(), "bTitle", b.title(), "bStart", b.start().toString(), "bEnd", b.end().toString(),
                            "location", a.location()
                    ));
                }
            }
        }
        return overlaps;
    }

    // Sessions starting within the next N minutes (from "now" server time)
    @Tool(name = "springone-starting-soon", description = "Sessions starting within the next N minutes from now.")
    public List<Session> startingSoon(Integer minutes) {
        var now = java.time.LocalDateTime.now();
        var until = now.plusMinutes(minutes == null || minutes <= 0 ? 30 : minutes);
        return conference.sessions().stream()
                .filter(s -> !s.start().isBefore(now) && !s.start().isAfter(until))
                .sorted(Comparator.comparing(Session::start))
                .toList();
    }

    // Speaker schedule (optionally for a given date)
    @Tool(name = "springone-speaker-schedule", description = "Schedule for a speaker; optional date filter (yyyy-MM-dd).")
    public List<Session> speakerSchedule(String speakerName, String date) {
        String q = speakerName.trim().toLowerCase();
        Predicate<Session> p = s -> s.speakers() != null && s.speakers().stream().anyMatch(sp -> sp != null && sp.toLowerCase().contains(q));
        if (date != null && !date.isBlank()) {
            LocalDate d = LocalDate.parse(date);
            p = p.and(s -> d.equals(s.date()));
        }
        return conference.sessions().stream().filter(p).sorted(Comparator.comparing(Session::start)).toList();
    }

    // Multi-filter query: any parameter may be null/blank
    @Tool(name = "springone-query", description = "Multi-filter: query (text), type, location, speaker, date (yyyy-MM-dd).")
    public List<Session> multiQuery(String query, String type, String location, String speaker, String date) {
        String q = blankLower(query);
        String t = blankLower(type);
        String loc = blankLower(location);
        String sp = blankLower(speaker);
        LocalDate d = (date == null || date.isBlank()) ? null : LocalDate.parse(date);

        return conference.sessions().stream()
                .filter(s -> d == null || d.equals(s.date()))
                .filter(s -> t == null || (s.type() != null && s.type().toLowerCase().contains(t)))
                .filter(s -> loc == null || (s.location() != null && s.location().toLowerCase().contains(loc)))
                .filter(s -> sp == null || (s.speakers() != null && s.speakers().stream().anyMatch(x -> x != null && x.toLowerCase().contains(sp))))
                .filter(s -> q == null || containsText(s, q))
                .sorted(Comparator.comparing(Session::start))
                .toList();
    }

    // Simple similar-session recommender by Jaccard over title+description
    @Tool(name = "springone-similar", description = "Find k sessions similar to the given session code (title+description).")
    public List<Session> similarTo(String code, Integer k) {
        var base = findByCode(code);
        if (base == null) return List.of();

        Set<String> baseTokens = tokens(base.title() + " " + base.description());
        return conference.sessions().stream()
                .filter(s -> !Objects.equals(s.code(), code))
                .map(s -> Map.entry(s, jaccard(baseTokens, tokens(s.title() + " " + s.description()))))
                .sorted(Map.Entry.<Session, Double>comparingByValue().reversed())
                .limit(k == null || k <= 0 ? 5 : k)
                .map(Map.Entry::getKey)
                .toList();
    }

    /* -------------------------------------------------------
     * Loader
     * ----------------------------------------------------- */

    @PostConstruct
    public void init() {
        log.info("Loading sessions from '/data/sessions.json'");
        try (InputStream is = SessionTools.class.getResourceAsStream("/data/sessions.json")) {
            if (is == null) throw new IllegalStateException("sessions.json not found on classpath at /data/sessions.json");
            // Expecting structure: { "conference": {..}, "sessions": [..] }
            var root = objectMapper.readTree(is);
            var confNode = root.get("conference");
            var sessionsNode = root.get("sessions");

            Conference meta = new Conference(
                    confNode.get("name").asText(),
                    confNode.get("year").asInt(),
                    objectMapper.convertValue(confNode.get("dates"), new com.fasterxml.jackson.core.type.TypeReference<List<String>>(){}),
                    confNode.get("location").asText(),
                    objectMapper.convertValue(sessionsNode, new com.fasterxml.jackson.core.type.TypeReference<List<Session>>() {})
            );
            this.conference = meta;
            log.info("Loaded {} sessions", conference.sessions().size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON data", e);
        }
    }

    /* -------------------------------------------------------
     * Helpers
     * ----------------------------------------------------- */

    private static String blankLower(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t.toLowerCase();
    }

    private static boolean containsText(Session s, String q) {
        return (s.title() != null && s.title().toLowerCase().contains(q)) ||
                (s.description() != null && s.description().toLowerCase().contains(q));
    }

    private static Set<String> tokens(String text) {
        if (text == null) return Set.of();
        String[] raw = text.toLowerCase().split("[^a-z0-9]+");
        Set<String> stop = Set.of("the","a","an","and","of","to","for","in","on","with","at","by","from","is","are","how","what","into");
        return Arrays.stream(raw).filter(w -> w.length() > 2 && !stop.contains(w)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static double jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() && b.isEmpty()) return 0.0;
        Set<String> inter = new HashSet<>(a);
        inter.retainAll(b);
        Set<String> uni = new HashSet<>(a);
        uni.addAll(b);
        return (double) inter.size() / (double) uni.size();
    }
}
