package com.orchestration.order.controller;

import com.orchestration.common.model.OrderDTO;
import com.orchestration.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("order")
public class OrderController {
    private final OrderService service;

    @PostMapping("/create")
    public Mono<?> createOrder(@RequestBody Mono<OrderDTO> mono) {
        return mono.flatMap(service::createOrder);
    }

    @GetMapping
    public Flux<List<OrderDTO>> getOrders() {
        return Flux.just(service.getOrders());
    }
}
