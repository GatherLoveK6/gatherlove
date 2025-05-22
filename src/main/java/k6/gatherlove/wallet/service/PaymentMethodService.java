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

    /** restore the original public validator so WalletService can call it */
    public void validatePaymentMethod(String userId, String paymentMethodId) {
        PaymentMethod pm = repo.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found: " + paymentMethodId));
        if (!pm.getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "Payment method " + paymentMethodId + " does not belong to user " + userId
            );
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<List<PaymentMethod>> getAllAsync(String userId) {
        return CompletableFuture.completedFuture(repo.findByUserId(userId));
    }

    @Async("taskExecutor")
    public CompletableFuture<PaymentMethod> createAsync(String userId, String pmId, String type) {
        PaymentMethod pm = new PaymentMethod(pmId, userId, type);
        return CompletableFuture.completedFuture(repo.save(pm));
    }

    @Async("taskExecutor")
    public CompletableFuture<PaymentMethod> updateAsync(String userId, String pmId, String newType) {
        PaymentMethod pm = repo.findById(pmId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found: " + pmId));
        validatePaymentMethod(userId, pmId);
        pm.setType(newType);
        return CompletableFuture.completedFuture(repo.save(pm));
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> deleteAsync(String userId, String pmId) {
        validatePaymentMethod(userId, pmId);
        repo.deleteById(pmId);
        return CompletableFuture.completedFuture(null);
    }
}