package com.majee.ecommerce.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignUpRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @Email
    @NotBlank
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(min = 6 , max = 40)
    private String password;
    private Set<String> role;
}
