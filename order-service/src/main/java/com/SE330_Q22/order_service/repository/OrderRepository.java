package com.SE330_Q22.order_service.repository;

import com.SE330_Q22.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>
{
    List<Order> findByUserId(Long userId);
}
