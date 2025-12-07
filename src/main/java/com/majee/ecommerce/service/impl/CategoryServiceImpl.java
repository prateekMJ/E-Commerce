package com.majee.ecommerce.service.impl;

import com.majee.ecommerce.entity.Category;
import com.majee.ecommerce.exception.ResourceNotFoundException;
import com.majee.ecommerce.model.CategoryRequestDTO;
import com.majee.ecommerce.model.CategoryResponseDTO;
import com.majee.ecommerce.repository.CategoryRepository;
import com.majee.ecommerce.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponseDTO getAllCategory(Integer pageNum, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNum , pageSize, sortByAndOrder);
        Page<Category> category = categoryRepository.findAll(pageable);
        List<CategoryRequestDTO> categoryRequestDTOS = category.stream()
                .map(ctgry -> modelMapper.map(ctgry , CategoryRequestDTO.class))
                .toList();
        if(categoryRequestDTOS.isEmpty()) throw new ResourceNotFoundException("No category found" , HttpStatus.NOT_FOUND);
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setCategories(categoryRequestDTOS);
        return categoryResponseDTO;
    }

    @Override
    public CategoryRequestDTO addCategory(CategoryRequestDTO categoryRequestDTO) {

        Category category = modelMapper.map(categoryRequestDTO, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory , CategoryRequestDTO.class);

    }

    @Override
    public CategoryRequestDTO updateCategory(CategoryRequestDTO categoryRequestDTO, Long categoryId) {
        Category existingCategory = categoryRepository.findCategoryById(categoryId);
        if(existingCategory == null){
            throw new ResourceNotFoundException("Category with id :"+categoryId+" does not exists." , HttpStatus.NOT_FOUND);
        }
        existingCategory.setName(categoryRequestDTO.getName());
        Category savedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(savedCategory, CategoryRequestDTO.class);
    }

    @Override
    public CategoryRequestDTO getCategoryById(Long categoryId) {
        Category saveCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category with id :"+categoryId+" does not exists." , HttpStatus.NOT_FOUND));
        return modelMapper.map(saveCategory, CategoryRequestDTO.class);
    }

    @Override
    public CategoryResponseDTO getCategoryByKeyword(String keyword , String sortBy, String sortOrder) {

        CategoryResponseDTO savedCategoryResponseDTO = new CategoryResponseDTO();
        List<Category> savedCategories = categoryRepository.findCategoryByNameLikeIgnoreCase("%"+keyword+"%", sortOrder.equalsIgnoreCase("asc") ?
                                                                                                Sort.by(sortBy).ascending()
                                                                                                : Sort.by(sortBy).descending());
        if(savedCategories.isEmpty()){
            throw new ResourceNotFoundException("Category with name :"+keyword+" does not exists." , HttpStatus.NOT_FOUND);
        }else{
            List<CategoryRequestDTO> fetchedSavedCategories = savedCategories.stream()
                    .map(category -> modelMapper.map(category , CategoryRequestDTO.class))
                    .toList();
            savedCategoryResponseDTO.setCategories(fetchedSavedCategories);
        }
        return savedCategoryResponseDTO;
    }

    @Override
    public CategoryRequestDTO deleteCategory(Long categoryId) {
        Category existingCategory = categoryRepository.findCategoryById(categoryId);
        if(existingCategory == null){
            throw new ResourceNotFoundException("Category with id :"+categoryId+" does not exists." , HttpStatus.NOT_FOUND);
        }
        categoryRepository.delete(existingCategory);
        return modelMapper.map(existingCategory , CategoryRequestDTO.class);
    }
}
