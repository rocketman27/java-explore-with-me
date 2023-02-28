package ru.practicum.event.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.model.EventFullDto;
import org.openapitools.model.EventShortDto;
import org.openapitools.model.NewEventDto;
import org.openapitools.model.UpdateEventAdminRequest;
import org.openapitools.model.UpdateEventUserRequest;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.ExceptionUtils;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "location", source = "location")
    @Mapping(source = "category", target = "category", qualifiedByName = "lookUpCategory")
    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event toEntity(NewEventDto newEventDto, @Context CategoryRepository categoryRepository);

    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EventShortDto toEventShortDto(Event event);

    @InheritConfiguration(name = "toEntity")
    @Mapping(source = "category", target = "category", qualifiedByName = "lookUpCategory")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event partialUpdate(NewEventDto newEventDto, @MappingTarget Event event,
                        @Context CategoryRepository categoryRepository);

    @InheritConfiguration(name = "toEntity")
    @Mapping(source = "category", target = "category", qualifiedByName = "lookUpCategory")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event partialUpdate(UpdateEventUserRequest updateEventUserRequest, @MappingTarget Event event,
                        @Context CategoryRepository categoryRepository);

    @InheritConfiguration(name = "toEntity")
    @Mapping(source = "category", target = "category", qualifiedByName = "lookUpCategory")
    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event partialUpdate(UpdateEventAdminRequest updateEventAdminRequest, @MappingTarget Event event,
                        @Context CategoryRepository categoryRepository);

    @Named("lookUpCategory")
    default Category lookUpCategory(Long category, @Context CategoryRepository categoryRepository) {
        return categoryRepository.findById(category)
                                 .orElseThrow(() -> ExceptionUtils.getCategoryNotFoundException(category));
    }
}
