package k6.gatherlove.donation.client;

import k6.gatherlove.donation.client.WalletClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RestWalletClient implements WalletClient {
    private final RestTemplate rest;

    // if no property is set, it will fall back to http://localhost:8081
    @Value("${wallet.service.url:http://localhost:8081}")
    private String walletBaseUrl;

    public RestWalletClient(RestTemplateBuilder builder) {
        this.rest = builder.build();
    }

    @Override
    public void deduct(String userId, double amount) {
        rest.postForEntity(
                walletBaseUrl + "/wallets/{userId}/deduct",
                Map.of("amount", amount),
                Void.class,
                userId
        );
    }

    @Override
    public void refund(String userId, double amount) {
        rest.postForEntity(
                walletBaseUrl + "/wallets/{userId}/refund",
                Map.of("amount", amount),
                Void.class,
                userId
        );
    }
}