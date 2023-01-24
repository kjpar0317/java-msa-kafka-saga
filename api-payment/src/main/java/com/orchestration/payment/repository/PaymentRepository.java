package com.orchestration.payment.repository;

import com.orchestration.payment.model.PaymentDTO;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PaymentRepository {
    private Map<String, PaymentDTO> paymentMap = new HashMap<>();

    public PaymentDTO findById(String id) {
        return !ObjectUtils.isEmpty(paymentMap.get(id)) ? paymentMap.get(id) : new PaymentDTO();
    }

    public void save(PaymentDTO payment) {
        paymentMap.put(payment.getId(), payment);
    }

    public List<PaymentDTO> findAll() {
        return new ArrayList<>(paymentMap.values());
    }
}
