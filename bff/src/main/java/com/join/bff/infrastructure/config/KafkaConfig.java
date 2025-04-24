package com.join.bff.infrastructure.config;

import com.join.bff.application.dto.request.OrderCreationDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConfig {

    private Map<String, Object> getProperties() {
        var properties = new HashMap<String, Object>();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 3600000);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "bff-application");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        properties.put(JsonDeserializer.KEY_DEFAULT_TYPE, Object.class);
        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderCreationDTO.class);
        properties.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return properties;
    }

    private ReceiverOptions<Object, OrderCreationDTO> getReceiverOptions(List<String> topics) {
        var properties = getProperties();
        return ReceiverOptions.<Object, OrderCreationDTO>create(properties)
                .subscription(topics);
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<Object, OrderCreationDTO> reversalConsumerTemplate() {
        return new ReactiveKafkaConsumerTemplate<>(getReceiverOptions(List.of("order-created")));
    }

}
