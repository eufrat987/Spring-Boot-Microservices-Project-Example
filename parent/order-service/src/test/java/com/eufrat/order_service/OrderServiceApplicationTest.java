package com.eufrat.order_service;

import com.eufrat.order_service.stubs.InventoryClientStubs;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderServiceApplicationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public WebClient.Builder testBuilder() {
            return WebClient.builder();
        }
    }

    @ServiceConnection
    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.36");
    @Container
    private static KafkaContainer kafkaContainer = new KafkaContainer("apache/kafka:4.1.0");
    private static WireMockServer wireMockServer = new WireMockServer(0);

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("rest.inventory.uri", () -> "http://localhost:" + wireMockServer.port());
        registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setUp() {
        wireMockServer.start();
    }

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

        InventoryClientStubs.stubInventoryCall(wireMockServer, "iphone_13_blue");

        var mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andReturn();


        var response = mockMvc.perform(asyncDispatch(mvcResult))
                .andReturn();

        Assertions.assertEquals("Order placed successfully", response.getResponse().getContentAsString());
        Assertions.assertEquals(response.getResponse().getStatus(), HttpStatus.CREATED.value());
    }

}
