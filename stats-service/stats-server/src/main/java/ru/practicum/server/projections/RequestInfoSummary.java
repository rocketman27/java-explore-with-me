package ru.practicum.server.projections;

public interface RequestInfoSummary {
    String getApp();

    String getUri();

    Integer getHits();
}
