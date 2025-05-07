package k6.gatherlove.repository;

import k6.gatherlove.domain.PaymentMethod;
import java.util.List;

public interface PaymentMethodRepository {
    List<PaymentMethod> findByUserId(String userId);
    PaymentMethod findById(String paymentMethodId);
    void save(PaymentMethod pm);
    void delete(String paymentMethodId);
}