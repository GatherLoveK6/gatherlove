package k6.gatherlove.integration;

import k6.gatherlove.domain.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentMethodControllerIntegrationTest {

    @Autowired TestRestTemplate rest;

    @Test
    void list_update_delete_flow() {
        // 1) Create
        var createResp = rest.postForEntity(
                "/users/bob/payment-methods",
                Map.of("paymentMethodId","pm-1","type","CC"),
                PaymentMethod.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 2) List → GET /users/bob/payment-methods returns JSON array
        var listResp = rest.getForEntity(
                "/users/bob/payment-methods",
                PaymentMethod[].class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        PaymentMethod[] methods = listResp.getBody();
        assertThat(methods).hasSize(1);
        assertThat(methods[0].getType()).isEqualTo("CC");

        // 3) Update → PUT /users/bob/payment-methods/pm-1
        rest.put(
                "/users/bob/payment-methods/pm-1",
                Map.of("type","GoPay"));
        // confirm:
        PaymentMethod updated = rest.getForEntity(
                "/users/bob/payment-methods",
                PaymentMethod[].class).getBody()[0];
        assertThat(updated.getType()).isEqualTo("GoPay");

        // 4) Delete → DELETE /users/bob/payment-methods/pm-1
        rest.delete("/users/bob/payment-methods/pm-1");
        // confirm empty:
        PaymentMethod[] after = rest.getForEntity(
                "/users/bob/payment-methods",
                PaymentMethod[].class).getBody();
        assertThat(after).isEmpty();
    }
}
