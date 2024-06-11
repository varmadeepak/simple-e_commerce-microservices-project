package com.microservicesproject.order_service.controller;

import com.microservicesproject.order_service.dto.OrderRequest;
import com.microservicesproject.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RequestMapping("/api/order")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @CircuitBreaker(name = "inventory", fallbackMethod = "placeOrderFallback")
//    @TimeLimiter(name = "inventory") // same as used in properties "circuitbreaker.instances.inventory"
//    @Retry(name = "inventory")
    public String placeOrder(@RequestBody OrderRequest orderRequest){
        return orderService.placeOrder(orderRequest);
    }
    public CompletableFuture<String> placeOrderFallback(OrderRequest orderRequest,RuntimeException rex){
        return CompletableFuture.supplyAsync(() ->"Something Went Wrong while placing order, Please try again later");
    }
}
