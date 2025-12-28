package com.majee.ecommerce.service;

import com.majee.ecommerce.entity.User;
import com.majee.ecommerce.model.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO addAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddress(Long addressId);

    List<AddressDTO> getUserSpecificAddress(User user);

    AddressDTO updateAddressById(Long addressId, @Valid AddressDTO addressDTO);

    String deleteAddressById(Long addressId);
}
