package com.majee.ecommerce.entity;


import com.majee.ecommerce.constants.AppRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Data
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    @ToString.Exclude
    private AppRole roleName;

    public Role(AppRole roleName) {
        this.roleName = roleName;
    }
}
