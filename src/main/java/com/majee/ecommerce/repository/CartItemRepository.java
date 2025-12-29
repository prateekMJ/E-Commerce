package com.majee.ecommerce.repository;

import com.majee.ecommerce.entity.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = ?2 AND ci.product.productId = ?1")
    CartItem findCartItemByProductIdAndCartId(Long productId, Long cartId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = ?1 AND ci.product.productId = ?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);
}
