package k6.gatherlove.fundraising.dto;

import k6.gatherlove.fundraising.model.CampaignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CampaignResponseDtoTest {

    @Test
    void builderAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        CampaignResponseDto dto = CampaignResponseDto.builder()
                .id(1L)
                .title("Education Fund")
                .description("Support education for children.")
                .goalAmount(new BigDecimal("1000.00"))
                .currentAmount(new BigDecimal("200.00"))
                .deadline(LocalDate.now().plusDays(30))
                .createdAt(now)
                .updatedAt(now)
                .userId(2L)
                .status(CampaignStatus.ACTIVE)
                .proofFilePath("proofs/file.pdf")
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Education Fund");
        assertThat(dto.getDescription()).isEqualTo("Support education for children.");
        assertThat(dto.getGoalAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(dto.getCurrentAmount()).isEqualTo(new BigDecimal("200.00"));
        assertThat(dto.getDeadline()).isAfterOrEqualTo(LocalDate.now());
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
        assertThat(dto.getUserId()).isEqualTo(2L);
        assertThat(dto.getStatus()).isEqualTo(CampaignStatus.ACTIVE);
        assertThat(dto.getProofFilePath()).isEqualTo("proofs/file.pdf");
    }

    @Test
    void settersWork() {
        CampaignResponseDto dto = new CampaignResponseDto();
        dto.setId(5L);
        dto.setTitle("Health Fund");
        dto.setDescription("Support health for all.");
        dto.setGoalAmount(new BigDecimal("5000.00"));
        dto.setCurrentAmount(new BigDecimal("1000.00"));
        dto.setDeadline(LocalDate.now().plusDays(10));
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setUserId(10L);
        dto.setStatus(CampaignStatus.PENDING_VERIFICATION);
        dto.setProofFilePath("proofs/health.pdf");

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getTitle()).isEqualTo("Health Fund");
        assertThat(dto.getDescription()).isEqualTo("Support health for all.");
        assertThat(dto.getGoalAmount()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(dto.getCurrentAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(dto.getDeadline()).isAfterOrEqualTo(LocalDate.now());
        assertThat(dto.getUserId()).isEqualTo(10L);
        assertThat(dto.getStatus()).isEqualTo(CampaignStatus.PENDING_VERIFICATION);
        assertThat(dto.getProofFilePath()).isEqualTo("proofs/health.pdf");
    }
}