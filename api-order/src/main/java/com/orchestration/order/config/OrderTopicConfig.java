package com.orchestration.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class OrderTopicConfig {
    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name("order-topic")
                .partitions(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic orderResultTopic() {
        return TopicBuilder.name("order-result-topic")
                .partitions(1)
                .compact()
                .build();
    }
}
