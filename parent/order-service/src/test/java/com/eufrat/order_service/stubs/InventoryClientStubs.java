package com.eufrat.order_service.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class InventoryClientStubs {
    public static void stubInventoryCall(String skuCode) {
        stubFor(get("/api/inventory?skuCode" + skuCode).willReturn(aResponse().withStatus(200)));
    }
}
