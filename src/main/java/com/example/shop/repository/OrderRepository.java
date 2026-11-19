package com.example.shop.repository;

import com.example.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Commandes d'un utilisateur, retrouvées par son username
    List<Order> findByUserUsername(String username);
}
