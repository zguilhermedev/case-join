package com.join.bff.domain.service;

import com.join.bff.application.dto.request.OrderCreationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class NotificationService implements CommandLineRunner {
    private final ReactiveKafkaConsumerTemplate<Object, OrderCreationDTO> consumer;
    private final Sinks.Many<OrderCreationDTO> sink = Sinks.many().replay().latest();

    @Override
    public void run(String... args) throws Exception {
        consumer.receiveAutoAck()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(record -> sink.tryEmitNext(record.value()))
                .subscribe();
    }

    public Flux<String> getOrders() {
        return sink.asFlux().map(o -> "Order: " + o.getUserId());
    }
}
