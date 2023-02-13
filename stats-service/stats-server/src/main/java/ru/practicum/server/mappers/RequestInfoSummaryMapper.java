package ru.practicum.server.mappers;

import ru.practicum.dto.RequestInfoSummaryDto;
import ru.practicum.server.projections.RequestInfoSummary;

public class RequestInfoSummaryMapper {

    public static RequestInfoSummaryDto toRequestInfoSummaryDto(RequestInfoSummary summary) {
        return RequestInfoSummaryDto.builder()
                                    .withApp(summary.getApp())
                                    .withUri(summary.getUri())
                                    .withHits(summary.getHits())
                                    .build();
    }
}
