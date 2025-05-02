package k6.gatherlove.service;

import k6.gatherlove.domain.Transaction;
import k6.gatherlove.domain.Wallet;
import k6.gatherlove.repository.InMemoryTransactionRepository;
import k6.gatherlove.repository.InMemoryWalletRepository;
import k6.gatherlove.repository.TransactionRepository;
import k6.gatherlove.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletServiceTest {

    private WalletRepository walletRepo;
    private TransactionRepository transactionRepo;
    private PaymentMethodService paymentMethodService;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletRepo = new InMemoryWalletRepository();
        transactionRepo = new InMemoryTransactionRepository();
        paymentMethodService = new PaymentMethodService();
        walletService = new WalletService(walletRepo, transactionRepo, paymentMethodService);
    }

    @Test
    void testTopUpCreatesWalletIfNull() {
        String userId = "testUser1";
        BigDecimal amount = BigDecimal.valueOf(100);

        // Act
        Transaction tx = walletService.topUp(userId, amount, "pm_001");

        // Assert
        assertNotNull(tx.getTransactionId());
        assertEquals("SUCCESS", tx.getStatus());
        Wallet wallet = walletService.getWallet(userId);
        assertEquals(amount, wallet.getBalance());
    }

    @Test
    void testWithdrawSuccess() {
        String userId = "testUser2";

        // First, top-up with 100
        walletService.topUp(userId, BigDecimal.valueOf(100), "pm_002");

        // Now, withdraw 50
        Transaction tx = walletService.withdraw(userId, BigDecimal.valueOf(50));
        assertNotNull(tx.getTransactionId());
        assertEquals("SUCCESS", tx.getStatus());

        Wallet wallet = walletService.getWallet(userId);
        assertEquals(BigDecimal.valueOf(50), wallet.getBalance());
    }

    @Test
    void testWithdrawInsufficientFunds() {
        String userId = "testUser3";

        // Create wallet with 20
        walletService.topUp(userId, BigDecimal.valueOf(20), "pm_003");

        // Attempt to withdraw 50
        assertThrows(IllegalArgumentException.class,
                () -> walletService.withdraw(userId, BigDecimal.valueOf(50))
        );
    }

    @Test
    void testGetWalletReturnsNullIfNotExists() {
        // No top-up, no wallet
        Wallet wallet = walletService.getWallet("nonExistentUser");
        assertNull(wallet);
    }
}
