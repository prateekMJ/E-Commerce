package com.majee.ecommerce.service.impl;

import com.majee.ecommerce.entity.Category;
import com.majee.ecommerce.entity.Product;
import com.majee.ecommerce.exception.ResourceNotFoundException;
import com.majee.ecommerce.model.ProductDTO;
import com.majee.ecommerce.model.ProductResponse;
import com.majee.ecommerce.repository.CategoryRepository;
import com.majee.ecommerce.repository.ProductRepository;
import com.majee.ecommerce.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category wit ID : "+categoryId+" does not exist" , HttpStatus.NOT_FOUND));
        Product saveProduct = new Product();
        saveProduct.setCategory(savedCategory);
        saveProduct.setProductName(productDTO.getProductName());
        saveProduct.setPrice(productDTO.getPrice());
        saveProduct.setQuantity(productDTO.getQuantity());
        saveProduct.setDescription(productDTO.getDescription());
        saveProduct.setDiscount(productDTO.getDiscount());
        double discountedPrice = productDTO.getPrice() - ( productDTO.getPrice() * ( productDTO.getDiscount() / 100 ) );
        saveProduct.setSpecialPrice(discountedPrice);
        saveProduct.setImage(productDTO.getImage());

        Product savedProduct = productRepository.save(saveProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNum, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNum , pageSize, sortByAndOrder);

        Page<Product> productList = productRepository.findAll(pageable);
        List<ProductDTO> listOfProducts = productList.stream()
                .map(product -> modelMapper.map(product , ProductDTO.class))
                .toList();
        if(productList.isEmpty()){
            throw new ResourceNotFoundException("No products found", HttpStatus.NOT_FOUND);
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProducts(listOfProducts);
        return productResponse;
    }

    @Override
    public ProductResponse getProductByCategoryId(Long categoryId, Integer pageNum, Integer pageSize, String sortBy, String sortOrder) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category wit ID : "+categoryId+" does not exist" , HttpStatus.NOT_FOUND));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNum , pageSize, sortByAndOrder);

        Page<Product> productList = productRepository.findByCategory(savedCategory , pageable);
        List<ProductDTO> listOfProducts = productList.stream()
                .map(product -> modelMapper.map(product , ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProducts(listOfProducts);
        return productResponse;
    }

    @Override
    public ProductResponse getProductByKeyword(String keyword, Integer pageNum, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNum , pageSize, sortByAndOrder);

        Page<Product> productPageList = productRepository.findByProductNameLikeIgnoreCase("%"+keyword+"%" , pageable);
        List<ProductDTO> productList =  productPageList.stream()
                .map(product -> modelMapper.map(product , ProductDTO.class))
                .toList();
        if(productList.isEmpty()){
            throw new ResourceNotFoundException("Product name not found ",HttpStatus.NOT_FOUND);
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProducts(productList);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {

        Product savedProduct = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product id : "+productId+" does not exist" , HttpStatus.NOT_FOUND)
        );
        savedProduct.setProductName(productDTO.getProductName());
        savedProduct.setPrice(productDTO.getPrice());
        savedProduct.setQuantity(productDTO.getQuantity());
        savedProduct.setDescription(productDTO.getDescription());
        double discountedPrice = ( productDTO.getPrice() * ( productDTO.getDiscount() / 100 ) );
        savedProduct.setSpecialPrice(discountedPrice);
        savedProduct.setImage(productDTO.getImage());

        Product newlySavedProduct = productRepository.save(savedProduct);
        return modelMapper.map(newlySavedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO removeProduct(Long productId) {

        Product savedProduct = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product id : "+productId+" does not exist" , HttpStatus.NOT_FOUND)
        );

        productRepository.delete(savedProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }
}
