package k6.gatherlove.service;

import k6.gatherlove.domain.PaymentMethod;
import k6.gatherlove.repository.PaymentMethodRepository;
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
        System.out.println("Validating payment method " + paymentMethodId + " for user " + userId);
    }

    public List<PaymentMethod> getAll(String userId) {
        return repo.findByUserId(userId);
    }

    public PaymentMethod create(String userId, String pmId, String type) {
        PaymentMethod pm = new PaymentMethod(pmId, userId, type);
        repo.save(pm);
        return pm;
    }

    public PaymentMethod update(String userId, String pmId, String newType) {
        PaymentMethod pm = repo.findById(pmId);
        if (pm == null || !pm.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not found");
        }
        pm.setType(newType);
        repo.save(pm);
        return pm;
    }

    public void delete(String userId, String pmId) {
        PaymentMethod pm = repo.findById(pmId);
        if (pm == null || !pm.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not found");
        }
        repo.delete(pmId);
    }
}