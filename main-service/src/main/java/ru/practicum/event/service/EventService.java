package ru.practicum.event.service;

import org.openapitools.model.CommentDto;
import org.openapitools.model.EventFullDto;
import org.openapitools.model.EventShortDto;
import org.openapitools.model.FullCommentDto;
import org.openapitools.model.NewEventDto;
import org.openapitools.model.UpdateEventAdminRequest;
import org.openapitools.model.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long id);

    EventFullDto getEvent(long userId, long eventId);

    List<EventShortDto> getEvents(long userId, int from, int size);

    EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size);

    List<EventShortDto> getEventsByPublicUser(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size);

    FullCommentDto addComment(long userId, long eventId, CommentDto newCommentDto);

    FullCommentDto getComment(long userId, long eventId, long commentId);

    FullCommentDto updateComment(long userId, long eventId, long commentId, CommentDto commentDto);

    void deleteComment(long userId, long eventId, long commentId);

    void deleteComment(long eventId, long commentId);
}
