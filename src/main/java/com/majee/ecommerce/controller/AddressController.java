package com.majee.ecommerce.controller;

import com.majee.ecommerce.entity.User;
import com.majee.ecommerce.model.AddressDTO;
import com.majee.ecommerce.service.AddressService;
import com.majee.ecommerce.utility.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@RequestBody @Valid AddressDTO addressDTO) {

        User user = authUtil.loggedInUser();

        AddressDTO savedAddress = addressService.addAddress(addressDTO , user);
        return new ResponseEntity<AddressDTO>(savedAddress, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {

        List<AddressDTO> savedAddresses = addressService.getAllAddresses();
        return new ResponseEntity<>(savedAddresses , HttpStatus.FOUND);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {

        AddressDTO savedAddress = addressService.getAddress(addressId);
        return new ResponseEntity<>(savedAddress , HttpStatus.FOUND);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserSpecificAddress() {

        User user = authUtil.loggedInUser();
        List<AddressDTO> savedAddress = addressService.getUserSpecificAddress(user);
        return new ResponseEntity<>(savedAddress , HttpStatus.FOUND);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Long addressId ,
                                                        @RequestBody @Valid AddressDTO addressDTO) {

        AddressDTO updatedAddress = addressService.updateAddressById(addressId , addressDTO);
        return new ResponseEntity<>(updatedAddress , HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId) {

        String status = addressService.deleteAddressById(addressId);
        return new ResponseEntity<>(status , HttpStatus.OK);
    }
}
