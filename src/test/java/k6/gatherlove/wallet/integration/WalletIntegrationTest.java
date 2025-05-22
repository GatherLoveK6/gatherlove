package k6.gatherlove.wallet.integration;

import k6.gatherlove.wallet.domain.PaymentMethod;
import k6.gatherlove.wallet.domain.Transaction;
import k6.gatherlove.wallet.domain.Wallet;
import k6.gatherlove.wallet.dto.TopUpRequest;
import k6.gatherlove.wallet.repository.PaymentMethodRepository;
import k6.gatherlove.wallet.repository.TransactionRepository;
import k6.gatherlove.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WalletIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired private PaymentMethodRepository pmRepo;
    @Autowired private WalletRepository walletRepo;
    @Autowired private TransactionRepository txRepo;

    private final HttpHeaders headers = new HttpHeaders();

    public WalletIntegrationTest() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    @BeforeEach
    void cleanDatabase() {
        txRepo.deleteAll();
        walletRepo.deleteAll();
        pmRepo.deleteAll();
    }

    @Test
    void fullFlow() {
        // 1) CREATE PAYMENT METHOD
        HttpEntity<Map<String,String>> pmReq = new HttpEntity<>(
                Map.of("paymentMethodId","pm-1","type","CC"),
                headers
        );
        ResponseEntity<PaymentMethod> pmResp = rest.exchange(
                "/users/{userId}/payment-methods",
                HttpMethod.POST,
                pmReq,
                PaymentMethod.class,
                "bob"
        );
        assertThat(pmResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 2) TOP-UP WALLET
        TopUpRequest topUp = new TopUpRequest();
        topUp.setAmount(BigDecimal.valueOf(200));
        topUp.setPaymentMethodId("pm-1");
        HttpEntity<TopUpRequest> topUpReq = new HttpEntity<>(topUp, headers);

        ResponseEntity<Transaction> txResp = rest.exchange(
                "/users/{userId}/wallet/topup",
                HttpMethod.POST,
                topUpReq,
                Transaction.class,
                "bob"
        );
        assertThat(txResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(txResp.getBody().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(200));

        // 3) GET WALLET BALANCE
        ResponseEntity<Wallet> wResp = rest.exchange(
                "/users/{userId}/wallet",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Wallet.class,
                "bob"
        );
        assertThat(wResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(wResp.getBody().getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(200));
    }
}