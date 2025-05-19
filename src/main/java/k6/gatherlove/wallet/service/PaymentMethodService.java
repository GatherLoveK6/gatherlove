package k6.gatherlove.wallet.service;

import k6.gatherlove.wallet.domain.PaymentMethod;
import k6.gatherlove.wallet.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {
    private final PaymentMethodRepository repo;

    @Autowired
    public PaymentMethodService(PaymentMethodRepository repo) {
        this.repo = repo;
    }

    public void validatePaymentMethod(String userId, String paymentMethodId) {
        PaymentMethod pm = repo.findById(paymentMethodId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Payment method not found: " + paymentMethodId)
                );
        if (!pm.getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "Payment method " + paymentMethodId + " does not belong to user " + userId
            );
        }
    }

    public List<PaymentMethod> getAll(String userId) {
        return repo.findByUserId(userId);
    }

    public PaymentMethod create(String userId, String pmId, String type) {
        PaymentMethod pm = new PaymentMethod(pmId, userId, type);
        return repo.save(pm);
    }

    public PaymentMethod update(String userId, String pmId, String newType) {
        PaymentMethod pm = repo.findById(pmId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Payment method not found: " + pmId)
                );
        if (!pm.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Payment method not found for user: " + userId);
        }
        pm.setType(newType);
        return repo.save(pm);
    }

    public void delete(String userId, String pmId) {
        PaymentMethod pm = repo.findById(pmId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Payment method not found: " + pmId)
                );
        if (!pm.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Payment method not found for user: " + userId);
        }
        repo.deleteById(pmId);
    }
}
