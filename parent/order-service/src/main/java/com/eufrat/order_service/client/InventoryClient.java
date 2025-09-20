package com.eufrat.order_service.client;

import com.eufrat.order_service.dto.InventoryResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface InventoryClient {

    @GetExchange("/api/inventory")
    InventoryResponse[] isInStock(@RequestParam("skuCode") List<String> skuCodes);

}
