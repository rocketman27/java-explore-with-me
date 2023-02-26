package ru.practicum.request.service;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.EventRequestStatusUpdateRequest;
import org.openapitools.model.EventRequestStatusUpdateResult;
import org.openapitools.model.ParticipationRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ExceptionUtils;
import ru.practicum.request.mapper.ParticipationRequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.openapitools.model.EventRequestStatusUpdateRequest.*;

@Slf4j
@Service
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final UserMapper userMapper;
    private final ParticipationRequestRepository requestRepository;
    private final ParticipationRequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    public ParticipationRequestServiceImpl(ParticipationRequestRepository participationRequestRepository,
                                           ParticipationRequestMapper requestMapper, UserRepository userRepository,
                                           EventRepository eventRepository,
                                           UserMapper userMapper) {
        this.requestRepository = participationRequestRepository;
        this.requestMapper = requestMapper;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        Event event = eventRepository.findById(eventId)
                                     .orElseThrow(() -> ExceptionUtils.getEventNotFoundException(eventId));

        return requestRepository.findByEventId(event.getId())
                                .stream()
                                .map(requestMapper::toDto)
                                .collect(Collectors.toList());

    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        return requestRepository.findByRequesterId(user.getId())
                                .stream()
                                .map(requestMapper::toDto)
                                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                      .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                                                        .orElseThrow(() -> ExceptionUtils.getRequestNotFoundException(requestId));

        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        userRepository.findById(userId)
                      .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        Event event = eventRepository.findById(eventId)
                                     .orElseThrow(() -> ExceptionUtils.getEventNotFoundException(eventId));

        StatusEnum status = eventRequestStatusUpdateRequest.getStatus();
        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();

        List<ParticipationRequest> requests = requestRepository.findAllById(requestIds);
        HashMap<String, List<ParticipationRequest>> processedRequests = rejectOrApproveRequest(event, requests, status);

        EventRequestStatusUpdateResult statusUpdateResult = new EventRequestStatusUpdateResult();

        if (processedRequests.get("rejected") != null) {
            statusUpdateResult.setRejectedRequests(processedRequests.get("rejected")
                                                                    .stream()
                                                                    .map(requestRepository::save)
                                                                    .map(requestMapper::toDto)
                                                                    .collect(Collectors.toList()));
        } else {
            statusUpdateResult.setRejectedRequests(Collections.emptyList());
        }

        if (processedRequests.get("confirmed") != null) {
            statusUpdateResult.setConfirmedRequests(processedRequests.get("confirmed")
                                                                     .stream()
                                                                     .map(requestRepository::save)
                                                                     .map(requestMapper::toDto)
                                                                     .collect(Collectors.toList()));

            long confirmedRequests = event.getConfirmedRequests();
            event.setConfirmedRequests(confirmedRequests + processedRequests.get("confirmed").size());
            eventRepository.save(event);
        } else {
            statusUpdateResult.setConfirmedRequests(Collections.emptyList());
        }

        return statusUpdateResult;
    }

    private HashMap<String, List<ParticipationRequest>> rejectOrApproveRequest(Event event, List<ParticipationRequest> requests, StatusEnum status) {
        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        HashMap<String, List<ParticipationRequest>> map = new HashMap<>();
        for (int i = 0; i < requests.size(); i++) {
            ParticipationRequest request = requests.get(i);
            if (isValidationNotRequired(event)) {
                request.setStatus(RequestStatus.CONFIRMED);
                continue;
            }

            if (isParticipantLimitReached(event)) {
                throw new ConflictException(format(
                        "Participant limit is reached for event id=%s, cannot raise a participation request.",
                        event.getId())
                );
            }

            if (!isRequestPending(request)) {
                throw new ConflictException(format("Request with id=%s is not pending, cannot process it.", request.getId()));
            }

            if (status.equals(StatusEnum.CONFIRMED)) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(request);
                if (isParticipantLimitReached(event)) {
                    log.info("Participation limit is reached for event id={}, cancelling the rest of the requests.", event.getId());
                    for (int j = i + 1; j < requests.size(); j++) {
                        ParticipationRequest rejectedRequest = requests.get(j);
                        rejectedRequest.setStatus(RequestStatus.REJECTED);
                        rejectedRequests.add(rejectedRequest);
                    }
                    map.put("confirmed", confirmedRequests);
                    map.put("rejected", rejectedRequests);
                    return map;
                }
            }

            if (status.equals(StatusEnum.REJECTED)) {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        }

        map.put("confirmed", confirmedRequests);
        map.put("rejected", rejectedRequests);
        return map;
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        Event event = eventRepository.findById(eventId)
                                     .orElseThrow(() -> ExceptionUtils.getEventNotFoundException(eventId));

        validateNewRequest(user, event);
        ParticipationRequest request = ParticipationRequest.builder()
                                                           .requesterId(userId)
                                                           .eventId(eventId).build();

        setNewParticipationRequestStatus(event, request);
        eventRepository.save(event);
        return requestMapper.toDto(requestRepository.save(request));
    }

    private void validateNewRequest(User user, Event event) {
        if (isEventInitiator(user, event)) {
            throw new ConflictException(format(
                    "Event's initiator with id=%s cannot raise a participation request for event id=%s.",
                    user.getId(), event.getId())
            );
        }
        if (isEventPending(event)) {
            throw new ConflictException(
                    format("Event with id=%s is pending, cannot raise a participation request.", event.getId())
            );
        }
        if (isParticipantLimitReached(event)) {
            throw new ConflictException(format(
                    "Participant limit is reached for event id=%s, cannot raise a participation request.",
                    event.getId())
            );
        }
    }

    private boolean isEventInitiator(User user, Event event) {
        return event.getInitiator() == user;
    }

    private boolean isRequestPending(ParticipationRequest request) {
        return request.getStatus().equals(RequestStatus.PENDING);
    }

    private boolean isEventPending(Event event) {
        return event.getState().equals(EventState.PENDING);
    }

    private boolean isParticipantLimitReached(Event event) {
        return event.getConfirmedRequests() == event.getParticipantLimit().longValue();
    }

    private boolean isValidationNotRequired(Event event) {
        return event.getParticipantLimit() == 0 || !event.getRequestModeration();
    }

    private void setNewParticipationRequestStatus(Event event, ParticipationRequest request) {
        if (isValidationNotRequired(event)) {
            log.info("Event's participant limit is 0 or moderation is not required, request's status is Confirmed.");
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            log.info("Event's status is Pending.");
            request.setStatus(RequestStatus.PENDING);
        }
    }
}
