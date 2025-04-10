package k6.gatherlove.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodDomainTest {

    @Test
    void testPaymentMethodActivationDeactivation() {
        PaymentMethod paymentMethod = new PaymentMethod("pm001", "user1", "CreditCard");
        assertTrue(paymentMethod.isActive(), "New payment method should be active.");

        paymentMethod.deactivate();
        assertFalse(paymentMethod.isActive(), "Payment method should be inactive after deactivation.");

        paymentMethod.activate();
        assertTrue(paymentMethod.isActive(), "Payment method should be active after reactivation.");
    }
}
