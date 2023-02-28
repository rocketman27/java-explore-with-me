package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends PagingAndSortingRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Set<Event> findAllByIdIn(Set<Long> id);

    Optional<Event> findEventByIdAndInitiator(long eventId, User initiator);

    List<Event> findEventsByInitiator(User initiator, Pageable pageable);
}
