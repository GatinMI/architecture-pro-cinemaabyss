package com.cinemaabyss.events.controller;

import com.cinemaabyss.events.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public EventController(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/health")
    public Mono<Map<String, Boolean>> health() {
        return Mono.just(Map.of("status", true));
    }

    @PostMapping("/movie")
    public Mono<ResponseEntity<EventResponse>> createMovieEvent(@RequestBody MovieEvent movieEvent) {
        Event event = new Event("movie", movieEvent);
        return sendEvent("movie-events", event);
    }

    @PostMapping("/user")
    public Mono<ResponseEntity<EventResponse>> createUserEvent(@RequestBody UserEvent userEvent) {
        Event event = new Event("user", userEvent);
        return sendEvent("user-events", event);
    }

    @PostMapping("/payment")
    public Mono<ResponseEntity<EventResponse>> createPaymentEvent(@RequestBody PaymentEvent paymentEvent) {
        Event event = new Event("payment", paymentEvent);
        return sendEvent("payment-events", event);
    }

    private Mono<ResponseEntity<EventResponse>> sendEvent(String topic, Event event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, event.getId(), json);

            return Mono.fromFuture(future)
                    .map(result -> {
                        int partition = result.getRecordMetadata().partition();
                        long offset = result.getRecordMetadata().offset();
                        log.info("Event sent to topic={}, partition={}, offset={}, id={}", topic, partition, offset, event.getId());
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(new EventResponse("success", partition, offset, event));
                    })
                    .onErrorResume(e -> {
                        log.error("Failed to send event to topic={}: {}", topic, e.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new EventResponse("error", 0, 0L, event)));
                    });
        } catch (Exception e) {
            log.error("Error serializing event: {}", e.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EventResponse("error", 0, 0L, event)));
        }
    }
}
