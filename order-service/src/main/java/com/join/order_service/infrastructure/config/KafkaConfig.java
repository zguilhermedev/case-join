package com.join.order_service.infrastructure.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;

@Configuration
public class KafkaConfig {

    private <T> SenderOptions<Object, Object> getProducerOptions(Class<T> serializerClass) {
        var properties = new HashMap<String, Object>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializerClass);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializerClass);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, Integer.MAX_VALUE);

        return SenderOptions.create(properties).maxInFlight(1024);
    }

    @Bean(name = "jsonReactiveProducer")
    public ReactiveKafkaProducerTemplate<Object, Object> jsonReactiveProducer() {
        return new ReactiveKafkaProducerTemplate<>(getProducerOptions(JsonSerializer.class));
    }

}
