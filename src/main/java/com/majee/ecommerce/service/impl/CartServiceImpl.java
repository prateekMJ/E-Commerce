package com.majee.ecommerce.service.impl;

import com.majee.ecommerce.entity.Cart;
import com.majee.ecommerce.entity.CartItem;
import com.majee.ecommerce.entity.Product;
import com.majee.ecommerce.exception.APIException;
import com.majee.ecommerce.exception.ResourceNotFoundException;
import com.majee.ecommerce.model.CartDTO;
import com.majee.ecommerce.model.ProductDTO;
import com.majee.ecommerce.repository.CartItemRepository;
import com.majee.ecommerce.repository.CartRepository;
import com.majee.ecommerce.repository.ProductRepository;
import com.majee.ecommerce.service.CartService;
import com.majee.ecommerce.utility.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found" , HttpStatus.NOT_FOUND));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),
                productId
        );

        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart>  carts = cartRepository.findAll();

        if(carts.isEmpty()) {
            throw new APIException("No carts exist");
        }

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                    CartDTO cartDto = modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> products = cart.getCartItems().stream()
                            .map(product -> modelMapper.map(product , ProductDTO.class))
                            .toList();
                    cartDto.setProducts(products);
                    return cartDto;
                })
                .toList();

        return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {

        Cart cart = cartRepository.findCartByEmailAndCartId(emailId , cartId);
        if(cart == null) {
            throw new ResourceNotFoundException("Cart not found" , HttpStatus.NOT_FOUND);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(cartItem -> cartItem.getProduct().setQuantity(cartItem.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p, ProductDTO.class))
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String email = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(email);
        Cart cart = cartRepository.findById(userCart.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found" , HttpStatus.NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found" , HttpStatus.NOT_FOUND));


        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, userCart.getCartId());
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " does not exist in the cart");
        }

        int newQuantity = cartItem.getQuantity() + quantity;

        if(newQuantity < 0){
            throw new APIException("The resulting quantity cannot be negative.");
        }

        if(newQuantity == 0){
            deleteProductFromCart(userCart.getCartId() ,  productId);
        }else {

            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }
        CartItem updatedItem = cartItemRepository.save(cartItem);

        if(updatedItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream()
                .map(item -> {
                    ProductDTO prod =  modelMapper.map(item.getProduct(), ProductDTO.class);
                    prod.setQuantity(item.getQuantity());
                    return prod;
                });

        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found" , HttpStatus.NOT_FOUND));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cart.getCartId());

        if(cartItem == null) {
            throw new ResourceNotFoundException("Product "+productId+" not found" , HttpStatus.NOT_FOUND);
        }

        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId , productId);
        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found" , HttpStatus.NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found" , HttpStatus.NOT_FOUND));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cart.getCartId());

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " does not exist in the cart");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);

    }

    private Cart createCart(){

        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;
    }
}
