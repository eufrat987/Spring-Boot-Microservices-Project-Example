package com.eufrat.order_service.service;

import com.eufrat.order_service.dto.InventoryResponse;
import com.eufrat.order_service.dto.OrderLineItemsDto;
import com.eufrat.order_service.dto.OrderRequest;
import com.eufrat.order_service.model.Order;
import com.eufrat.order_service.model.OrderLineItems;
import com.eufrat.order_service.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtos().stream().map(this::mapToDto).toList();
        order.setOrderLineItemsList(orderLineItems);

        // check
        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        InventoryResponse[] inventoryResponses = webClient.get()
                .uri("http://inventory-service/api/inventory", uriBuilder ->
                        uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve().bodyToMono(InventoryResponse[].class).block();

        boolean inStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if (inStock) {
            orderRepository.save(order);
            return "Order placed successfully";
        }

        throw new IllegalArgumentException("Product not in stock. Please try again later");
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrize(orderLineItemsDto.getPrize());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
