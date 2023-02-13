package ru.practicum.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.RequestInfoDto;
import ru.practicum.dto.RequestInfoSummaryDto;
import ru.practicum.server.mappers.RequestInfoMapper;
import ru.practicum.server.mappers.RequestInfoSummaryMapper;
import ru.practicum.server.repositories.RequestsInfoRepository;

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
    public void saveRequestInfo(RequestInfoDto requestDto) {
        repository.save(RequestInfoMapper.toRequestInfo(requestDto));
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
