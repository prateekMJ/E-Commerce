package com.majee.ecommerce.security.response;

import lombok.Data;

@Data
public class MessageResponse {
    private String message;
    public MessageResponse(String usernameIsAlreadyInUse) {
        this.message = usernameIsAlreadyInUse;
    }
}
