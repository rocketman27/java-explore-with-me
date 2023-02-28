package ru.practicum.api;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.PublicApi;
import org.openapitools.model.CategoryDto;
import org.openapitools.model.CompilationDto;
import org.openapitools.model.EventFullDto;
import org.openapitools.model.EventShortDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.practicum.category.service.CategoryService;
import ru.practicum.client.StatisticsClient;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.dto.RequestInfoDto;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class PublicController implements PublicApi {
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final StatisticsClient statisticsClient;

    @Autowired
    public PublicController(CategoryService categoryService,
                            EventService eventService,
                            CompilationService compilationService,
                            StatisticsClient statisticsClient) {
        this.categoryService = categoryService;
        this.eventService = eventService;
        this.compilationService = compilationService;
        this.statisticsClient = statisticsClient;
    }

    @Override
    public ResponseEntity<List<CategoryDto>> getCategories(Integer from, Integer size) {
        log.info("Received GET request to get categories, pageable from={}, size={}", from, size);
        return ResponseEntity.of(categoryService.getCategories(from, size));
    }

    @Override
    public ResponseEntity<CategoryDto> getCategory(Long catId) {
        log.info("Received GET request to get category with id={}", catId);
        return ResponseEntity.of(categoryService.getCategory(catId));
    }

    @Override
    public ResponseEntity<EventFullDto> getEvent1(Long id) {
        hitStatisticService();
        return ResponseEntity.of(Optional.of(eventService.getEvent(id)));
    }

    @Override
    public ResponseEntity<List<EventShortDto>> getEvents1(String text, List<Long> categories, Boolean paid,
                                                          String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                          String sort, Integer from, Integer size) {
        hitStatisticService();
        return ResponseEntity.of(Optional.of(
                eventService.getEventsByPublicUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size))
        );
    }

    @Override
    public ResponseEntity<CompilationDto> getCompilation(Long compId) {
        return ResponseEntity.of(Optional.of(compilationService.getCompilation(compId)));
    }

    @Override
    public ResponseEntity<List<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size) {
        return ResponseEntity.of(Optional.of(compilationService.getCompilations(pinned, from, size)));
    }

    private void hitStatisticService() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String remoteAddress = attributes.getRequest().getRemoteAddr();
        String path = attributes.getRequest().getRequestURI();

        RequestInfoDto requestInfoDto = RequestInfoDto.builder().withApp("main-service")
                                                      .withIp(remoteAddress)
                                                      .withUri(path)
                                                      .withTimestamp(LocalDateTime.now())
                                                      .build();

        statisticsClient.hit(requestInfoDto);
    }
}
