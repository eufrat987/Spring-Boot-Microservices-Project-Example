package com.eufrat;

import com.eufrat.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics = "notification-topic")
    public void handleNotification(OrderPlacedEvent orderPlacedEvent) {
        log.info("received event " + orderPlacedEvent.getOrderNumber());
    }

}