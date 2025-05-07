package k6.gatherlove.repository;

import k6.gatherlove.domain.Transaction;
import java.util.List;

public interface TransactionRepository {
    void save(Transaction transaction);
    List<Transaction> findByUserId(String userId);
}