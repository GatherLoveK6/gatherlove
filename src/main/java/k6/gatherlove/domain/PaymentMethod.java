package k6.gatherlove.domain;

public class PaymentMethod {
    private String paymentMethodId;
    private String userId;
    private String type;
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

    public void setType(String newType) {
        this.type = newType;
    }
}