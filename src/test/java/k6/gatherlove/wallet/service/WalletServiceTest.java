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

    @Mock
    private WalletRepository walletRepo;

    @Mock
    private TransactionRepository transactionRepo;

    @Mock
    private PaymentMethodRepository paymentMethodRepo;

    private PaymentMethodService paymentMethodService;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        paymentMethodService = new PaymentMethodService(paymentMethodRepo);
        walletService = new WalletService(walletRepo, transactionRepo, paymentMethodService);
    }

    @Test
    void topUp_createsTransactionAndIncrementsBalance() {
        String userId = "user1";
        String pmId   = "pm1";
        BigDecimal amount = BigDecimal.valueOf(100);

        when(paymentMethodRepo.findById(pmId))
                .thenReturn(Optional.of(new PaymentMethod(pmId, userId, "CREDIT_CARD")));
        when(walletRepo.findByUserId(userId))
                .thenReturn(Optional.empty());

        var walletCaptor = ArgumentCaptor.forClass(Wallet.class);

        Transaction tx = walletService.topUp(userId, amount, pmId);

        assertEquals("SUCCESS", tx.getStatus());
        verify(walletRepo).save(walletCaptor.capture());
        assertEquals(0,
                walletCaptor.getValue().getBalance().compareTo(amount),
                "Saldo harus sama dengan jumlah top-up");
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    void withdraw_reducesBalance() {
        String userId = "user2";
        BigDecimal initial     = BigDecimal.valueOf(200);
        BigDecimal withdrawAmt = BigDecimal.valueOf(50);

        Wallet existing = new Wallet("w2", userId);
        existing.topUp(initial);
        when(walletRepo.findByUserId(userId))
                .thenReturn(Optional.of(existing));

        Transaction tx = walletService.withdraw(userId, withdrawAmt);

        assertEquals("SUCCESS", tx.getStatus());
        assertEquals(0,
                existing.getBalance().compareTo(BigDecimal.valueOf(150)),
                "Saldo tersisa harus 150");
        verify(transactionRepo).save(any(Transaction.class));
        verify(walletRepo).save(existing);
    }

    @Test
    void withdraw_insufficientFunds_throws() {
        String userId = "user3";
        when(walletRepo.findByUserId(userId))
                .thenReturn(Optional.of(new Wallet("w3", userId)));

        assertThrows(IllegalArgumentException.class,
                () -> walletService.withdraw(userId, BigDecimal.valueOf(10)));

        verify(transactionRepo, never()).save(any());
        verify(walletRepo, never()).save(any());
    }
}
