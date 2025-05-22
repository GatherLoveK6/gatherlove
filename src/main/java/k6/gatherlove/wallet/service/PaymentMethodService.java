package k6.gatherlove.wallet.service;

import k6.gatherlove.wallet.domain.PaymentMethod;
import k6.gatherlove.wallet.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentMethodService {
    private final PaymentMethodRepository repo;

    @Autowired
    public PaymentMethodService(PaymentMethodRepository repo) {
        this.repo = repo;
    }

    // Validator publik untuk WalletService
    public void validatePaymentMethod(String userId, String pmCode) {
        repo.findByUserIdAndPaymentMethodId(userId, pmCode)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment method tidak ditemukan atau bukan milik user ini: " + pmCode
                        )
                );
    }

    @Async("taskExecutor")
    public CompletableFuture<List<PaymentMethod>> getAllAsync(String userId) {
        return CompletableFuture.completedFuture(repo.findByUserId(userId));
    }

    @Async("taskExecutor")
    public CompletableFuture<PaymentMethod> createAsync(
            String userId, String pmCode, String type
    ) {
        // limit 3
        if (repo.countByUserId(userId) >= 3) {
            throw new IllegalArgumentException("Maksimal 3 payment method per user");
        }
        // no dup untuk user yang sama
        if (repo.findByUserIdAndPaymentMethodId(userId, pmCode).isPresent()) {
            throw new IllegalArgumentException(
                    "Payment method '" + pmCode + "' sudah ada untuk user ini"
            );
        }
        PaymentMethod pm = new PaymentMethod(userId, pmCode, type);
        return CompletableFuture.completedFuture(repo.save(pm));
    }

    @Async("taskExecutor")
    public CompletableFuture<PaymentMethod> updateAsync(
            String userId, String pmCode, String newType
    ) {
        validatePaymentMethod(userId, pmCode);
        PaymentMethod pm = repo.findByUserIdAndPaymentMethodId(userId, pmCode).get();
        pm.setType(newType);
        return CompletableFuture.completedFuture(repo.save(pm));
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> deleteAsync(String userId, String pmCode) {
        validatePaymentMethod(userId, pmCode);
        PaymentMethod pm = repo.findByUserIdAndPaymentMethodId(userId, pmCode).get();
        repo.delete(pm);
        return CompletableFuture.completedFuture(null);
    }
}
