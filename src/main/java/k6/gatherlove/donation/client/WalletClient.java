package k6.gatherlove.donation.client;

public interface WalletClient {
    void deduct(String userId, double amount);
    void refund(String userId, double amount);
}
