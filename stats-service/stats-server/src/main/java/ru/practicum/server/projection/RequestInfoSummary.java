package ru.practicum.server.projection;

public interface RequestInfoSummary {
    String getApp();

    String getUri();

    Long getHits();
}
