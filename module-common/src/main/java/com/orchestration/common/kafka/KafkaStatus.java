package com.orchestration.common.kafka;

public enum KafkaStatus {
    NEW,
    ACCEPT,
    WAITING,
    COMPLETED,
    REJECT,
    ROLLBACK,
    CANCELED
}
