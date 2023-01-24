package com.orchestration.inventory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orchestration.common.kafka.KafkaStatus;
import com.orchestration.common.model.OrderDTO;
import com.orchestration.common.utils.ObjectMapperUtils;
import com.orchestration.inventory.model.InventoryDTO;
import com.orchestration.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class InventoryService {
    private final InventoryRepository repository;
    private final KafkaTemplate<String, String> template;

    public void reserveInventory(OrderDTO order) throws JsonProcessingException {
        InventoryDTO inventory = repository.findById(order.getOrderId());

        if(order.getProductPrice() <= inventory.getAvaliableAmount()) {
            order.setStatus(KafkaStatus.ACCEPT);
            inventory.setReserveAmount(inventory.getReserveAmount() + order.getProductPrice());
            inventory.setAvaliableAmount(inventory.getAvaliableAmount() - order.getProductPrice());
            repository.save(inventory);

            log.info("Inventory accepted {}", inventory);
        } else {
            order.setStatus(KafkaStatus.REJECT);

            log.info("Inventory rejected {}", inventory);
        }

        order.setSrcService("inventory-service");
        String message = ObjectMapperUtils.getInstance().writeValueAsString(order);
        template.send("order-result-topic", order.getOrderId(), message);

        log.info("Inventory reserve operation completed {}", inventory);
    }

    public void confirmInventory(OrderDTO order) {
        InventoryDTO inventory = repository.findById(order.getOrderId());

        if(KafkaStatus.COMPLETED == order.getStatus()) {
            inventory.setReserveAmount(inventory.getReserveAmount() - order.getProductPrice());
            repository.save(inventory);

            log.info("Order complete for inventory {}", inventory);
        } else if(KafkaStatus.ROLLBACK == order.getStatus()) {
            if(inventory.getReserveAmount() != 0) {
                inventory.setReserveAmount(inventory.getReserveAmount() - order.getProductPrice());
                inventory.setAvaliableAmount(inventory.getAvaliableAmount() + order.getProductPrice());
                repository.save(inventory);
            } else {
                log.info("No need to rollback reserved amount {}", inventory);
            }
            log.info("Order rolled back for inventory {}", inventory);
        }
    }
}
