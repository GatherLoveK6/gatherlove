package k6.gatherlove.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k6.gatherlove.domain.Wallet;
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
        // Prepare URL
        String url = "http://localhost:" + port + "/wallet/topup?userId=userX&amount=100&paymentMethodId=pm_test";

        // Make POST request
        ResponseEntity<String> response =
                restTemplate.postForEntity(url, null, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("SUCCESS"));  // We expect "SUCCESS" in transaction JSON
    }

    @Test
    void testWithdrawEndpoint() {
        // 1) First top-up the wallet
        String topupUrl = "http://localhost:" + port + "/wallet/topup?userId=userY&amount=100&paymentMethodId=pm_test";
        restTemplate.postForEntity(topupUrl, null, String.class);

        // 2) Now withdraw
        String withdrawUrl = "http://localhost:" + port + "/wallet/withdraw?userId=userY&amount=50";
        ResponseEntity<String> response =
                restTemplate.postForEntity(withdrawUrl, null, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("SUCCESS"));
    }

    @Test
    void testGetBalanceEndpoint() {
        // 1) top up
        String topupUrl = "http://localhost:" + port + "/wallet/topup?userId=userZ&amount=200&paymentMethodId=pm_test";
        restTemplate.postForEntity(topupUrl, null, String.class);

        // 2) get wallet
        String getUrl = "http://localhost:" + port + "/wallet/balance?userId=userZ";
        ResponseEntity<Wallet> response =
                restTemplate.getForEntity(getUrl, Wallet.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Wallet wallet = response.getBody();
        assertNotNull(wallet);
        assertEquals(BigDecimal.valueOf(200), wallet.getBalance());
    }
}
