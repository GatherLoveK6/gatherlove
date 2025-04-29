package k6.gatherlove.repository;

import k6.gatherlove.domain.Transaction;
import k6.gatherlove.domain.TransactionType;
import k6.gatherlove.domain.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    private WalletRepository walletRepo;
    private TransactionRepository transactionRepo;

    @BeforeEach
    void setUp() {
        walletRepo = new InMemoryWalletRepository();
        transactionRepo = new InMemoryTransactionRepository();
    }

    @Test
    void testWalletRepositorySaveAndFind() {
        Wallet wallet = new Wallet("wallet1", "user1");
        wallet.topUp(BigDecimal.valueOf(100));
        walletRepo.save(wallet);

        Wallet retrieved = walletRepo.findByUserId("user1");
        assertNotNull(retrieved, "Wallet should be found in repository.");
        assertEquals(BigDecimal.valueOf(100), retrieved.getBalance(), "Retrieved wallet balance should be 100.");
    }

    @Test
    void testTransactionRepositorySaveAndFind() {
        String walletId = "wallet1";
        Transaction tx1 = new Transaction(UUID.randomUUID().toString(), walletId, TransactionType.TOP_UP, BigDecimal.valueOf(100));
        tx1.markCompleted();
        Transaction tx2 = new Transaction(UUID.randomUUID().toString(), walletId, TransactionType.WITHDRAW, BigDecimal.valueOf(50));
        tx2.markCompleted();

        transactionRepo.save(tx1);
        transactionRepo.save(tx2);

        List<Transaction> transactions = transactionRepo.findByUserId("wallet1");
        assertNotNull(transactions, "Transactions list should not be null.");
        assertEquals(2, transactions.size(), "There should be two transactions saved for wallet1.");
    }
}
