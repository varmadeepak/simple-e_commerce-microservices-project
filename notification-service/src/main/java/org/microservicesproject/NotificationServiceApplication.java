package org.microservicesproject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
@EnableKafka
public class NotificationServiceApplication {
    public static void main(String[] args){
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
//    @KafkaListener(id = "notificationId",topics = "notificationTopic")
//    public void handleNotification(OrderPlacedEvent orderPlacedEvent){
//        //send an email notification
//        log.info("Kafka Event Received: {}",orderPlacedEvent.getOrderNumber());
//    }
}