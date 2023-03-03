package ru.practicum.event.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.model.CommentDto;
import org.openapitools.model.FullCommentDto;
import ru.practicum.event.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CommentMapper {

    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "created", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "user", target = "userId", qualifiedByName = "convertUserToUserId")
    @Mapping(source = "event", target = "eventId", qualifiedByName = "convertEventToEventId")
    FullCommentDto toDto(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment partialUpdate(CommentDto commentDto, @MappingTarget Comment comment);

    @Named("convertUserToUserId")
    default long convertUserToUserId(User user) {
        return user.getId();
    }

    @Named("convertEventToEventId")
    default long convertEventToEventId(Event event) {
        return event.getId();
    }
}
