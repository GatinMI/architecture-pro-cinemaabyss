package com.cinemaabyss.events.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    @KafkaListener(topics = "movie-events", groupId = "events-service-group")
    public void consumeMovieEvent(String message) {
        log.info("Consumed movie event: {}", message);
    }

    @KafkaListener(topics = "user-events", groupId = "events-service-group")
    public void consumeUserEvent(String message) {
        log.info("Consumed user event: {}", message);
    }

    @KafkaListener(topics = "payment-events", groupId = "events-service-group")
    public void consumePaymentEvent(String message) {
        log.info("Consumed payment event: {}", message);
    }
}
