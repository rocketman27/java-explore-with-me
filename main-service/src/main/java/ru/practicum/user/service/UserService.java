package ru.practicum.user.service;

import org.openapitools.model.NewUserRequest;
import org.openapitools.model.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    void delete(Long userId);

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    UserDto registerUser(NewUserRequest newUserRequest);
}
