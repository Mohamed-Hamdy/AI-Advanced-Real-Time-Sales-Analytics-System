package com.analytics.salessystem.Service;

import com.analytics.salessystem.Repository.OrderRepository;
import com.analytics.salessystem.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository repository;

    public void addOrder(Order order) {
        order.setDate(LocalDateTime.now());
        repository.save(order);
    }

    public Double getTotalRevenue() {
        return repository.getTotalRevenue();
    }

    public List<Object[]> getTopSellingProducts() {
        return repository.getTopSellingProducts();
    }

    public Long getRecentOrderCount() {
        return repository.countRecentOrders(LocalDateTime.now().minusMinutes(1));
    }

    public Double getRecentRevenue() {
        return repository.getRecentRevenue(LocalDateTime.now().minusMinutes(1));
    }
}
