package ru.practicum.category.mapper;

import org.mapstruct.*;
import org.openapitools.model.CategoryDto;
import org.openapitools.model.NewCategoryDto;
import ru.practicum.category.model.Category;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryDto categoryDto);

    Category toEntity(NewCategoryDto newCategoryDto);

    CategoryDto toDto(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Category partialUpdate(CategoryDto categoryDto, @MappingTarget Category category);
}
