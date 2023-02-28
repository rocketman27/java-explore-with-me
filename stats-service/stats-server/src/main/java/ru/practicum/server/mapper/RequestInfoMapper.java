package ru.practicum.server.mapper;

import ru.practicum.dto.RequestInfoDto;
import ru.practicum.server.model.RequestInfo;

public class RequestInfoMapper {

    public static RequestInfo toRequestInfo(RequestInfoDto requestDto) {
        return RequestInfo.builder()
                          .withApp(requestDto.getApp())
                          .withUri(requestDto.getUri())
                          .withIp(requestDto.getIp())
                          .withTimestamp(requestDto.getTimestamp())
                          .build();
    }
}
