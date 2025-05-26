package k6.gatherlove.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import k6.gatherlove.auth.repository.UserRepository;
import k6.gatherlove.donation.repository.DonationRepository;
import k6.gatherlove.fundraising.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;
    
    // Repositories for gauge metrics
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final DonationRepository donationRepository;

    private Counter campaignCreatedCounter;
    private Counter donationCreatedCounter;
    private Counter userRegistrationCounter;
    private Timer walletTransactionTimer;
    private Timer campaignProcessingTimer;

    @PostConstruct
    public void setupMetrics() {
        // Initialize counters
        campaignCreatedCounter = Counter.builder("campaigns_total")
                .description("Total number of campaigns created")
                .register(meterRegistry);

        donationCreatedCounter = Counter.builder("donations_total")
                .description("Total number of donations made")
                .register(meterRegistry);

        userRegistrationCounter = Counter.builder("users_registered_total")
                .description("Total number of users registered")
                .register(meterRegistry);

        // Initialize timers
        walletTransactionTimer = Timer.builder("wallet_transaction_duration_seconds")
                .description("Time taken for wallet transactions")
                .register(meterRegistry);

        campaignProcessingTimer = Timer.builder("campaign_processing_duration_seconds")
                .description("Time taken to process campaign operations")
                .register(meterRegistry);

        // Register gauges for real-time metrics
        Gauge.builder("users_active", this::getTotalUsers)
                .description("Total number of active users")
                .register(meterRegistry);

        Gauge.builder("campaigns_active", this::getTotalCampaigns)
                .description("Total number of active campaigns")
                .register(meterRegistry);

        Gauge.builder("donations_active", this::getTotalDonations)
                .description("Total number of donations")
                .register(meterRegistry);
    }

    public void incrementCampaignCreated() {
        campaignCreatedCounter.increment();
    }

    public void incrementDonationCreated() {
        donationCreatedCounter.increment();
    }

    public void incrementUserRegistration() {
        userRegistrationCounter.increment();
    }

    public Timer.Sample startWalletTransactionTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordWalletTransaction(Timer.Sample sample) {
        sample.stop(walletTransactionTimer);
    }

    public Timer.Sample startCampaignProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordCampaignProcessing(Timer.Sample sample) {
        sample.stop(campaignProcessingTimer);
    }

    private Double getTotalUsers() {
        return (double) userRepository.count();
    }

    private Double getTotalCampaigns() {
        return (double) campaignRepository.count();
    }

    private Double getTotalDonations() {
        return (double) donationRepository.count();
    }
}
