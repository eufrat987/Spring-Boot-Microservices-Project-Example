package com.eufrat.order_service.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class InventoryClientStubs {
    public static void stubInventoryCall(WireMockServer server, String skuCode) {
        server.stubFor(get("/api/inventory?skuCode=" + skuCode).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withResponseBody(new Body("""
                                [
                                    {
                                        "skuCode": "iphone_13_blue",
                                        "isInStock": true
                                    }
                                ]
                        """))));
    }
}
