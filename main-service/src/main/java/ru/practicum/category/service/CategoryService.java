package ru.practicum.category.service;

import org.openapitools.model.CategoryDto;
import org.openapitools.model.NewCategoryDto;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(long catId);

    Optional<List<CategoryDto>> getCategories(Integer from, Integer size);

    Optional<CategoryDto> getCategory(long catId);

    CategoryDto updateCategory(long catId, CategoryDto categoryDto);
}
