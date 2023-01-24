package com.orchestration.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orchestration.common.kafka.KafkaStatus;
import com.orchestration.common.model.OrderDTO;
import com.orchestration.common.utils.ObjectMapperUtils;
import com.orchestration.payment.model.PaymentDTO;
import com.orchestration.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> template;

    public void reservePayment(OrderDTO order) throws JsonProcessingException {
        PaymentDTO payment = paymentRepository.findById(order.getOrderId());

        if(order.getProductPrice() <= payment.getAvaliableAmount()) {
            order.setStatus(KafkaStatus.ACCEPT);
            payment.setReserveAmount(payment.getReserveAmount() + order.getProductPrice());
            payment.setAvaliableAmount(payment.getAvaliableAmount() - order.getProductPrice());
            paymentRepository.save(payment);

            log.info("Payment accepted {}", payment);
        } else {
            order.setStatus(KafkaStatus.REJECT);

            log.info("Payment rejected {}", payment);
        }

        order.setSrcService("payment-service");
        String message = ObjectMapperUtils.getInstance().writeValueAsString(order);

        log.info("Send payment message {}", message);

        template.send("order-result-topic", order.getOrderId(), message);

        log.info("Payment reserve operation completed {}", payment);
    }

    public void completePayment(OrderDTO order) {
        PaymentDTO payment = paymentRepository.findById(order.getOrderId());

        if(KafkaStatus.COMPLETED == order.getStatus()) {
            payment.setReserveAmount(payment.getReserveAmount() - order.getProductPrice());
            paymentRepository.save(payment);

            log.info("Order complete for payment {}", payment);
        } else if(KafkaStatus.ROLLBACK == order.getStatus()) {
            if(payment.getReserveAmount() != 0) {
                payment.setReserveAmount(payment.getReserveAmount() - order.getProductPrice());
                payment.setAvaliableAmount(payment.getAvaliableAmount() + order.getProductPrice());
                paymentRepository.save(payment);
            } else {
                log.info("No need to rollback reserved amount {}", payment);
            }
            log.info("Order rolled back for payment {}", payment);
        }
    }
}
