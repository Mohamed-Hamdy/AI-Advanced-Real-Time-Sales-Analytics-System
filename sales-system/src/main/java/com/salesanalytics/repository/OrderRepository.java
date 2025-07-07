package com.salesanalytics.repository;

import com.salesanalytics.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT SUM(o.quantity * o.price) FROM Order o")
    Double getTotalRevenue();
    
    @Query("SELECT COUNT(o) FROM Order o")
    Integer getTotalOrderCount();
    
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :since")
    Integer countOrdersSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT o.productName, SUM(o.quantity * o.price) as totalSales, SUM(o.quantity) as totalQuantity " +
           "FROM Order o GROUP BY o.productName ORDER BY totalSales DESC")
    List<Object[]> getTopProductsByRevenue();
    
    @Query("SELECT SUM(o.quantity * o.price) FROM Order o WHERE o.createdAt >= :since")
    Double getRevenueSince(@Param("since") LocalDateTime since);
}