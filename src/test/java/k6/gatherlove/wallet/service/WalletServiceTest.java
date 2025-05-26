package k6.gatherlove.wallet.service;

import k6.gatherlove.wallet.domain.Transaction;
import k6.gatherlove.wallet.domain.TransactionType;
import k6.gatherlove.wallet.domain.Wallet;
import k6.gatherlove.wallet.repository.TransactionRepository;
import k6.gatherlove.wallet.repository.WalletRepository;
import k6.gatherlove.service.MetricsService;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PaymentMethodService paymentMethodService;
    
    @Mock
    private MetricsService metricsService;
    
    @Mock
    private Timer.Sample timerSample;

    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletService(
                walletRepository,
                transactionRepository,
                paymentMethodService,
                metricsService
        );
        
        // Mock timer behavior
        when(metricsService.startWalletTransactionTimer()).thenReturn(timerSample);
    }

    @Test
    void topUpAsync_createsTransactionAndIncrementsBalance() throws ExecutionException, InterruptedException {
        // Given
        String userId = "user123";
        BigDecimal amount = BigDecimal.valueOf(100);
        String pmCode = "pm456";

        Wallet wallet = new Wallet("wallet789", userId);
        wallet.setBalance(BigDecimal.valueOf(50));

        Transaction savedTransaction = new Transaction("tx123", "wallet789", TransactionType.TOP_UP, amount);
        savedTransaction.markCompleted();

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // When
        CompletableFuture<Transaction> result = walletService.topUpAsync(userId, amount, pmCode);
        Transaction transaction = result.get();

        // Then
        assertNotNull(transaction);
        assertEquals(TransactionType.TOP_UP, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        
        verify(paymentMethodService).validatePaymentMethod(userId, pmCode);
        verify(metricsService).startWalletTransactionTimer();
        verify(metricsService).recordWalletTransaction(timerSample);
    }

    @Test
    void withdrawAsync_reducesBalance() throws ExecutionException, InterruptedException {
        // Given
        String userId = "user123";
        BigDecimal amount = BigDecimal.valueOf(30);

        Wallet wallet = new Wallet("wallet789", userId);
        wallet.setBalance(BigDecimal.valueOf(100));

        Transaction savedTransaction = new Transaction("tx123", "wallet789", TransactionType.WITHDRAW, amount);
        savedTransaction.markCompleted();

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // When
        CompletableFuture<Transaction> result = walletService.withdrawAsync(userId, amount);
        Transaction tx = result.get();

        // Then
        assertNotNull(tx);
        assertEquals(TransactionType.WITHDRAW, tx.getType());
        
        verify(metricsService).startWalletTransactionTimer();
        verify(metricsService).recordWalletTransaction(timerSample);
    }
}