package com.eufrat.product_service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductRequest(String name, String description, BigDecimal price) {

}
