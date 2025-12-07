package com.majee.ecommerce.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {

    private Long id;
    @NotNull
    @Size(min=3, max=20 , message = "category name length should be within 3 characters and 20 characters")
    private String name;
}
