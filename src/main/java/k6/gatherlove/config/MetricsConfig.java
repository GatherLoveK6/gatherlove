package k6.gatherlove.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter campaignCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("campaigns_created_total")
                .description("Total number of campaigns created")
                .register(meterRegistry);
    }

    @Bean
    public Counter donationCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("donations_created_total")
                .description("Total number of donations made")
                .register(meterRegistry);
    }

    @Bean
    public Counter userRegistrationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("users_registered_total")
                .description("Total number of users registered")
                .register(meterRegistry);
    }

    @Bean
    public Timer walletTransactionTimer(MeterRegistry meterRegistry) {
        return Timer.builder("wallet_transaction_duration")
                .description("Time taken for wallet transactions")
                .register(meterRegistry);
    }

    @Bean
    public Timer campaignProcessingTimer(MeterRegistry meterRegistry) {
        return Timer.builder("campaign_processing_duration")
                .description("Time taken to process campaign operations")
                .register(meterRegistry);
    }
}
