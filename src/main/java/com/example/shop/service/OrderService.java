package com.example.shop.service;

import com.example.shop.dto.OrderDto;
import com.example.shop.dto.OrderItemDto;
import com.example.shop.entity.Order;
import com.example.shop.entity.OrderItem;
import com.example.shop.entity.Product;
import com.example.shop.entity.User;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    // ADMIN : toutes les commandes
    @Transactional(readOnly = true)
    public List<OrderDto> findAll() {
        return orderRepo.findAll().stream().map(this::toDto).toList();
    }

    // USER : uniquement ses propres commandes
    @Transactional(readOnly = true)
    public List<OrderDto> findByUsername(String username) {
        return orderRepo.findByUserUsername(username).stream().map(this::toDto).toList();
    }

    // Crée une commande pour l'utilisateur connecté.
    // Un même id de produit répété dans la liste augmente la quantité.
    @Transactional
    public OrderDto createOrder(String username, List<Long> productIds) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (Long productId : productIds) {
            quantities.merge(productId, 1, Integer::sum);
        }

        Order order = new Order();
        order.setUser(user);
        orderRepo.save(order);                       // 1) persister la commande
        for (Map.Entry<Long, Integer> e : quantities.entrySet()) {   // 2) puis chaque item
            Product p = productRepo.findById(e.getKey())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
            OrderItem item = new OrderItem();
            item.setProduct(p);
            item.setQuantity(e.getValue());
            item.setOrder(order);
            order.getItems().add(item);
        }
        return toDto(orderRepo.save(order));
    }

    // Un USER ne peut consulter que ses propres commandes ; un ADMIN peut tout voir.
    @Transactional(readOnly = true)
    public OrderDto getById(Long id, String username, boolean isAdmin) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        boolean isOwner = order.getUser() != null && order.getUser().getUsername().equals(username);
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Access denied");
        }
        return toDto(order);
    }

    @Transactional
    public void delete(Long id) {
        orderRepo.deleteById(id);
    }

    private OrderDto toDto(Order o) {
        return new OrderDto(o.getId(), o.getOrderDate(),
                o.getItems().stream()
                        .map(it -> new OrderItemDto(it.getId(), it.getProduct().getId(), it.getQuantity()))
                        .toList());
    }
}
