package com.majee.ecommerce.service;

import com.majee.ecommerce.model.ProductDTO;
import com.majee.ecommerce.model.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getAllProducts(Integer pageNum, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductByCategoryId(Long categoryId, Integer pageNum, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductByKeyword(String keyword, Integer pageNum, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO removeProduct(Long productId);
}
