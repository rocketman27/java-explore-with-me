package ru.practicum.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.RequestInfoDto;
import ru.practicum.dto.RequestInfoSummaryDto;
import ru.practicum.server.mapper.RequestInfoMapper;
import ru.practicum.server.mapper.RequestInfoSummaryMapper;
import ru.practicum.server.model.RequestInfo;
import ru.practicum.server.repository.RequestsInfoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final RequestsInfoRepository repository;

    @Autowired
    public StatisticsServiceImpl(RequestsInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public RequestInfoDto saveRequestInfo(RequestInfoDto requestDto) {
        RequestInfo requestInfo = RequestInfoMapper.toRequestInfo(requestDto);
        repository.save(requestInfo);
        return RequestInfoMapper.toRequestInfoDto(requestInfo);
    }

    @Override
    public List<RequestInfoSummaryDto> getRequestsInfo(LocalDateTime start, LocalDateTime end, String[] uris,
                                                       boolean unique) {
        if (unique) {
            return repository.findUniqueRequests(start, end, uris)
                             .stream()
                             .map(RequestInfoSummaryMapper::toRequestInfoSummaryDto)
                             .collect(Collectors.toList());
        } else {
            return repository.findRequests(start, end, uris)
                             .stream()
                             .map(RequestInfoSummaryMapper::toRequestInfoSummaryDto)
                             .collect(Collectors.toList());
        }
    }

    @Override
    public List<RequestInfoSummaryDto> getRequestsInfo(LocalDateTime start, LocalDateTime end, boolean unique) {
        if (unique) {
            return repository.findUniqueRequests(start, end)
                             .stream()
                             .map(RequestInfoSummaryMapper::toRequestInfoSummaryDto)
                             .collect(Collectors.toList());
        } else {
            return repository.findRequests(start, end)
                             .stream()
                             .map(RequestInfoSummaryMapper::toRequestInfoSummaryDto)
                             .collect(Collectors.toList());
        }
    }
}
