package com.majee.ecommerce.repository;

import com.majee.ecommerce.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findCategoryById(Long categoryId);

    List<Category> findCategoryByNameLikeIgnoreCase(String name, Sort sort);
}
