package k6.gatherlove.wallet.dto;

import java.math.BigDecimal;

public class TopUpRequest {
    private BigDecimal amount;
    private String paymentMethodId;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }
}