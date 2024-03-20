package com.codepole.repository;

import com.codepole.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findAllByOrderUserIdAndOrderDateBetween(UUID userId, Instant dateAfter, Instant dateBefore);

}
