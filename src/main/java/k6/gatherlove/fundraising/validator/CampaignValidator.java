package k6.gatherlove.fundraising.validator;

import k6.gatherlove.fundraising.dto.CampaignCreationRequest;
import k6.gatherlove.fundraising.exception.ValidationException;

import java.time.LocalDate;

public class CampaignValidator {
    public static void validate(CampaignCreationRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title cannot be empty");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new ValidationException("Description cannot be empty");
        }
        if (request.getDescription().trim().length() < 20) {
            throw new ValidationException("Description must be at least 20 characters");
        }
        if (request.getGoalAmount() == null || request.getGoalAmount().signum() <= 0) {
            throw new ValidationException("Goal amount must be greater than zero");
        }
        if (request.getDeadline() == null || request.getDeadline().isBefore(LocalDate.now())) {
            throw new ValidationException("Deadline must be in the future");
        }
    }
}
