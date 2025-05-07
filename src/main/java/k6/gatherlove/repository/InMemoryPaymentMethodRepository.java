package k6.gatherlove.repository;

import k6.gatherlove.domain.PaymentMethod;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryPaymentMethodRepository implements PaymentMethodRepository {
    private final Map<String, PaymentMethod> store = new HashMap<>();

    @Override
    public List<PaymentMethod> findByUserId(String userId) {
        return store.values().stream()
                .filter(pm -> pm.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public PaymentMethod findById(String paymentMethodId) {
        return store.get(paymentMethodId);
    }

    @Override
    public void save(PaymentMethod pm) {
        store.put(pm.getPaymentMethodId(), pm);
    }

    @Override
    public void delete(String paymentMethodId) {
        store.remove(paymentMethodId);
    }
}