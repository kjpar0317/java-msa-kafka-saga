package com.orchestration.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orchestration.common.kafka.KafkaStep;
import com.orchestration.common.model.OrderDTO;
import com.orchestration.common.utils.ObjectMapperUtils;
import com.orchestration.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> template;

    public Mono<OrderDTO> createOrder(OrderDTO order) {
        try {
            // New Order Status
            order.setOrderStatus(KafkaStep.NEW);

            String message = ObjectMapperUtils.getInstance().writeValueAsString(order);
            orderRepository.save(order);
            template.send("order-topic", order.getOrderId(), message);
            log.info("Order created {}", order);
            return Mono.just(order);
        } catch(JsonProcessingException e) {
            return null;
        }
    }

    public List<OrderDTO> getOrders() {
        return orderRepository.findAll();
    }
}
