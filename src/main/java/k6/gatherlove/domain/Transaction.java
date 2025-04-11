package k6.gatherlove.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String walletId;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String status; // e.g., "SUCCESS", "FAILED"

    public Transaction(String transactionId, String walletId, TransactionType type, BigDecimal amount) {
        this.transactionId = transactionId;
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.transactionDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public void markCompleted() {
        this.status = "SUCCESS";
    }

    // Getters (and setters if needed)
    public String getTransactionId() {
        return transactionId;
    }

    public String getWalletId() {
        return walletId;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", walletId='" + walletId + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}

