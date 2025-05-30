package k6.gatherlove.wallet.repository;

import k6.gatherlove.wallet.domain.PaymentMethod;
import k6.gatherlove.wallet.domain.Transaction;
import k6.gatherlove.wallet.domain.TransactionType;
import k6.gatherlove.wallet.domain.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Test
    @DisplayName("WalletRepository: saveAndFlush() → findByUserId()")
    void testWalletRepositorySaveAndFindByUserId() {
        // arrange
        Wallet w = new Wallet("w1", "user1");
        w.topUp(BigDecimal.valueOf(100));
        walletRepository.saveAndFlush(w);

        // act
        Optional<Wallet> found = walletRepository.findByUserId("user1");

        // assert
        assertTrue(found.isPresent());
        assertEquals(0, BigDecimal.valueOf(100)
                .compareTo(found.get().getBalance()));
    }

    @Test
    @DisplayName("TransactionRepository: saveAndFlush() → findByWalletId()")
    void testTransactionRepositorySaveAndFindByWalletId() {
        Wallet w = new Wallet("w2", "user2");
        walletRepository.saveAndFlush(w);

        Transaction tx = new Transaction("tx1", "w2",
                TransactionType.TOP_UP, BigDecimal.valueOf(75));
        transactionRepository.saveAndFlush(tx);

        List<Transaction> txs = transactionRepository.findByWalletId("w2");
        assertEquals(1, txs.size());

        Transaction saved = txs.get(0);
        assertEquals(TransactionType.TOP_UP, saved.getType());
        assertEquals(0, BigDecimal.valueOf(75)
                .compareTo(saved.getAmount()));
        assertEquals("PENDING", saved.getStatus());
    }

    @Test
    @DisplayName("PaymentMethodRepository: saveAndFlush() → findByUserId()")
    void testPaymentMethodRepositorySaveAndFindByUserId() {
        // **Note**: userId first, then paymentMethodId
        PaymentMethod pm = new PaymentMethod("user3", "pm1", "CREDIT_CARD");
        paymentMethodRepository.saveAndFlush(pm);

        List<PaymentMethod> methods = paymentMethodRepository.findByUserId("user3");
        assertEquals(1, methods.size());

        PaymentMethod saved = methods.get(0);
        assertEquals("pm1",         saved.getPaymentMethodId());
        assertEquals("CREDIT_CARD", saved.getType());
        assertTrue(saved.isActive());
    }
}