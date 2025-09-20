package com.eufrat.order_service.service;

import com.eufrat.order_service.client.InventoryClient;
import com.eufrat.order_service.dto.InventoryResponse;
import com.eufrat.order_service.dto.OrderLineItemsDto;
import com.eufrat.order_service.dto.OrderRequest;
import com.eufrat.order_service.event.OrderPlacedEvent;
import com.eufrat.order_service.model.Order;
import com.eufrat.order_service.model.OrderLineItems;
import com.eufrat.order_service.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final InventoryClient inventoryClient;

    public String placeOrder(OrderRequest orderRequest) {
        log.info("Try place order");
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.orderLineItemsDtos().stream().map(this::mapToDto).toList();
        order.setOrderLineItemsList(orderLineItems);

        // check
        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        InventoryResponse[] inventoryResponses = inventoryClient.isInStock(skuCodes);

        boolean inStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if (inStock) {
            orderRepository.save(order);
            kafkaTemplate.send("notification-topic", new OrderPlacedEvent(order.getOrderNumber()));
            return "Order placed successfully";
        }

        throw new IllegalArgumentException("Product not in stock. Please try again later");
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrize(orderLineItemsDto.prize());
        orderLineItems.setQuantity(orderLineItemsDto.quantity());
        orderLineItems.setSkuCode(orderLineItemsDto.skuCode());
        return orderLineItems;
    }
}
