package k6.gatherlove.wallet.controller;

import k6.gatherlove.wallet.domain.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentMethodControllerIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    private final HttpHeaders headers = new HttpHeaders();

    public PaymentMethodControllerIntegrationTest() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    @Test
    void list_update_delete_flow() {
        // 1) CREATE
        HttpEntity<Map<String,String>> createReq =
                new HttpEntity<>(Map.of("paymentMethodId","pm-1","type","CC"), headers);

        ResponseEntity<PaymentMethod> createResp = rest.exchange(
                "/users/{userId}/payment-methods",
                HttpMethod.POST,
                createReq,
                PaymentMethod.class,
                "bob"
        );
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 2) LIST
        ResponseEntity<PaymentMethod[]> listResp = rest.exchange(
                "/users/{userId}/payment-methods",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PaymentMethod[].class,
                "bob"
        );
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        PaymentMethod[] methods = listResp.getBody();
        assertThat(methods).hasSize(1);
        assertThat(methods[0].getType()).isEqualTo("CC");

        // 3) UPDATE
        HttpEntity<Map<String,String>> updateReq =
                new HttpEntity<>(Map.of("type","GoPay"), headers);

        ResponseEntity<Void> updateResp = rest.exchange(
                "/users/{userId}/payment-methods/{pmId}",
                HttpMethod.PUT,
                updateReq,
                Void.class,
                "bob", "pm-1"
        );
        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // confirm update
        PaymentMethod updated = rest.exchange(
                "/users/{userId}/payment-methods",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PaymentMethod[].class,
                "bob"
        ).getBody()[0];
        assertThat(updated.getType()).isEqualTo("GoPay");

        // 4) DELETE
        ResponseEntity<Void> deleteResp = rest.exchange(
                "/users/{userId}/payment-methods/{pmId}",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class,
                "bob", "pm-1"
        );
        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // confirm empty
        PaymentMethod[] after = rest.exchange(
                "/users/{userId}/payment-methods",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PaymentMethod[].class,
                "bob"
        ).getBody();
        assertThat(after).isEmpty();
    }
}