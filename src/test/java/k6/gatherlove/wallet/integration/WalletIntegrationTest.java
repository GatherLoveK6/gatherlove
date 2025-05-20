package k6.gatherlove.wallet.integration;

import k6.gatherlove.wallet.domain.PaymentMethod;
import k6.gatherlove.wallet.domain.Transaction;
import k6.gatherlove.wallet.domain.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WalletIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void fullFlow() {
        // always send JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1) create a new payment-method
        HttpEntity<Map<String,String>> pmReq = new HttpEntity<>(
                Map.of("paymentMethodId","pm-1","type","CC"),
                headers
        );
        ResponseEntity<PaymentMethod> pmResp = rest.exchange(
                "/users/bob/payment-methods",
                HttpMethod.POST,
                pmReq,
                PaymentMethod.class
        );
        assertThat(pmResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(pmResp.getBody().getPaymentMethodId()).isEqualTo("pm-1");

        // 2) top-up 200
        HttpEntity<Map<String,Object>> topUpReq = new HttpEntity<>(
                Map.of("amount", BigDecimal.valueOf(200),
                        "paymentMethodId","pm-1"),
                headers
        );
        ResponseEntity<Transaction> txResp = rest.exchange(
                "/users/bob/wallet/topup",
                HttpMethod.POST,
                topUpReq,
                Transaction.class
        );
        assertThat(txResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(txResp.getBody().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(200));

        // 3) fetch wallet and assert balance
        ResponseEntity<Wallet> wResp = rest.getForEntity(
                "/users/bob/wallet",
                Wallet.class
        );
        assertThat(wResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(wResp.getBody().getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(200));
    }
}
