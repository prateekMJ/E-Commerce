package com.majee.ecommerce.repository;

import com.majee.ecommerce.entity.Category;
import com.majee.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByProductNameLikeIgnoreCase(String s, Pageable pageable);
}
