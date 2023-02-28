package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Optional<ParticipationRequest> findByIdAndRequesterId(long requestId, long requesterId);

    List<ParticipationRequest> findByEventId(long eventId);

    List<ParticipationRequest> findByRequesterId(long requesterId);
}
