package com.eufrat.order_service.dto;

import java.util.ArrayList;
import java.util.List;

public record OrderRequest(List<OrderLineItemsDto> orderLineItemsDtos) {
    public OrderRequest(List<OrderLineItemsDto> orderLineItemsDtos) {
        this.orderLineItemsDtos = List.copyOf(orderLineItemsDtos);
    }
}
