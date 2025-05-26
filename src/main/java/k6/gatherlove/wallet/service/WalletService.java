package k6.gatherlove.wallet.service;

import k6.gatherlove.wallet.domain.Transaction;
import k6.gatherlove.wallet.domain.TransactionType;
import k6.gatherlove.wallet.domain.Wallet;
import k6.gatherlove.wallet.repository.TransactionRepository;
import k6.gatherlove.wallet.repository.WalletRepository;
import k6.gatherlove.service.MetricsService;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class WalletService {
    private final WalletRepository walletRepo;
    private final TransactionRepository txRepo;
    private final PaymentMethodService pmService;
    private final MetricsService metricsService;

    @Autowired
    public WalletService(WalletRepository walletRepo,
                        TransactionRepository txRepo,
                        PaymentMethodService pmService,
                        MetricsService metricsService) {
        this.walletRepo = walletRepo;
        this.txRepo = txRepo;
        this.pmService = pmService;
        this.metricsService = metricsService;
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Transaction> topUpAsync(String userId, BigDecimal amount, String pmCode) {
        Timer.Sample sample = metricsService.startWalletTransactionTimer();
        
        try {
            pmService.validatePaymentMethod(userId, pmCode);
            
            Wallet wallet = getOrCreateWallet(userId);
            wallet.topUp(amount);
            walletRepo.save(wallet);

            Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                wallet.getWalletId(),
                TransactionType.TOP_UP,
                amount
            );
            tx.markCompleted();
            Transaction saved = txRepo.save(tx);
            
            return CompletableFuture.completedFuture(saved);
        } finally {
            metricsService.recordWalletTransaction(sample);
        }
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Transaction> withdrawAsync(String userId, BigDecimal amount) {
        Timer.Sample sample = metricsService.startWalletTransactionTimer();
        
        try {
            Wallet wallet = getOrCreateWallet(userId);
            wallet.withdraw(amount);
            walletRepo.save(wallet);

            Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                wallet.getWalletId(),
                TransactionType.WITHDRAW,
                amount
            );
            tx.markCompleted();
            Transaction saved = txRepo.save(tx);
            
            return CompletableFuture.completedFuture(saved);
        } finally {
            metricsService.recordWalletTransaction(sample);
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<Wallet> getWalletAsync(String userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return CompletableFuture.completedFuture(wallet);
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Transaction>> getTransactionsAsync(String userId) {
        Wallet wallet = getOrCreateWallet(userId);
        List<Transaction> transactions = txRepo.findByWalletId(wallet.getWalletId());
        return CompletableFuture.completedFuture(transactions);
    }

    private Wallet getOrCreateWallet(String userId) {
        return walletRepo.findByUserId(userId).orElseGet(() -> {
            Wallet newWallet = new Wallet(UUID.randomUUID().toString(), userId);
            return walletRepo.save(newWallet);
        });
    }
}