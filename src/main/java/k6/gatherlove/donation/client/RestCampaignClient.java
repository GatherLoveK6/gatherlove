package k6.gatherlove.donation.client;

import k6.gatherlove.donation.client.CampaignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RestCampaignClient implements CampaignClient {
    private final RestTemplate rest;

    @Value("${fundraising.service.url:http://localhost:8082}")
    private String fundraisingBaseUrl;

    public RestCampaignClient(RestTemplateBuilder builder) {
        this.rest = builder.build();
    }

    @Override
    public void recordDonation(String campaignId, String donationId, double amount) {
        rest.postForEntity(
                fundraisingBaseUrl + "/campaigns/{campaignId}/donations",
                Map.of("donationId", donationId, "amount", amount),
                Void.class,
                campaignId
        );
    }

    @Override
    public void removeDonation(String campaignId, String donationId) {
        rest.delete(
                fundraisingBaseUrl + "/campaigns/{campaignId}/donations/{donationId}",
                campaignId, donationId
        );
    }
}