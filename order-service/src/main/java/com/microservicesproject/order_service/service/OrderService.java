package com.microservicesproject.order_service.service;

import com.microservicesproject.order_service.Producer.KafkaProducer;
import com.microservicesproject.order_service.dto.InventoryResponse;
import com.microservicesproject.order_service.dto.OrderLineItemsDto;
import com.microservicesproject.order_service.dto.OrderRequest;
import com.microservicesproject.order_service.event.OrderPlacedEvent;
import com.microservicesproject.order_service.model.Order;
import com.microservicesproject.order_service.model.OrderLineItems;
import com.microservicesproject.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;
    private final KafkaProducer kafkaProducer;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::maptoOrderLineItems)
                .toList();

        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(OrderLineItemsDto::getSkuCode)
                .toList();
        // check if order is present or not using inventory service
        InventoryResponse[] results = webClient
                .build()
                .get()
                .uri("http://inventory-service/api/inventory"
                        ,uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                        .retrieve()
                                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean allOrdersInStock = Arrays.stream(results).allMatch(InventoryResponse::isInStock);

       if(Boolean.TRUE.equals(allOrdersInStock)){
              orderRepository.save(order);
              kafkaProducer.sendMessage(order.getOrderNumber());
              return "Order placed successfully";
       }
       else {
           throw new RuntimeException("Order can not be placed as inventory is not available");
       }
    }

    private OrderLineItems maptoOrderLineItems(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
