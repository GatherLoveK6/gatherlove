package k6.gatherlove.wallet.repository;

import k6.gatherlove.wallet.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByUserId(String userId);
}