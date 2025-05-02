package k6.gatherlove.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentMethodService {

    public void validatePaymentMethod(String userId, String paymentMethodId) {
        System.out.println("Validating payment method " + paymentMethodId + " for user " + userId);
        // Simulate validation here; throw an exception if validation fails.
    }
}
