package com.eufrat.order_service.dto;

import java.math.BigDecimal;

public record OrderLineItemsDto(Long id, String skuCode, BigDecimal prize, Integer quantity) {
}
