package com.example.mylists;

import java.time.*;

public class NaturalLanguageParser {

    public static ParsedResult parse(String raw, ZoneId zoneId) {
        if (raw == null || raw.isBlank()) {
            return new ParsedResult("", null);
        }

        String original = raw.trim();
        String lower = original.toLowerCase();

        LocalDate date = null;
        LocalTime time = null;

        // --- date ---
        if (lower.contains("tomorrow")) {
            date = LocalDate.now(zoneId).plusDays(1);
        } else if (lower.contains("today")) {
            date = LocalDate.now(zoneId);
        }

        // --- time of day ---
        if (lower.contains("morning")) {
            time = LocalTime.of(9, 0);
        } else if (lower.contains("afternoon")) {
            time = LocalTime.of(14, 0);
        } else if (lower.contains("evening")) {
            time = LocalTime.of(19, 0);
        } else if (lower.contains("tonight")) {
            time = LocalTime.of(20, 0);
        }

        // default time if we have a date but no specific time
        if (date != null && time == null) {
            time = LocalTime.of(9, 0);
        }

        Instant dueAt = null;
        if (date != null && time != null) {
            ZonedDateTime zdt = ZonedDateTime.of(date, time, zoneId);
            dueAt = zdt.toInstant();
        }

        // crude cleanup: strip known words from title
        String cleaned = original
                .replaceAll("(?i)\\btomorrow\\b", "")
                .replaceAll("(?i)\\btoday\\b", "")
                .replaceAll("(?i)\\bmorning\\b", "")
                .replaceAll("(?i)\\bafternoon\\b", "")
                .replaceAll("(?i)\\bevening\\b", "")
                .replaceAll("(?i)\\btonight\\b", "")
                .replaceAll("\\s+", " ")
                .trim();

        if (cleaned.isEmpty()) {
            cleaned = original; // fall back to original phrase
        }

        return new ParsedResult(cleaned, dueAt);
    }

    public record ParsedResult(String title, Instant dueAt) {}
}
