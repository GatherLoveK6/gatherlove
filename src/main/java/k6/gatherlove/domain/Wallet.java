package k6.gatherlove.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Wallet {
    private String walletId;
    private String userId;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Wallet(String walletId, String userId) {
        this.walletId = walletId;
        this.userId = userId;
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canWithdraw(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    public void topUp(BigDecimal amount) {
        balance = balance.add(amount);
        updatedAt = LocalDateTime.now();
    }

    public void withdraw(BigDecimal amount) {
        if (!canWithdraw(amount)) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        balance = balance.subtract(amount);
        updatedAt = LocalDateTime.now();
    }

    // Getters (and setters if needed)
    public String getWalletId() {
        return walletId;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
