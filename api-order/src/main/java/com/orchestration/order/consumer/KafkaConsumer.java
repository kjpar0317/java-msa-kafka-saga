package com.orchestration.order.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orchestration.common.kafka.KafkaStep;
import com.orchestration.common.model.OrderDTO;
import com.orchestration.common.utils.ObjectMapperUtils;
import com.orchestration.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

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

        if (KafkaStep.REJECT.equals(order.getOrderStatus())) {
            order.setOrderStatus(KafkaStep.ROLLBACK);
            String newMessage = ObjectMapperUtils.getInstance().writeValueAsString(order);
            order.setOrderStatus(KafkaStep.CANCELED);
            repository.save(order);
            template.send("order-topic", order.getOrderId(), newMessage);

            log.info("Order will be cancelled {}", order);
        } else if (KafkaStep.ACCEPT.equals(order.getOrderStatus())) {
            OrderDTO currentOrder = repository.findById(order.getOrderId());

            if(KafkaStep.NEW.equals(currentOrder.getOrderStatus())) {
                order.setOrderStatus(KafkaStep.WAITING);
                repository.save(order);

                log.info("Order is waiting {}", order);
            } else if(KafkaStep.WAITING.equals(currentOrder.getOrderStatus())) {
                order.setOrderStatus(KafkaStep.COMPLETED);
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
