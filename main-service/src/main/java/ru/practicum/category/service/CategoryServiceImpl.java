package ru.practicum.category.service;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.CategoryDto;
import org.openapitools.model.NewCategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.ExceptionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.toEntity(newCategoryDto);
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteCategory(long catId) {
        Category category = categoryRepository.findById(catId)
                                              .orElseThrow(() -> ExceptionUtils.getCategoryNotFoundException(catId));
        categoryRepository.delete(category);
    }

    @Override
    public Optional<List<CategoryDto>> getCategories(Integer from, Integer size) {
        return Optional.of(categoryRepository.findAll(PageRequest.of(from / size, size))
                                             .stream()
                                             .map(categoryMapper::toDto)
                                             .collect(Collectors.toList()));
    }

    @Override
    public Optional<CategoryDto> getCategory(long catId) {
        return Optional.of(categoryRepository.findById(catId)
                                             .map(categoryMapper::toDto)
                                             .orElseThrow(() -> ExceptionUtils.getCategoryNotFoundException(catId)));
    }

    @Override
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                                              .orElseThrow(() -> ExceptionUtils.getCategoryNotFoundException(catId));

        category = categoryMapper.partialUpdate(categoryDto, category);

        return categoryMapper.toDto(categoryRepository.save(category));
    }
}
