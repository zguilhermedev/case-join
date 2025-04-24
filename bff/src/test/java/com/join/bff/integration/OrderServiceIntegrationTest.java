package com.join.bff.integration;

import com.join.bff.TestConfig;
import com.join.bff.application.dto.response.OrderDTO;
import com.join.bff.domain.service.OrderService;
import com.join.bff.domain.service.ProductService;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;

@SpringBootTest
@Import(TestConfig.class)
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    private MockWebServer mockOrderServer;
    private MockWebServer mockProductServer;

    @BeforeEach
    void setUp() throws IOException {
        mockOrderServer = new MockWebServer();
        mockOrderServer.start();

        mockProductServer = new MockWebServer();
        mockProductServer.start();

        setServiceUrl(orderService, "baseUrl", mockOrderServer.url("/").toString());
        setServiceUrl(productService, "baseUrl", mockProductServer.url("/").toString());
    }

    private void setServiceUrl(Object service, String fieldName, String url) {
        try {
            Field field = service.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(service, url);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set base URL", e);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        mockOrderServer.shutdown();
        mockProductServer.shutdown();
    }

    @Test
    void findAllOrdersWithProducts_ShouldIntegrateWithServices() {
        String orderResponse = """
            {
                "content": [
                    {
                        "id": 1,
                        "userId": 1,
                        "amount": 100.00,
                        "products": {
                            "1": 2
                        }
                    }
                ],
                "totalElements": 1,
                "number": 0,
                "size": 10
            }
            """;
        mockOrderServer.enqueue(new MockResponse()
                .setBody(orderResponse)
                .addHeader("Content-Type", "application/json"));

        String productResponse = """
            [
                {
                    "id": 1,
                    "name": "Product 1",
                    "value": 50.00
                }
            ]
            """;
        mockProductServer.enqueue(new MockResponse()
                .setBody(productResponse)
                .addHeader("Content-Type", "application/json"));

        Page<OrderDTO> result = orderService.findAllOrdersWithProducts(Optional.empty(), 0, 10);

        Assertions.assertEquals(1, result.getTotalElements());
        OrderDTO orderDTO = result.getContent().get(0);
        Assertions.assertEquals(1L, orderDTO.getId());
        Assertions.assertEquals(1, orderDTO.getProducts().size());
        Assertions.assertEquals("Product 1", orderDTO.getProducts().get(0).getProduct().getName());
    }
}