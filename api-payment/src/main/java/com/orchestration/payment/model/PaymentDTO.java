package com.orchestration.payment.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class PaymentDTO {
    private String id;
    private String name;
    private Double avaliableAmount = 1200000.00;
    private Double reserveAmount = 0.00;
}
