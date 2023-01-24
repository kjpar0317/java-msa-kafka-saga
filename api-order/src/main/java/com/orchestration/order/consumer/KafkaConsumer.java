package com.orchestration.order.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orchestration.common.kafka.KafkaStatus;
import com.orchestration.common.model.OrderDTO;
import com.orchestration.common.utils.ObjectMapperUtils;
import com.orchestration.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {
    private final KafkaTemplate<String, String> template;
    private final OrderRepository repository;

    @KafkaListener(id = "payment", topics = {"order-result-topic"}, groupId = "order-service")
    public void onOrderResultEventReceived(String message) throws JsonProcessingException {
        OrderDTO order = ObjectMapperUtils.getInstance().readValue(message, OrderDTO.class);

        log.info("Order result received {}", order);

        if (KafkaStatus.REJECT == order.getStatus()) {
            order.setStatus(KafkaStatus.ROLLBACK);
            String newMessage = ObjectMapperUtils.getInstance().writeValueAsString(order);
            order.setStatus(KafkaStatus.CANCELED);
            repository.save(order);
            template.send("order-topic", order.getOrderId(), newMessage);

            log.info("Order will be cancelled {}", order);
        } else if (KafkaStatus.ACCEPT == order.getStatus()) {
            OrderDTO currentOrder = repository.findById(order.getOrderId());

            if(KafkaStatus.NEW == currentOrder.getStatus()) {
                order.setStatus(KafkaStatus.WAITING);
                repository.save(order);

                log.info("Order is waiting {}", order);
            } else if(KafkaStatus.WAITING == currentOrder.getStatus()) {
                order.setStatus(KafkaStatus.COMPLETED);
                repository.save(order);
                String newMessage = ObjectMapperUtils.getInstance().writeValueAsString(order);
                template.send("order-topic", order.getOrderId(), newMessage);

                log.info("Order is completed {}", order);
            } else {
                log.info("Order cancelled {}", order);
            }
        }
    }
}
