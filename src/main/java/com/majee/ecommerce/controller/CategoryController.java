package com.majee.ecommerce.controller;

import com.majee.ecommerce.constants.Constants;
import com.majee.ecommerce.model.CategoryRequestDTO;
import com.majee.ecommerce.model.CategoryResponseDTO;
import com.majee.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponseDTO> getAllCategory(@RequestParam(value = "pageNum" , required = false , defaultValue = Constants.PAGENUM) Integer pageNum,
                                                              @RequestParam(value = "pageSize", required = false , defaultValue = Constants.PAGESIZE) Integer pageSize,
                                                              @RequestParam(value = "sortBy" , required = false , defaultValue = Constants.SORTBY) String sortBy,
                                                              @RequestParam(value = "sortOrder" , required = false , defaultValue = Constants.ORDER) String sortOrder) {
        CategoryResponseDTO categoryResponseDTO = categoryService.getAllCategory(pageNum , pageSize , sortBy , sortOrder);
        return new ResponseEntity<>(categoryResponseDTO , HttpStatus.OK);
    }

    @GetMapping("/public/category/{categoryId}")
    public ResponseEntity<CategoryRequestDTO> getCategoryById(@PathVariable("categoryId") Long categoryId){
        CategoryRequestDTO savedCategory = categoryService.getCategoryById(categoryId);
        return new ResponseEntity<>(savedCategory , HttpStatus.FOUND);
    }

    @GetMapping("/public/categories/{keyword}")
    public ResponseEntity<CategoryResponseDTO> getCategoryByKeyword(@PathVariable("keyword") String keyword,
                                                                   @RequestParam(value = "sortBy" , required = false , defaultValue = Constants.SORTBY) String sortBy,
                                                                   @RequestParam(value = "sortOrder" , required = false , defaultValue = Constants.ORDER) String sortOrder) {
        CategoryResponseDTO savedCategory = categoryService.getCategoryByKeyword(keyword , sortBy , sortOrder);
        return new ResponseEntity<>(savedCategory , HttpStatus.FOUND);
    }

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryRequestDTO> addCategory(@RequestBody @Valid CategoryRequestDTO categoryRequestDTO){
        CategoryRequestDTO savedcategoryRequestDTO = categoryService.addCategory(categoryRequestDTO);
        return new ResponseEntity<>(savedcategoryRequestDTO ,  HttpStatus.CREATED);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryRequestDTO> updateCategory(@RequestBody @Valid CategoryRequestDTO categoryRequestDTO,
                                                             @PathVariable("categoryId") Long categoryId){
        CategoryRequestDTO savedcategoryRequestDTO = categoryService.updateCategory(categoryRequestDTO , categoryId);
        return new ResponseEntity<>(savedcategoryRequestDTO ,  HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/category/{categoryId}")
    public ResponseEntity<CategoryRequestDTO> deleteCategory(@PathVariable("categoryId") Long categoryId){
        CategoryRequestDTO deletedCategory = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategory , HttpStatus.OK);
    }

}
