package com.majee.ecommerce.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResourceNotFoundException extends RuntimeException{
    private String message;
    private HttpStatus status;
    public ResourceNotFoundException(String message, HttpStatus status){
        this.message = message;
        this.status = status;
    }
}
