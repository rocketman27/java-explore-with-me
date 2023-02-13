package ru.practicum.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.RequestInfoDto;
import ru.practicum.dto.RequestInfoSummaryDto;
import ru.practicum.server.services.StatisticsServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
public class StatisticsController {
    private final StatisticsServiceImpl service;

    @Autowired
    public StatisticsController(StatisticsServiceImpl service) {
        this.service = service;
    }

    @PostMapping("/hit")
    public void saveRequest(@RequestBody RequestInfoDto requestInfoDto) {
        service.saveRequestInfo(requestInfoDto);
    }

    @GetMapping("/stats")
    public List<RequestInfoSummaryDto> getStatistics(@RequestParam
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                     LocalDateTime start,
                                                     @RequestParam
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                     LocalDateTime end,
                                                     @RequestParam(required = false, defaultValue = "")
                                                     String[] uris,
                                                     @RequestParam(required = false, defaultValue = "false")
                                                     boolean unique) {
        if (uris.length != 0) {
            return service.getRequestsInfo(start, end, uris, unique);
        } else {
            return service.getRequestsInfo(start, end, unique);
        }
    }
}
