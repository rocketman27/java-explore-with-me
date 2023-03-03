package ru.practicum.api;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.AdminApi;
import org.openapitools.model.CategoryDto;
import org.openapitools.model.CompilationDto;
import org.openapitools.model.EventFullDto;
import org.openapitools.model.NewCategoryDto;
import org.openapitools.model.NewCompilationDto;
import org.openapitools.model.NewUserRequest;
import org.openapitools.model.UpdateCompilationRequest;
import org.openapitools.model.UpdateEventAdminRequest;
import org.openapitools.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.service.CategoryService;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.event.service.EventService;
import ru.practicum.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class AdminController implements AdminApi {
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @Autowired
    public AdminController(CategoryService categoryService,
                           UserService userService, EventService eventService, CompilationService compilationService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.eventService = eventService;
        this.compilationService = compilationService;
    }

    @Override
    public ResponseEntity<CategoryDto> addCategory(NewCategoryDto newCategoryDto) {
        log.info("Received POST request to add a new category");
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(categoryService.addCategory(newCategoryDto));
    }

    @Override
    public ResponseEntity<Void> deleteCategory(Long catId) {
        log.info("Received DELETE request to delete category with id={}", catId);
        categoryService.deleteCategory(catId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CategoryDto> updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("Received PATCH request to update category with id={}", catId);
        return ResponseEntity.of(Optional.of(categoryService.updateCategory(catId, categoryDto)));
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        log.info("Received DELETE request to delete user with id={}", userId);
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<UserDto>> getUsers(List<Long> ids, Integer from, Integer size) {
        log.info("Received GET request to get users with IDs={}, pageable from={}, size={}", ids, from, size);
        return ResponseEntity.of(Optional.of(userService.getUsers(ids, PageRequest.of(from / size, size))));
    }

    @Override
    public ResponseEntity<UserDto> registerUser(NewUserRequest newUserRequest) {
        log.info("Received POST request to register a new user");
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(userService.registerUser(newUserRequest));
    }

    @Override
    public ResponseEntity<EventFullDto> updateEvent1(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Received PATCH request to update event with id={}", eventId);
        return ResponseEntity.of(Optional.of(eventService.updateEvent(eventId, updateEventAdminRequest)));
    }

    @Override
    public ResponseEntity<List<EventFullDto>> getEvents2(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        log.info("Received GET request from admin");
        return ResponseEntity.of(Optional.of(eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size)));
    }

    @Override
    public ResponseEntity<CompilationDto> saveCompilation(NewCompilationDto newCompilationDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(compilationService.saveCompilation(newCompilationDto));
    }

    @Override
    public ResponseEntity<Void> deleteCompilation(Long compId) {
        compilationService.deleteCompilation(compId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CompilationDto> updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        return ResponseEntity.of(Optional.of(compilationService.updateCompilation(compId, updateCompilationRequest)));
    }

    @Override
    public ResponseEntity<Void> deleteCommentByAdmin(Long eventId, Long commentId) {
        eventService.deleteComment(eventId, commentId);
        return ResponseEntity.noContent().build();
    }
}
