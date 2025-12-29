package com.majee.ecommerce.service;

import com.majee.ecommerce.model.OrderDTO;
import jakarta.transaction.Transactional;

public interface OrderService {

    @Transactional
    OrderDTO placeOrder(String emailId, Long orderRequestDTO, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
