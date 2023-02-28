package ru.practicum.compilation.service;

import org.openapitools.model.CompilationDto;
import org.openapitools.model.NewCompilationDto;
import org.openapitools.model.UpdateCompilationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ExceptionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository,
                                  EventMapper eventMapper, CompilationMapper compilationMapper) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.compilationMapper = compilationMapper;
    }

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toEntity(newCompilationDto, eventRepository);
        compilation = compilationRepository.save(compilation);
        return compilationMapper.toDto(compilation, eventMapper);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                                                       .orElseThrow(() -> ExceptionUtils.getCompilationNotFound(compId));

        compilation = compilationMapper.partialUpdate(updateCompilationRequest, compilation, eventRepository);
        compilation = compilationRepository.save(compilation);
        compilation.getEvents().stream().map(eventRepository::save);
        return compilationMapper.toDto(compilation, eventMapper);
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                                                       .orElseThrow(() -> ExceptionUtils.getCompilationNotFound(compId));

        return compilationMapper.toDto(compilation, eventMapper);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, pageRequest)
                                        .stream()
                                        .map(compilation -> compilationMapper.toDto(compilation, eventMapper))
                                        .collect(Collectors.toList());
        } else {
            return compilationRepository.findAll(pageRequest)
                                        .stream()
                                        .map(compilation -> compilationMapper.toDto(compilation, eventMapper))
                                        .collect(Collectors.toList());
        }
    }
}
