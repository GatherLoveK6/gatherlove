package k6.gatherlove.dto;

public class PaymentMethodDto {
    private String paymentMethodId;
    private String type;

    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}