package com.majee.ecommerce.service;

import com.majee.ecommerce.model.CategoryRequestDTO;
import com.majee.ecommerce.model.CategoryResponseDTO;
import jakarta.validation.Valid;

public interface CategoryService {
    CategoryResponseDTO getAllCategory(Integer pageNum, Integer pageSize, String sortBy, String sortOrder);

    CategoryRequestDTO addCategory(CategoryRequestDTO categoryRequestDTO);

    CategoryRequestDTO updateCategory(@Valid CategoryRequestDTO categoryRequestDTO, Long categoryId);

    CategoryRequestDTO getCategoryById(Long categoryId);

    CategoryResponseDTO getCategoryByKeyword(String keyword , String sortBy, String sortOrder);

    CategoryRequestDTO deleteCategory(Long categoryId);
}
