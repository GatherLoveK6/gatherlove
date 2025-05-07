package k6.gatherlove.repository;

import k6.gatherlove.domain.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryPaymentMethodRepositoryTest {

    private InMemoryPaymentMethodRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryPaymentMethodRepository();
    }

    @Test
    void save_and_findByUserId_shouldReturnAllMethodsForThatUser() {
        PaymentMethod pm1 = new PaymentMethod("pm-1", "userA", "CreditCard");
        PaymentMethod pm2 = new PaymentMethod("pm-2", "userA", "GoPay");
        PaymentMethod pm3 = new PaymentMethod("pm-3", "userB", "OVO");

        repo.save(pm1);
        repo.save(pm2);
        repo.save(pm3);

        List<PaymentMethod> listA = repo.findByUserId("userA");
        assertEquals(2, listA.size());
        assertTrue(listA.stream().anyMatch(pm -> pm.getPaymentMethodId().equals("pm-1")));
    }

    @Test
    void findById_and_delete_shouldRemoveMethodCorrectly() {
        PaymentMethod pm = new PaymentMethod("pm-42", "user42", "OVO");
        repo.save(pm);
        assertNotNull(repo.findById("pm-42"));

        repo.delete("pm-42");
        assertNull(repo.findById("pm-42"));
    }
}
