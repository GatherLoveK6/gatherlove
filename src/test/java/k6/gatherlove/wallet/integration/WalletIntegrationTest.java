package k6.gatherlove.wallet.integration;

import k6.gatherlove.wallet.domain.PaymentMethod;
import k6.gatherlove.wallet.domain.Transaction;
import k6.gatherlove.wallet.domain.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WalletIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    @Test
    void fullFlow() {
        // 1) add a payment method
        var resp1 = rest.postForEntity(
                "/users/bob/payment-methods",
                Map.of("paymentMethodId","pm-1","type","CC"),
                PaymentMethod.class
        );
        assertThat(resp1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 2) top-up
        var txResp = rest.postForEntity(
                "/users/bob/wallet/topup",
                Map.of("amount",200,"paymentMethodId","pm-1"),
                Transaction.class
        );
        assertThat(txResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(txResp.getBody().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(200));

        // 3) balance should be 200
        var wallet = rest.getForObject("/users/bob/wallet", Wallet.class);
        assertThat(wallet.getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(200));
    }
}
