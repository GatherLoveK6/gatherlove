package k6.gatherlove.wallet.repository;

import k6.gatherlove.wallet.domain.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentMethodRepository
        extends JpaRepository<PaymentMethod, UUID> {

    List<PaymentMethod> findByUserId(String userId);

    long countByUserId(String userId);

    Optional<PaymentMethod> findByUserIdAndPaymentMethodId(
            String userId, String paymentMethodId
    );
}