package ru.practicum.api;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.PrivateApi;
import org.openapitools.model.CommentDto;
import org.openapitools.model.EventFullDto;
import org.openapitools.model.EventRequestStatusUpdateRequest;
import org.openapitools.model.EventRequestStatusUpdateResult;
import org.openapitools.model.EventShortDto;
import org.openapitools.model.FullCommentDto;
import org.openapitools.model.NewEventDto;
import org.openapitools.model.ParticipationRequestDto;
import org.openapitools.model.UpdateEventUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.service.EventService;
import ru.practicum.request.service.ParticipationRequestService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class PrivateController implements PrivateApi {
    private final EventService eventService;
    private final ParticipationRequestService requestService;

    @Autowired
    public PrivateController(EventService eventService, ParticipationRequestService requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    @Override
    public ResponseEntity<List<EventShortDto>> getEvents(Long userId, Integer from, Integer size) {
        log.info("Received GET request to get all events from user with id={}", userId);
        return ResponseEntity.of(Optional.of(eventService.getEvents(userId, from, size)));
    }

    @Override
    public ResponseEntity<EventFullDto> addEvent(Long userId, NewEventDto newEventDto) {
        log.info("Received POST request to add a new event from user with id={}", userId);
        log.info(newEventDto.toString());
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(eventService.addEvent(userId, newEventDto));
    }

    @Override
    public ResponseEntity<EventFullDto> getEvent(Long userId, Long eventId) {
        log.info("Received GET request to get event with id={} from user with id={}", eventId, userId);
        return ResponseEntity.of(Optional.of(eventService.getEvent(userId, eventId)));
    }

    @Override
    public ResponseEntity<EventFullDto> updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Received PATCH request to update event with id={} from user with id={}", eventId, userId);
        return ResponseEntity.of(Optional.of(eventService.updateEvent(userId, eventId, updateEventUserRequest)));
    }

    @Override
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(Long userId, Long eventId) {
        return ResponseEntity.of(Optional.of(requestService.getEventParticipants(userId, eventId)));
    }

    @Override
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(Long userId) {
        return ResponseEntity.of(Optional.of(requestService.getUserRequests(userId)));
    }

    @Override
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestStatus(Long userId, Long eventId,
                                                                              EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return ResponseEntity.of(Optional.of(requestService.changeRequestStatus(userId, eventId, eventRequestStatusUpdateRequest)));
    }


    @Override
    public ResponseEntity<ParticipationRequestDto> addParticipationRequest(Long userId, Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(requestService.addParticipationRequest(userId, eventId));
    }

    @Override
    public ResponseEntity<ParticipationRequestDto> cancelRequest(Long userId, Long requestId) {
        return ResponseEntity.of(Optional.of(requestService.cancelRequest(userId, requestId)));
    }

    @Override
    public ResponseEntity<FullCommentDto> addUserComment(Long userId, Long eventId, CommentDto commentDto) {
        return  ResponseEntity.status(HttpStatus.CREATED)
                              .body(eventService.addComment(userId, eventId, commentDto));
    }

    @Override
    public ResponseEntity<FullCommentDto> getComment(Long userId, Long eventId, Long commentId) {
        return ResponseEntity.of(Optional.of(eventService.getComment(userId, eventId, commentId)));
    }

    @Override
    public ResponseEntity<Void> deleteComment(Long userId, Long eventId, Long commentId) {
        eventService.deleteComment(userId, commentId, commentId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<FullCommentDto>> getComments(Long userId, Long eventId) {
        return PrivateApi.super.getComments(userId, eventId);
    }

    @Override
    public ResponseEntity<FullCommentDto> updateComment(Long userId, Long eventId, Long commentId, CommentDto commentDto) {
        return ResponseEntity.of(Optional.of(eventService.updateComment(userId, eventId, commentId, commentDto)));
    }
}
