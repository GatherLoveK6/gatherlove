package k6.gatherlove.service;

import k6.gatherlove.domain.Transaction;
import k6.gatherlove.domain.TransactionType;
import k6.gatherlove.domain.Wallet;
import k6.gatherlove.repository.TransactionRepository;
import k6.gatherlove.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        // 1. Validate payment method.
        paymentMethodService.validatePaymentMethod(userId, paymentMethodId);

        // 2. Retrieve or create a wallet.
        Wallet wallet = walletRepo.findByUserId(userId);
        if (wallet == null) {
            wallet = new Wallet(UUID.randomUUID().toString(), userId);
        }
        wallet.topUp(amount);

        // 3. Create a transaction record.
        Transaction tx = new Transaction(UUID.randomUUID().toString(), wallet.getWalletId(), TransactionType.TOP_UP, amount);
        tx.markCompleted();

        // 4. Persist changes.
        walletRepo.save(wallet);
        transactionRepo.save(tx);

        return tx;
    }

    public Transaction withdraw(String userId, BigDecimal amount) {
        Wallet wallet = walletRepo.findByUserId(userId);
        if (wallet == null || !wallet.canWithdraw(amount)) {
            throw new IllegalArgumentException("Insufficient funds or wallet not found for user: " + userId);
        }
        wallet.withdraw(amount);
        Transaction tx = new Transaction(UUID.randomUUID().toString(), wallet.getWalletId(), TransactionType.WITHDRAW, amount);
        tx.markCompleted();
        walletRepo.save(wallet);
        transactionRepo.save(tx);
        return tx;
    }

    public Wallet getWallet(String userId) {
        return walletRepo.findByUserId(userId);
    }
}
