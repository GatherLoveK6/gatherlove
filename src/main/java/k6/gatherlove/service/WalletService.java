package k6.gatherlove.service;

import k6.gatherlove.domain.Transaction;
import k6.gatherlove.domain.TransactionType;
import k6.gatherlove.domain.Wallet;
import k6.gatherlove.repository.TransactionRepository;
import k6.gatherlove.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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
        // validate (stubbed) payment method
        paymentMethodService.validatePaymentMethod(userId, paymentMethodId);

        // load or create wallet
        Wallet wallet = walletRepo.findByUserId(userId);
        if (wallet == null) {
            wallet = new Wallet(UUID.randomUUID().toString(), userId);
        }

        // perform top-up
        wallet.topUp(amount);
        walletRepo.save(wallet);

        // record transaction under userId (not walletId)
        Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                userId,
                TransactionType.TOP_UP,
                amount
        );
        tx.markCompleted();
        transactionRepo.save(tx);

        return tx;
    }

    public Transaction withdraw(String userId, BigDecimal amount) {
        Wallet wallet = walletRepo.findByUserId(userId);
        if (wallet == null || !wallet.canWithdraw(amount)) {
            throw new IllegalArgumentException("Insufficient funds or wallet not found for user: " + userId);
        }

        // perform withdrawal
        wallet.withdraw(amount);
        walletRepo.save(wallet);

        // record transaction under userId
        Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                userId,
                TransactionType.WITHDRAW,
                amount
        );
        tx.markCompleted();
        transactionRepo.save(tx);

        return tx;
    }

    public Wallet getWallet(String userId) {
        return walletRepo.findByUserId(userId);
    }

    public List<Transaction> getTransactions(String userId) {
        return transactionRepo.findByUserId(userId);
    }
}