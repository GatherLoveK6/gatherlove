package k6.gatherlove.wallet.service;

import k6.gatherlove.wallet.domain.Transaction;
import k6.gatherlove.wallet.domain.TransactionType;
import k6.gatherlove.wallet.domain.Wallet;
import k6.gatherlove.wallet.repository.TransactionRepository;
import k6.gatherlove.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Transaction topUp(String userId, BigDecimal amount, String paymentMethodId) {
        // 1) Validate payment method
        paymentMethodService.validatePaymentMethod(userId, paymentMethodId);

        // 2) Load or create wallet
        Optional<Wallet> optWallet = walletRepo.findByUserId(userId);
        Wallet wallet = optWallet.orElseGet(() -> new Wallet(UUID.randomUUID().toString(), userId));

        // 3) Perform top-up
        wallet.topUp(amount);
        walletRepo.save(wallet);

        // 4) Record transaction (note: storing walletId, not userId)
        Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                wallet.getWalletId(),
                TransactionType.TOP_UP,
                amount
        );
        tx.markCompleted();
        transactionRepo.save(tx);

        return tx;
    }

    public Transaction withdraw(String userId, BigDecimal amount) {
        // 1) Load wallet
        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Wallet not found for user: " + userId));

        // 2) Check funds
        if (!wallet.canWithdraw(amount)) {
            throw new IllegalArgumentException("Insufficient funds for user: " + userId);
        }

        // 3) Perform withdrawal
        wallet.withdraw(amount);
        walletRepo.save(wallet);

        // 4) Record transaction
        Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                wallet.getWalletId(),
                TransactionType.WITHDRAW,
                amount
        );
        tx.markCompleted();
        transactionRepo.save(tx);

        return tx;
    }

    public Wallet getWallet(String userId) {
        return walletRepo.findByUserId(userId)
                .orElse(null);
    }

    public List<Transaction> getTransactions(String userId) {
        // fetch the wallet first, then query by walletId
        return walletRepo.findByUserId(userId)
                .map(w -> transactionRepo.findByWalletId(w.getWalletId()))
                .orElse(List.of());
    }
}
