package k6.gatherlove.domain;

public class PaymentMethod {
    private String paymentMethodId;
    private String userId;
    private String type;  // E.g., "CreditCard", "OVO", "GoPay", etc.
    private boolean active;

    public PaymentMethod(String paymentMethodId, String userId, String type) {
        this.paymentMethodId = paymentMethodId;
        this.userId = userId;
        this.type = type;
        this.active = true;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    // Getters (and setters if needed)
    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }
}

