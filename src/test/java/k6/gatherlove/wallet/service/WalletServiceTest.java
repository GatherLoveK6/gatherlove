package k6.gatherlove.wallet.service;

import k6.gatherlove.wallet.domain.PaymentMethod;
import k6.gatherlove.wallet.domain.Transaction;
import k6.gatherlove.wallet.domain.Wallet;
import k6.gatherlove.wallet.repository.PaymentMethodRepository;
import k6.gatherlove.wallet.repository.TransactionRepository;
import k6.gatherlove.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock private WalletRepository walletRepo;
    @Mock private TransactionRepository transactionRepo;
    @Mock private PaymentMethodRepository paymentMethodRepo;

    private PaymentMethodService paymentMethodService;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        paymentMethodService = new PaymentMethodService(paymentMethodRepo);
        walletService = new WalletService(walletRepo, transactionRepo, paymentMethodService);
    }

    @Test
    void topUpAsync_createsTransactionAndIncrementsBalance() {
        String userId = "user1";
        String pmId   = "pm1";
        BigDecimal amount = BigDecimal.valueOf(100);

        // stub validate step (composite lookup)
        when(paymentMethodRepo.findByUserIdAndPaymentMethodId(userId, pmId))
                .thenReturn(Optional.of(new PaymentMethod(userId, pmId, "CREDIT_CARD")));
        when(walletRepo.findByUserId(userId))
                .thenReturn(Optional.empty());

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);

        Transaction tx = walletService
                .topUpAsync(userId, amount, pmId)
                .join();

        assertEquals("SUCCESS", tx.getStatus());
        verify(walletRepo).save(walletCaptor.capture());
        assertEquals(0,
                walletCaptor.getValue().getBalance().compareTo(amount),
                "Balance should equal top-up amount");
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    void withdrawAsync_reducesBalance() {
        String userId = "user2";
        Wallet existing = new Wallet("w2", userId);
        existing.topUp(BigDecimal.valueOf(200));

        when(walletRepo.findByUserId(userId))
                .thenReturn(Optional.of(existing));

        Transaction tx = walletService
                .withdrawAsync(userId, BigDecimal.valueOf(50))
                .join();

        assertEquals("SUCCESS", tx.getStatus());
        assertEquals(0,
                existing.getBalance().compareTo(BigDecimal.valueOf(150)),
                "Remaining balance should be 150");
        verify(transactionRepo).save(any(Transaction.class));
        verify(walletRepo).save(existing);
    }

    @Test
    void withdrawAsync_insufficientFunds_throws() {
        String userId = "user3";
        when(walletRepo.findByUserId(userId))
                .thenReturn(Optional.of(new Wallet("w3", userId)));

        assertThrows(IllegalArgumentException.class,
                () -> walletService.withdrawAsync(userId, BigDecimal.valueOf(10)).join());

        verify(transactionRepo, never()).save(any());
        verify(walletRepo, never()).save(any());
    }
}
