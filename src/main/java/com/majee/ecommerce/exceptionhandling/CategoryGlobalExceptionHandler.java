package com.majee.ecommerce.exceptionhandling;

import com.majee.ecommerce.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CategoryGlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String , String>> ResourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        Map<String , String> map = new HashMap<>();
        map.put("message", ex.getMessage());
        map.put("status" , String.valueOf(HttpStatus.NOT_FOUND));
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }
}
