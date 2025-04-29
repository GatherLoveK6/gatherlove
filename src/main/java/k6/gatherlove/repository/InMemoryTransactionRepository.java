package k6.gatherlove.repository;

import k6.gatherlove.domain.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public void save(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> findByUserId(String userId) {
        return transactions.stream()
                .filter(tx -> tx.getWalletId().equals(userId))
                .collect(Collectors.toList());
    }
}
