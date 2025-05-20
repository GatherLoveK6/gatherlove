package k6.gatherlove.wallet.repository;

import k6.gatherlove.wallet.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByWalletId(String walletId);
}
