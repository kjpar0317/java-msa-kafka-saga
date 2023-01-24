package com.orchestration.inventory.repository;

import com.orchestration.inventory.model.InventoryDTO;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InventoryRepository {
    private Map<String, InventoryDTO> inventoryMap = new HashMap<>();

    public InventoryDTO findById(String id) {
        return !ObjectUtils.isEmpty(inventoryMap.get(id)) ? inventoryMap.get(id) : new InventoryDTO();
    }

    public void save(InventoryDTO payment) {
        inventoryMap.put(payment.getId(), payment);
    }

    public List<InventoryDTO> findAll() {
        return new ArrayList<>(inventoryMap.values());
    }
}
