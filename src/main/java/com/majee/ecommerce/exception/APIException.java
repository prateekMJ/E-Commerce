package com.majee.ecommerce.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class APIException extends RuntimeException {

    private String message;
    private String status;
    public APIException(String message) {
        super(message);
    }
    public APIException(String message, String status) {
        super(message);
    }
}
