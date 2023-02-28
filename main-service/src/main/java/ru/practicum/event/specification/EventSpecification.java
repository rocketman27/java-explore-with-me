package ru.practicum.event.specification;

import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Event_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder(setterPrefix = "with")
public class EventSpecification implements Specification<Event> {
    private final List<Long> users;
    private final List<Long> categories;
    private final LocalDateTime rangeStart;
    private final LocalDateTime rangeEnd;
    private final String text;
    private final Boolean paid;
    private final Boolean onlyAvailable;

    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate userInPredicate = withUserIn(root, criteriaBuilder);
        Predicate categoryInPredicate = withCategoryIn(root, criteriaBuilder);
        Predicate rangePredicate = withRange(root, criteriaBuilder);
        Predicate annotationLikePredicate = withAnnotationLike(root, criteriaBuilder);
        Predicate paidPredicatePredicate = withPaid(root, criteriaBuilder);
        Predicate onlyAvailablePredicate = withOnlyAvailable(root, criteriaBuilder);
        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(userInPredicate).ifPresent(predicates::add);
        Optional.ofNullable(categoryInPredicate).ifPresent(predicates::add);
        Optional.ofNullable(rangePredicate).ifPresent(predicates::add);
        Optional.ofNullable(annotationLikePredicate).ifPresent(predicates::add);
        Optional.ofNullable(paidPredicatePredicate).ifPresent(predicates::add);
        Optional.ofNullable(onlyAvailablePredicate).ifPresent(predicates::add);

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private Predicate withUserIn(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        if (users == null) {
            return null;
        }
        return criteriaBuilder.and(root.get(Event_.initiator).in(users));
    }

    private Predicate withCategoryIn(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        if (categories == null) {
            return null;
        }
        return criteriaBuilder.and(root.get(Event_.category).in(categories));
    }

    private Predicate withRange(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        if (rangeStart != null && rangeEnd != null) {
            return criteriaBuilder.between(root.get(Event_.eventDate), rangeStart, rangeEnd);
        } else if (rangeEnd == null) {
            return criteriaBuilder.greaterThan(root.get(Event_.eventDate), rangeStart);
        } else {
            return criteriaBuilder.lessThan(root.get(Event_.eventDate), rangeEnd);
        }
    }

    private Predicate withAnnotationLike(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        if (text == null) {
            return null;
        }
        return criteriaBuilder.like(root.get(Event_.annotation), "%" + text + "%");
    }

    private Predicate withPaid(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        if (paid == null) {
            return null;
        }
        return criteriaBuilder.equal(root.get(Event_.paid), paid);
    }

    private Predicate withOnlyAvailable(Root<Event> root, CriteriaBuilder criteriaBuilder) {
        if (onlyAvailable == null) {
            return null;
        }
        return criteriaBuilder.lessThan(root.get(Event_.confirmedRequests), root.get(Event_.participantLimit));
    }
}
