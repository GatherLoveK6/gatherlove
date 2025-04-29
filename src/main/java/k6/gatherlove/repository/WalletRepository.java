package k6.gatherlove.repository;

import k6.gatherlove.domain.Wallet;

public interface WalletRepository {
    Wallet findByUserId(String userId);
    void save(Wallet wallet);
}
