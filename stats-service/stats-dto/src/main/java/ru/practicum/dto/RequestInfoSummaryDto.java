package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
public class RequestInfoSummaryDto {
    private String app;
    private String uri;
    private Long hits;
}
