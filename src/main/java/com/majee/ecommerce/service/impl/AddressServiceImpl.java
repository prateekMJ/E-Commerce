package com.majee.ecommerce.service.impl;

import com.majee.ecommerce.entity.Address;
import com.majee.ecommerce.entity.User;
import com.majee.ecommerce.exception.ResourceNotFoundException;
import com.majee.ecommerce.model.AddressDTO;
import com.majee.ecommerce.repository.AddressRepository;
import com.majee.ecommerce.repository.UserRepository;
import com.majee.ecommerce.service.AddressService;
import com.majee.ecommerce.utility.AuthUtil;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDTO addAddress(AddressDTO addressDTO, User user) {

        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {

        List<Address> addresses = addressRepository.findAll();
        List<AddressDTO> addressDTOs = addresses.stream()
                .map(address -> modelMapper.map(address , AddressDTO.class))
                .toList();
        return addressDTOs;
    }

    @Override
    public AddressDTO getAddress(Long addressId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found for the id "+addressId , HttpStatus.NOT_FOUND));

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserSpecificAddress(User user) {
        List<Address> addresses = user.getAddresses();
        List<AddressDTO> addressDTOS = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
        return addressDTOS;
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, @Valid AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found for the id "+addressId , HttpStatus.NOT_FOUND));

        address.setBuildingName(addressDTO.getBuildingName());
        address.setCity(addressDTO.getCity());
        address.setCountry(addressDTO.getCountry());
        address.setPincode(addressDTO.getPincode());
        address.setStreet(addressDTO.getStreet());
        address.setState(addressDTO.getState());

        Address updatedAddress = addressRepository.save(address);

        User user = address.getUser();
        user.getAddresses().removeIf(addr -> addr.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);

        userRepository.save(user);
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found for the id "+addressId , HttpStatus.NOT_FOUND));

        addressRepository.deleteById(addressId);

        User user = address.getUser();
        user.getAddresses().removeIf(addr -> addr.getAddressId().equals(addressId));
        userRepository.save(user);

        return "Address has been deleted";
    }
}
