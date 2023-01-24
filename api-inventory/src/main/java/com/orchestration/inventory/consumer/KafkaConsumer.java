package com.orchestration.inventory.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orchestration.common.kafka.KafkaStatus;
import com.orchestration.common.model.OrderDTO;
import com.orchestration.common.utils.ObjectMapperUtils;
import com.orchestration.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {
//    private final KafkaTemplate<String, String> template;
    private final InventoryService service;

    @KafkaListener(id = "order-topic", topics = {"order-topic"}, groupId = "inventory-service")
    public void onOrderEventReceived(String message) throws JsonProcessingException {
        OrderDTO order = ObjectMapperUtils.getInstance().readValue(message, OrderDTO.class);

        log.info("Order received {}", order);

        if(KafkaStatus.NEW == order.getStatus()) {
            service.reserveInventory(order);
        } else {
            service.confirmInventory(order);
        }
    }
}
