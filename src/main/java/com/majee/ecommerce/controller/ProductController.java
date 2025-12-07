package com.majee.ecommerce.controller;

import com.majee.ecommerce.constants.Constants;
import com.majee.ecommerce.model.ProductDTO;
import com.majee.ecommerce.model.ProductResponse;
import com.majee.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@PathVariable("categoryId") Long categoryId,
                                                 @RequestBody ProductDTO productDTO) {
        ProductDTO savedProductDto = productService.addProduct(categoryId , productDTO);
        return new ResponseEntity<>(savedProductDto , HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(value = "pageNum" , required = false , defaultValue = Constants.PAGENUM) Integer  pageNum,
                                                          @RequestParam(value = "pageSize" , required = false , defaultValue = Constants.PAGESIZE) Integer pageSize,
                                                          @RequestParam(value = "sortBy" , required = false , defaultValue = Constants.SORTBY) String sortBy,
                                                          @RequestParam(value = "sortOrder" , required = false , defaultValue = Constants.ORDER) String sortOrder) {
        ProductResponse productResponse = productService.getAllProducts(pageNum , pageSize , sortBy , sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategoryId(@PathVariable("categoryId") Long categoryId,
                                                                  @RequestParam(value = "pageNum" , required = false , defaultValue = Constants.PAGENUM) Integer  pageNum,
                                                                  @RequestParam(value = "pageSize" , required = false , defaultValue = Constants.PAGESIZE) Integer pageSize,
                                                                  @RequestParam(value = "sortBy" , required = false , defaultValue = Constants.PRODUCT_SORTBY) String sortBy,
                                                                  @RequestParam(value = "sortOrder" , required = false , defaultValue = Constants.ORDER) String sortOrder) {
        ProductResponse productResponse = productService.getProductByCategoryId(categoryId , pageNum , pageSize , sortBy , sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(@PathVariable("keyword") String keyword,
                                                               @RequestParam(value = "pageNum" , required = false , defaultValue = Constants.PAGENUM) Integer  pageNum,
                                                               @RequestParam(value = "pageSize" , required = false , defaultValue = Constants.PAGESIZE) Integer pageSize,
                                                               @RequestParam(value = "sortBy" , required = false , defaultValue = Constants.PRODUCT_SORTBY) String sortBy,
                                                               @RequestParam(value = "sortOrder" , required = false , defaultValue = Constants.ORDER) String sortOrder) {
        ProductResponse productResponse = productService.getProductByKeyword(keyword , pageNum , pageSize , sortBy , sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("productId") Long productId,
                                                 @RequestBody ProductDTO productDTO) {
        ProductDTO savedProductDto = productService.updateProduct(productId , productDTO);
        return new ResponseEntity<>(savedProductDto , HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> removeProduct(@PathVariable("productId") Long productId) {
        ProductDTO productDTO = productService.removeProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }
}
