package k6.gatherlove.repository;

import k6.gatherlove.domain.Wallet;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryWalletRepository implements WalletRepository {

    private final Map<String, Wallet> walletMap = new HashMap<>();

    @Override
    public Wallet findByUserId(String userId) {
        return walletMap.get(userId);
    }

    @Override
    public void save(Wallet wallet) {
        walletMap.put(wallet.getUserId(), wallet);
    }
}
