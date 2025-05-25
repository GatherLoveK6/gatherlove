package k6.gatherlove.donation.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RestCampaignClient implements CampaignClient {

    private final RestTemplate restTemplate;

    /**
     * Base URL for the fundraising service. If the property
     * "fundraising.service.url" is not set, this will default
     * to "http://localhost:8080".
     */
    @Value("${fundraising.service.url:http://localhost:8080}")
    private String baseUrl;

    public RestCampaignClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Notify the fundraising service of a successful donation.
     *
     * @param campaignId ID of the campaign
     * @param donationId ID of the donation record
     * @param amount     donation amount
     */
    @Override
    public void recordDonation(String campaignId, String donationId, double amount) {
        String url = String.format("%s/api/fundraising/campaigns/%s/donations", baseUrl, campaignId);

        Map<String, Object> payload = Map.of(
                "donationId", donationId,
                "amount",     amount
        );

        restTemplate.postForEntity(url, payload, Void.class);
    }

    /**
     * Notify the fundraising service to remove (cancel) a donation.
     *
     * @param campaignId ID of the campaign
     * @param donationId ID of the donation to cancel
     */
    @Override
    public void removeDonation(String campaignId, String donationId) {
        String url = String.format("%s/api/fundraising/campaigns/%s/donations/%s",
                baseUrl, campaignId, donationId);

        restTemplate.delete(url);
    }
}
