package k6.gatherlove.fundraising.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignUpdateRequestTest {

    @Test
    void gettersAndSettersWork() {
        CampaignUpdateRequest req = new CampaignUpdateRequest();
        req.setTitle("Education Fund");
        req.setDescription("Raising funds for education.");
        req.setGoalAmount(new BigDecimal("2000.00"));
        req.setDeadline(LocalDate.now().plusDays(30));

        assertThat(req.getTitle()).isEqualTo("Education Fund");
        assertThat(req.getDescription()).isEqualTo("Raising funds for education.");
        assertThat(req.getGoalAmount()).isEqualTo(new BigDecimal("2000.00"));
        assertThat(req.getDeadline()).isEqualTo(LocalDate.now().plusDays(30));
    }
}