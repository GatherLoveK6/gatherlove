package k6.gatherlove.donation.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class RestWalletClient implements WalletClient {
    private final RestTemplate rest;

    public RestWalletClient(RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:8080"))
                .build();
    }

    @Override
    public void deduct(String userId, double amount) {
        rest.postForEntity(
                "/users/{userId}/wallet/withdraw",
                Map.of("amount", BigDecimal.valueOf(amount)),
                Void.class,
                userId
        );
    }

    @Override
    public void refund(String userId, double amount) {
        rest.postForEntity(
                "/users/{userId}/wallet/topup",
                Map.of("amount", BigDecimal.valueOf(amount)),
                Void.class,
                userId
        );
    }
}
