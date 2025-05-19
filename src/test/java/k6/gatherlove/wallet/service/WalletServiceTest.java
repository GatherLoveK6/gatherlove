package k6.gatherlove.wallet.service;

import k6.gatherlove.wallet.repository.PaymentMethodRepository;
import k6.gatherlove.wallet.repository.TransactionRepository;
import k6.gatherlove.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletServiceTest {

    private WalletRepository walletRepo;
    private TransactionRepository transactionRepo;
    private PaymentMethodRepository paymentMethodRepo;
    private PaymentMethodService paymentMethodService;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletRepo = new InMemoryWalletRepository();
        transactionRepo = new InMemoryTransactionRepository();
        paymentMethodRepo = new InMemoryPaymentMethodRepository();
        paymentMethodService = new PaymentMethodService(paymentMethodRepo);
        walletService = new WalletService(walletRepo, transactionRepo, paymentMethodService);
    }

    @Test
    void topUp_createsTransactionAndIncrementsBalance() {
        var tx = walletService.topUp("user1", BigDecimal.valueOf(100), "pm1");
        assertEquals("SUCCESS", tx.getStatus());
        assertEquals(BigDecimal.valueOf(100), walletService.getWallet("user1").getBalance());
    }

    @Test
    void withdraw_reducesBalance() {
        walletService.topUp("user2", BigDecimal.valueOf(200), "pm2");
        var tx = walletService.withdraw("user2", BigDecimal.valueOf(50));
        assertEquals("SUCCESS", tx.getStatus());
        assertEquals(BigDecimal.valueOf(150), walletService.getWallet("user2").getBalance());
    }

    @Test
    void withdraw_insufficientFunds_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> walletService.withdraw("user3", BigDecimal.valueOf(10)));
    }
}