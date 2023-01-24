package com.orchestration.payment.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orchestration.common.kafka.KafkaStep;
import com.orchestration.common.model.OrderDTO;
import com.orchestration.common.utils.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.orchestration.payment.service.PaymentService;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {
//    private final KafkaTemplate<String, String> template;
    private final PaymentService service;

    @KafkaListener(id = "order-topic", topics = {"order-topic"}, groupId = "payment-service")
    public void onOrderEventReceived(String message) throws JsonProcessingException {
        OrderDTO order = ObjectMapperUtils.getInstance().readValue(message, OrderDTO.class);

        log.info("Order received {}", order);

        if(KafkaStep.NEW.equals(order.getOrderStatus())) {
            service.reservePayment(order);
        } else {
            service.completePayment(order);
        }
    }
}
