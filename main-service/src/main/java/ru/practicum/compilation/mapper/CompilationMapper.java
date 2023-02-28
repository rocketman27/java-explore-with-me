package ru.practicum.compilation.mapper;

import org.mapstruct.*;
import org.openapitools.model.CompilationDto;
import org.openapitools.model.EventShortDto;
import org.openapitools.model.NewCompilationDto;
import org.openapitools.model.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = EventRepository.class)
public interface CompilationMapper {
    @Mapping(source = "events", target = "events", qualifiedByName = "MapEventIdsToEvents")
    Compilation toEntity(NewCompilationDto compilationDto, @Context EventRepository repository);

    @Mapping(source = "events", target = "events", qualifiedByName = "MapEventToEventShortDto")
    CompilationDto toDto(Compilation compilation, @Context EventMapper mapper);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "events", target = "events", qualifiedByName = "MapEventIdsToEvents")
    Compilation partialUpdate(UpdateCompilationRequest updateCompilationRequest,
                              @MappingTarget Compilation compilation, @Context EventRepository repository);

    @Named("MapEventIdsToEvents")
    default Set<Event> mapEventIdsToEvents(Set<Long> sourceEvents, @Context EventRepository repository) {
        return repository.findAllByIdIn(sourceEvents);
    }

    @Named("MapEventToEventShortDto")
    default Set<EventShortDto> mapEventToEventShortDto(Set<Event> events, @Context EventMapper mapper) {
        return events.stream()
                     .map(mapper::toEventShortDto)
                     .collect(Collectors.toSet());
    }
}
