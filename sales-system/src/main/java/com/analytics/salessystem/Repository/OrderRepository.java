package com.analytics.salessystem.Repository;

import com.analytics.salessystem.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT SUM(o.price * o.quantity) FROM Order o")
    Double getTotalRevenue();

    @Query("""
        SELECT o.productId, SUM(o.quantity) as totalSold 
        FROM Order o 
        GROUP BY o.productId 
        ORDER BY totalSold DESC
    """)
    List<Object[]> getTopSellingProducts();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.date > :timeLimit")
    Long countRecentOrders(@Param("timeLimit") LocalDateTime timeLimit);

    @Query("SELECT SUM(o.price * o.quantity) FROM Order o WHERE o.date > :timeLimit")
    Double getRecentRevenue(@Param("timeLimit") LocalDateTime timeLimit);
}
