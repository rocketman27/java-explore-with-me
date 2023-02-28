package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.server.model.RequestInfo;
import ru.practicum.server.projection.RequestInfoSummary;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestsInfoRepository extends JpaRepository<RequestInfo, Long> {

    @Query(value = "select r.app as app, r.uri as uri, count(r.ip) as hits " +
            "from RequestInfo r " +
            "where (r.timestamp between ?1 and ?2) and r.uri in ?3 " +
            "group by r.app, r.uri " +
            "order by hits desc ")
    List<RequestInfoSummary> findRequests(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query(value = "select r.app as app, r.uri as uri, count(r.ip) as hits " +
            "from RequestInfo r " +
            "where r.timestamp between ?1 and ?2 " +
            "group by r.app, r.uri " +
            "order by hits desc ")
    List<RequestInfoSummary> findRequests(LocalDateTime start, LocalDateTime end);

    @Query(value = "select r.app as app, r.uri as uri, count(distinct (r.ip)) as hits " +
            "from RequestInfo r " +
            "where (r.timestamp between ?1 and ?2) and r.uri in ?3 " +
            "group by r.app, r.uri " +
            "order by count(distinct (r.ip)) desc ")
    List<RequestInfoSummary> findUniqueRequests(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query(value = "select r.app as app, r.uri as uri, count(distinct (r.ip)) as hits " +
            "from RequestInfo r " +
            "where r.timestamp between ?1 and ?2 " +
            "group by r.app, r.uri " +
            "order by count(distinct (r.ip)) desc ")
    List<RequestInfoSummary> findUniqueRequests(LocalDateTime start, LocalDateTime end);
}
