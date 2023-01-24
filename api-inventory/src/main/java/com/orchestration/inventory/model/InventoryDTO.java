package com.orchestration.inventory.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class InventoryDTO {
    private String id;
    private String name;
    private Double avaliableAmount = 1200000.00;
    private Double reserveAmount = 0.00;
}
