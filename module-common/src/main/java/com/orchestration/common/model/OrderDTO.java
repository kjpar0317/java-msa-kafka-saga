package com.orchestration.common.model;

import com.orchestration.common.kafka.KafkaStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private String orderId;
    private String userId;
    private String productId;
    private String srcService;
    private Double productPrice;
    private KafkaStatus status;
}
