package com.majee.ecommerce.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class APIResponse extends RuntimeException {

    private String message;
    private String status;
    public APIResponse(String message, String status) {
        super(message);
    }
}
