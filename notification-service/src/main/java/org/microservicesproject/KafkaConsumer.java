package org.microservicesproject;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "order_topic", groupId = "notification-group")
    public void consume(String message) {
        System.out.println("Received Order Number: " + message);
    }
}
