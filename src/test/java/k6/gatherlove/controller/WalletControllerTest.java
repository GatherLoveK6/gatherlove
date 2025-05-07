package k6.gatherlove.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k6.gatherlove.domain.Wallet;
import k6.gatherlove.domain.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WalletControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testTopUpEndpoint() {
        String url = "http://localhost:" + port + "/wallet/topup?userId=userX&amount=100&paymentMethodId=pm_test";
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("SUCCESS"));
    }

    @Test
    void testWithdrawEndpoint() {
        String topupUrl = "http://localhost:" + port + "/wallet/topup?userId=userY&amount=100&paymentMethodId=pm_test";
        restTemplate.postForEntity(topupUrl, null, String.class);
        String withdrawUrl = "http://localhost:" + port + "/wallet/withdraw?userId=userY&amount=50";
        ResponseEntity<String> response = restTemplate.postForEntity(withdrawUrl, null, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("SUCCESS"));
    }

    @Test
    void testGetBalanceEndpoint() {
        String topupUrl = "http://localhost:" + port + "/wallet/topup?userId=userZ&amount=200&paymentMethodId=pm_test";
        restTemplate.postForEntity(topupUrl, null, String.class);
        String getUrl = "http://localhost:" + port + "/wallet/balance?userId=userZ";
        ResponseEntity<Wallet> response = restTemplate.getForEntity(getUrl, Wallet.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Wallet wallet = response.getBody();
        assertEquals(BigDecimal.valueOf(200), wallet.getBalance());
    }

    @Test
    void testGetTransactionsEndpoint() {
        restTemplate.postForEntity(
                "http://localhost:" + port + "/wallet/topup?userId=joe&amount=30&paymentMethodId=pm1",
                null, String.class);
        ResponseEntity<Transaction[]> resp = restTemplate.getForEntity(
                "http://localhost:" + port + "/wallet/transactions?userId=joe",
                Transaction[].class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        Transaction[] list = resp.getBody();
        assertTrue(list.length >= 1);
        assertEquals(new BigDecimal("30"), list[0].getAmount());
    }
}
