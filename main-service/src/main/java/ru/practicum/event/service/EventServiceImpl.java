package ru.practicum.event.service;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.CommentDto;
import org.openapitools.model.EventFullDto;
import org.openapitools.model.EventShortDto;
import org.openapitools.model.FullCommentDto;
import org.openapitools.model.NewEventDto;
import org.openapitools.model.UpdateEventAdminRequest;
import org.openapitools.model.UpdateEventUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatisticsClient;
import ru.practicum.event.mapper.CommentMapper;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.CommentRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.specification.EventSpecification;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ExceptionUtils;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.time.LocalDateTime.parse;
import static org.springframework.data.domain.Sort.Direction;
import static ru.practicum.utils.DateTimeConstants.DATE_TIME_FORMATTER;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final StatisticsClient statisticsClient;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            EventMapper eventMapper, StatisticsClient statisticsClient, CommentRepository commentRepository, CommentMapper commentMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
        this.statisticsClient = statisticsClient;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public EventFullDto addEvent(long userId, NewEventDto newEventDto) {
        Event event = eventMapper.toEntity(newEventDto, categoryRepository);

        User initiator = userRepository.findById(userId)
                                       .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        if (!isEventDateValid(newEventDto.getEventDate())) {
            throw new ConflictException(format("Event with id=%s has event in the past, cannot update the event.", event.getId()));
        }

        event.setInitiator(initiator);
        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0L);

        event = eventRepository.save(event);

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                                     .orElseThrow(() -> ExceptionUtils.getEventNotFoundException(eventId));

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getEvent(long userId, long eventId) {
        User initiator = userRepository.findById(userId)
                                       .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        Event event = eventRepository.findEventByIdAndInitiator(eventId, initiator)
                                     .orElseThrow(() -> ExceptionUtils.getEventNotFoundException(eventId));

        List<FullCommentDto> comments = commentRepository.findByEventId(eventId)
                                                         .stream()
                                                         .map(commentMapper::toDto)
                                                         .collect(Collectors.toList());

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setComments(comments);

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getEvents(long userId, int from, int size) {
        User initiator = userRepository.findById(userId)
                                       .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        return eventRepository.findEventsByInitiator(initiator, PageRequest.of(from / size, size))
                              .stream()
                              .map(eventMapper::toEventShortDto)
                              .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User initiator = userRepository.findById(userId)
                                       .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        Event event = eventRepository.findEventByIdAndInitiator(eventId, initiator)
                                     .orElseThrow(() -> ExceptionUtils.getEventNotFoundException(eventId));

        if (!isEventDateValid(updateEventUserRequest.getEventDate())) {
            throw new ConflictException(format("Event with id=%s has event in the past, cannot update the event.", event.getId()));
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            event = eventMapper.partialUpdate(updateEventUserRequest, event, categoryRepository);

            if (event.getStateAction() != null) {
                updateStateByUser(event);
            }

            return eventMapper.toEventFullDto(eventRepository.save(event));
        } else {
            throw new ConflictException(format("Event with id=%s is published, cannot update it.", eventId));
        }
    }

    private void updateStateByUser(Event event) {
        switch (event.getStateAction()) {
            case SEND_TO_REVIEW:
                event.setState(EventState.PENDING);
                break;
            case CANCEL_REVIEW:
                event.setState(EventState.CANCELED);
                break;
            default:
        }
    }

    @Override
    public EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                                     .orElseThrow(() -> ExceptionUtils.getEventNotFoundException(eventId));

        if (!isEventDateValid(updateEventAdminRequest.getEventDate())) {
            throw new ConflictException(format("Event with id=%s has event in the past, cannot update the event.", eventId));
        }

        if (event.getState().equals(EventState.PUBLISHED) || event.getState().equals(EventState.CANCELED)) {
            throw new ConflictException(format("Event with id=%s has state %s, cannot update the event.", eventId, event.getState()));
        }

        event = eventMapper.partialUpdate(updateEventAdminRequest, event, categoryRepository);

        if (event.getStateAction() != null) {
            updateStateByAdmin(event);
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    private boolean isEventDateValid(String eventDate) {
        if (eventDate == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        return (LocalDateTime.parse(eventDate, DATE_TIME_FORMATTER).isAfter(now));
    }

    private void updateStateByAdmin(Event event) {
        switch (event.getStateAction()) {
            case PUBLISH_EVENT:
                event.setState(EventState.PUBLISHED);
                break;
            case REJECT_EVENT:
                event.setState(EventState.CANCELED);
                break;
            default:
        }
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                               String rangeStart, String rangeEnd, Integer from, Integer size) {
        EventSpecification eventSpec = EventSpecification.builder()
                                                         .withUsers(users)
                                                         .withCategories(categories)
                                                         .withRangeStart(rangeStart != null
                                                                 ? parse(rangeStart, DATE_TIME_FORMATTER) : null)
                                                         .withRangeEnd(rangeStart != null
                                                                 ? parse(rangeEnd, DATE_TIME_FORMATTER) : null)
                                                         .build();

        return eventRepository.findAll(eventSpec, pageable(from / size, size))
                              .stream().map(eventMapper::toEventFullDto)
                              .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsByPublicUser(String text, List<Long> categories, Boolean paid,
                                                     String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                     String sort, Integer from, Integer size) {

        EventSpecification eventSpec = EventSpecification.builder()
                                                         .withText(text)
                                                         .withCategories(categories)
                                                         .withPaid(paid)
                                                         .withRangeStart(rangeStart != null
                                                                 ? parse(rangeStart, DATE_TIME_FORMATTER) : null)
                                                         .withRangeEnd(rangeStart != null
                                                                 ? parse(rangeEnd, DATE_TIME_FORMATTER) : null)
                                                         .withOnlyAvailable(onlyAvailable)
                                                         .build();

        Pageable pageable;

        if (sort != null) {
            String fieldName = getFieldNameBy(sort);
            pageable = pageable(from / size, size, fieldName);
        } else {
            pageable = pageable(from / size, size);
        }

        return eventRepository.findAll(eventSpec, pageable)
                              .stream().map(eventMapper::toEventShortDto)
                              .collect(Collectors.toList());
    }

    private Pageable pageable(int from, int size, String sortField) {
        return PageRequest.of(from / size, size, Direction.ASC, sortField);
    }

    private Pageable pageable(int from, int size) {
        return PageRequest.of(from / size, size);
    }

    public static String getFieldNameBy(String columnName) {
        Optional<Field> field = Arrays.stream(Event.class.getDeclaredFields())
                                      .filter(f -> f.getDeclaredAnnotationsByType(Column.class).length > 0)
                                      .filter(f -> f.getDeclaredAnnotationsByType(Column.class)[0].name().equals(columnName.toLowerCase()))
                                      .findFirst();

        return field.map(Field::getName).orElse(null);
    }

    @Override
    public FullCommentDto addComment(long userId, long eventId, CommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        Event event = eventRepository.findEventByIdAndInitiator(eventId, user)
                                     .orElseThrow(() -> ExceptionUtils.getEventNotFoundException(eventId));

        if (event.getState().equals(EventState.PUBLISHED)) {
            Comment comment = commentMapper.toEntity(newCommentDto);

            comment.setUser(user);
            comment.setEvent(event);

            return commentMapper.toDto(commentRepository.save(comment));
        } else {
            throw new ConflictException("User cannot comment an event which is not published");
        }
    }

    @Override
    public FullCommentDto getComment(long userId, long eventId, long commentId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        eventRepository.findEventByIdAndInitiator(eventId, user)
                       .orElseThrow(() -> ExceptionUtils.getEventNotFoundException(eventId));

        Comment comment = commentRepository.findById(commentId)
                                           .orElseThrow(() -> ExceptionUtils.getCommentNotFound(commentId));

        return commentMapper.toDto(comment);
    }

    @Override
    public FullCommentDto updateComment(long userId, long eventId, long commentId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        eventRepository.findEventByIdAndInitiator(eventId, user)
                       .orElseThrow(() -> ExceptionUtils.getEventNotFoundException(eventId));

        Comment comment = commentRepository.findById(commentId)
                                           .orElseThrow(() -> ExceptionUtils.getCommentNotFound(commentId));

        if (comment.getUser().equals(user)) {
            comment = commentMapper.partialUpdate(commentDto, comment);
            comment = commentRepository.save(comment);
            return commentMapper.toDto(comment);
        } else {
            throw new ConflictException(format("User with id=%s is not allowed to delete comment with id=%s", userId, commentId));
        }
    }

    @Override
    public void deleteComment(long userId, long eventId, long commentId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        Comment comment = commentRepository.findById(commentId)
                                           .orElseThrow(() -> ExceptionUtils.getCommentNotFound(commentId));

        if (comment.getUser().equals(user)) {
            commentRepository.delete(comment);
        } else {
            throw new ConflictException(format("User with id=%s is not allowed to delete comment with id=%s", userId, commentId));
        }
    }

    @Override
    public void deleteComment(long eventId, long commentId) {
        eventRepository.findById(eventId)
                       .orElseThrow(() -> ExceptionUtils.getEventNotFoundException(eventId));

        Comment comment = commentRepository.findById(commentId)
                                           .orElseThrow(() -> ExceptionUtils.getCommentNotFound(commentId));

        commentRepository.delete(comment);
    }
}
