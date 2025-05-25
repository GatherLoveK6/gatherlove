package k6.gatherlove.wallet.service;

import k6.gatherlove.wallet.domain.Transaction;
import k6.gatherlove.wallet.domain.TransactionType;
import k6.gatherlove.wallet.domain.Wallet;
import k6.gatherlove.wallet.repository.TransactionRepository;
import k6.gatherlove.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class WalletService {
    private final WalletRepository walletRepo;
    private final TransactionRepository transactionRepo;
    private final PaymentMethodService paymentMethodService;

    @Autowired
    public WalletService(WalletRepository walletRepo,
                         TransactionRepository transactionRepo,
                         PaymentMethodService paymentMethodService) {
        this.walletRepo = walletRepo;
        this.transactionRepo = transactionRepo;
        this.paymentMethodService = paymentMethodService;
    }

    @Async("taskExecutor")
    public CompletableFuture<Transaction> topUpAsync(String userId, BigDecimal amount, String paymentMethodId) {
        // <-- use the public validator
        paymentMethodService.validatePaymentMethod(userId, paymentMethodId);

        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseGet(() -> new Wallet(UUID.randomUUID().toString(), userId));
        wallet.topUp(amount);
        walletRepo.save(wallet);

        Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                wallet.getWalletId(),
                TransactionType.TOP_UP,
                amount
        );
        tx.markCompleted();
        transactionRepo.save(tx);
        return CompletableFuture.completedFuture(tx);
    }

    @Async("taskExecutor")
    public CompletableFuture<Transaction> withdrawAsync(String userId, BigDecimal amount) {
        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user: " + userId));
        if (!wallet.canWithdraw(amount)) {
            throw new IllegalArgumentException("Insufficient funds for user: " + userId);
        }
        wallet.withdraw(amount);
        walletRepo.save(wallet);

        Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                wallet.getWalletId(),
                TransactionType.WITHDRAW,
                amount
        );
        tx.markCompleted();
        transactionRepo.save(tx);
        return CompletableFuture.completedFuture(tx);
    }

    @Async("taskExecutor")
    public CompletableFuture<Wallet> getWalletAsync(String userId) {
        return CompletableFuture.completedFuture(
                walletRepo.findByUserId(userId).orElse(null)
        );
    }

    @Async("taskExecutor")
    public CompletableFuture<List<Transaction>> getTransactionsAsync(String userId) {
        List<Transaction> txs = walletRepo.findByUserId(userId)
                .map(w -> transactionRepo.findByWalletId(w.getWalletId()))
                .orElse(List.of());
        return CompletableFuture.completedFuture(txs);
    }
}