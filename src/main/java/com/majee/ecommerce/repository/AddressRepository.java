package com.majee.ecommerce.repository;

import com.majee.ecommerce.entity.Address;
import com.majee.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}
