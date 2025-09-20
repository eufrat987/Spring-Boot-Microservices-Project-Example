package com.eufrat.order_service;

import com.eufrat.order_service.stubs.InventoryClientStubs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderServiceApplicationTest {
    @ServiceConnection
    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.36");

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPlaceOrderSuccessfully() throws Exception {
        String request = """
                {
                    "orderLineItemsDtos": [
                        {
                            "skuCode":"iphone_13_blue"
                        }
                    ]
                }
                """;

        InventoryClientStubs.stubInventoryCall("iphone_13_blue");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andExpect(status().isCreated());
    }

}
