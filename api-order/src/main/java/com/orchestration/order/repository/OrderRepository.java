package com.orchestration.order.repository;

import com.orchestration.common.model.OrderDTO;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Repository
public class OrderRepository {
    private Map<String, OrderDTO> orderMap = new HashMap<>();

    public OrderDTO findById(String orderId) {
        return !ObjectUtils.isEmpty(orderMap.get(orderId)) ? orderMap.get(orderId) : new OrderDTO();
    }

    public void save(OrderDTO order) {
        orderMap.put(order.getOrderId(), order);
    }

    public List<OrderDTO> findAll() {
        return new ArrayList<>(orderMap.values());
    }
}
