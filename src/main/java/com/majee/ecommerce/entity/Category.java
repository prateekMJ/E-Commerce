package com.majee.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min=3, max=20 , message = "category name length should be within 3 characters and 20 characters")
    private String name;

    @OneToMany(mappedBy = "category" ,  cascade = CascadeType.ALL)
    private List<Product> products;
}
