package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(setterPrefix = "with")
public class RequestInfoDto {
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
