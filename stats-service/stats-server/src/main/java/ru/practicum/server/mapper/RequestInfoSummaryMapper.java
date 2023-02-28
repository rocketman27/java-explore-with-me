package ru.practicum.server.mapper;

import ru.practicum.dto.RequestInfoSummaryDto;
import ru.practicum.server.projection.RequestInfoSummary;

public class RequestInfoSummaryMapper {

    public static RequestInfoSummaryDto toRequestInfoSummaryDto(RequestInfoSummary summary) {
        return RequestInfoSummaryDto.builder()
                                    .withApp(summary.getApp())
                                    .withUri(summary.getUri())
                                    .withHits(summary.getHits())
                                    .build();
    }
}
