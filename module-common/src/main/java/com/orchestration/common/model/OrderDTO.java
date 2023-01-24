package com.orchestration.common.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private String orderId;
    private String userId;
    private String productId;
    private String orderStatus;
    private String srcService;
    private Double productPrice;
}
