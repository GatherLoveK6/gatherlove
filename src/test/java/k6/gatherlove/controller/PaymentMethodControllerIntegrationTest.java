package k6.gatherlove.controller;

import k6.gatherlove.domain.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentMethodControllerIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @LocalServerPort
    private int port;

    private String base() {
        return "http://localhost:" + port + "/wallet/payment-methods";
    }

    @Test
    void list_update_delete_flow() {
        String user = "alice";
        rest.postForEntity(
                base() + "?userId=" + user + "&paymentMethodId=pmA&type=OVO",
                null, Void.class);

        ResponseEntity<PaymentMethod[]> list0 = rest.getForEntity(
                base() + "?userId=" + user, PaymentMethod[].class);
        assertEquals(1, list0.getBody().length);

        HttpEntity<Void> empty = new HttpEntity<>(null);
        ResponseEntity<PaymentMethod> upd = rest.exchange(
                base() + "/pmA?userId=" + user + "&type=GoPay",
                HttpMethod.PUT, empty, PaymentMethod.class);
        assertEquals("GoPay", upd.getBody().getType());

        rest.exchange(
                base() + "/pmA?userId=" + user,
                HttpMethod.DELETE, empty, Void.class);

        ResponseEntity<PaymentMethod[]> list2 = rest.getForEntity(
                base() + "?userId=" + user, PaymentMethod[].class);
        assertEquals(0, list2.getBody().length);
    }
}