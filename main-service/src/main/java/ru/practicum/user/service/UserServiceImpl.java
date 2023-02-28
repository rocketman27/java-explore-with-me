package ru.practicum.user.service;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.NewUserRequest;
import org.openapitools.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ExceptionUtils;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
    }

    @Override
    public void delete(Long userId) {
        User user = repository.findById(userId)
                              .orElseThrow(() -> ExceptionUtils.getUserNotFoundException(userId));

        repository.delete(user);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        return repository.findAllByIdIn(ids, pageable)
                         .stream()
                         .map(userMapper::toDto)
                         .collect(Collectors.toList());
    }

    @Override
    public UserDto registerUser(NewUserRequest newUserRequest) {
        User user = userMapper.toEntity(newUserRequest);
        return userMapper.toDto(repository.save(user));
    }
}
