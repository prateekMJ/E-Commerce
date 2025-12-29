package com.majee.ecommerce.service.impl;

import com.majee.ecommerce.entity.*;
import com.majee.ecommerce.exception.APIException;
import com.majee.ecommerce.exception.ResourceNotFoundException;
import com.majee.ecommerce.model.OrderDTO;
import com.majee.ecommerce.model.OrderItemDTO;
import com.majee.ecommerce.repository.*;
import com.majee.ecommerce.service.CartService;
import com.majee.ecommerce.service.OrderService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    @Override
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        // Getting User cart
        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart not found against email "+emailId , HttpStatus.NOT_FOUND);
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found against email "+emailId , HttpStatus.NOT_FOUND));

        // Create a new order with payment info

        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted !");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod , pgPaymentId , pgStatus , pgResponseMessage , pgName);
        payment.setOrder(order);
        paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        // Get items from the cart into the order items
        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems == null || cartItems.isEmpty()){
            throw new APIException("Order has no cartItems");
        }

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setDiscount(cartItem.getDiscount());
                    orderItem.setOrderProductPrice(cartItem.getProductPrice());
                    orderItem.setOrder(savedOrder);

                    return orderItem;
                }).toList();

        orderItems = orderItemRepository.saveAll(orderItems);


        // Update product stock
        cart.getCartItems().forEach(cartItem -> {
            int quantity = cartItem.getQuantity();
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            // Clear the cart
            cartService.deleteProductFromCart(cart.getCartId() , cartItem.getProduct().getProductId());
        });

        // Send back the order summary
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item ->
                orderDTO.getOrderItems().add(
                        modelMapper.map(item , OrderItemDTO.class)
                ));
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }
}
