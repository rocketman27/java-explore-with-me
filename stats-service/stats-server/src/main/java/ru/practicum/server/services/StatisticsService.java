package ru.practicum.server.services;

import ru.practicum.dto.RequestInfoDto;
import ru.practicum.dto.RequestInfoSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {
     void saveRequestInfo(RequestInfoDto requestDto);

     List<RequestInfoSummaryDto> getRequestsInfo(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);

     List<RequestInfoSummaryDto> getRequestsInfo(LocalDateTime start, LocalDateTime end, boolean unique);

}
