package com.eufrat.order_service.controller;

import com.eufrat.order_service.dto.OrderRequest;
import com.eufrat.order_service.helper.TracingUtils;
import com.eufrat.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
public class OrderController {

    private final TracingUtils tracingUtils;
    private final OrderService orderService;

    @PostMapping
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallback")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<ResponseEntity<String>> placeOrder(@RequestBody OrderRequest orderRequest) {
        return tracingUtils.completableFuture(
                () -> ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(orderRequest))
        );
    }

    public CompletableFuture<ResponseEntity<String>> fallback(OrderRequest orderRequest, RuntimeException exception) {
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Oops! Please order later.")
        );
    }

}
