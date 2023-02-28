package ru.practicum.request.mapper;

import org.mapstruct.*;
import org.openapitools.model.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ParticipationRequestMapper {
    @Mapping(source = "requester", target = "requesterId")
    @Mapping(source = "event", target = "eventId")
    ParticipationRequest toEntity(ParticipationRequestDto participationRequestDto);

    @InheritInverseConfiguration(name = "toEntity")
    ParticipationRequestDto toDto(ParticipationRequest participationRequest);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ParticipationRequest partialUpdate(ParticipationRequestDto participationRequestDto,
                                       @MappingTarget ParticipationRequest participationRequest);
}
