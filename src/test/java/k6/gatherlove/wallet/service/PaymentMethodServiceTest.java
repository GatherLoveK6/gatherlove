package k6.gatherlove.wallet.service;

import k6.gatherlove.wallet.domain.PaymentMethod;
import k6.gatherlove.wallet.repository.PaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository repo;

    private PaymentMethodService svc;

    @BeforeEach
    void setUp() {
        svc = new PaymentMethodService(repo);
    }

    @Test
    void validatePaymentMethod_success() {
        var pm = new PaymentMethod("user1", "pm1", "TYPE");
        when(repo.findByUserIdAndPaymentMethodId("user1", "pm1"))
                .thenReturn(Optional.of(pm));

        assertDoesNotThrow(() ->
                svc.validatePaymentMethod("user1", "pm1")
        );
    }

    @Test
    void validatePaymentMethod_notFound() {
        when(repo.findByUserIdAndPaymentMethodId("user1", "x"))
                .thenReturn(Optional.empty());

        // Just assert that the method throws, no message check
        assertThrows(IllegalArgumentException.class, () ->
                svc.validatePaymentMethod("user1", "x")
        );
    }

    @Test
    void getAllAsync_returnsList() {
        var pm1 = new PaymentMethod("u", "p1", "T");
        var pm2 = new PaymentMethod("u", "p2", "T");
        when(repo.findByUserId("u")).thenReturn(List.of(pm1, pm2));

        List<PaymentMethod> out = svc.getAllAsync("u").join();

        assertEquals(2, out.size());
        assertSame(pm1, out.get(0));
    }

    @Test
    void createAsync_savesAndReturns() {
        var saved = new PaymentMethod("u", "p1", "T");
        when(repo.countByUserId("u")).thenReturn(0L);
        when(repo.findByUserIdAndPaymentMethodId("u", "p1"))
                .thenReturn(Optional.empty());
        when(repo.save(any(PaymentMethod.class))).thenReturn(saved);

        PaymentMethod out = svc.createAsync("u", "p1", "T").join();

        verify(repo).save(any(PaymentMethod.class));
        assertEquals(saved, out);
    }

    @Test
    void createAsync_limitExceeded() {
        when(repo.countByUserId("u")).thenReturn(3L);

        assertThrows(IllegalArgumentException.class, () ->
                svc.createAsync("u", "p1", "T").join()
        );
    }

    @Test
    void updateAsync_changesType() {
        var pm = new PaymentMethod("u", "p1", "OLD");
        when(repo.findByUserIdAndPaymentMethodId("u", "p1"))
                .thenReturn(Optional.of(pm));
        when(repo.save(pm)).thenReturn(pm);

        PaymentMethod out = svc.updateAsync("u", "p1", "NEW").join();

        assertEquals("NEW", out.getType());
        verify(repo).save(pm);
    }

    @Test
    void updateAsync_notFound() {
        when(repo.findByUserIdAndPaymentMethodId("u", "x"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                svc.updateAsync("u", "x", "T").join()
        );
    }

    @Test
    void deleteAsync_success() {
        var pm = new PaymentMethod("u", "p1", "T");
        when(repo.findByUserIdAndPaymentMethodId("u", "p1"))
                .thenReturn(Optional.of(pm));

        svc.deleteAsync("u", "p1").join();

        verify(repo).delete(pm);
    }

    @Test
    void deleteAsync_notFound() {
        when(repo.findByUserIdAndPaymentMethodId("u", "x"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                svc.deleteAsync("u", "x").join()
        );
    }
}