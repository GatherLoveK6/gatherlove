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
        var pm = new PaymentMethod("pm1", "user1", "TYPE");
        when(repo.findById("pm1")).thenReturn(Optional.of(pm));

        assertDoesNotThrow(() -> svc.validatePaymentMethod("user1", "pm1"));
    }

    @Test
    void validatePaymentMethod_notFound() {
        when(repo.findById("x")).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class,
                () -> svc.validatePaymentMethod("user1", "x"));
        assertTrue(ex.getMessage().contains("Payment method not found"));
    }

    @Test
    void validatePaymentMethod_wrongUser() {
        var pm = new PaymentMethod("pm1", "user2", "TYPE");
        when(repo.findById("pm1")).thenReturn(Optional.of(pm));

        var ex = assertThrows(IllegalArgumentException.class,
                () -> svc.validatePaymentMethod("user1", "pm1"));
        assertTrue(ex.getMessage().contains("does not belong to user"));
    }

    @Test
    void getAll_returnsList() {
        var pm1 = new PaymentMethod("p1", "u", "T");
        var pm2 = new PaymentMethod("p2", "u", "T");
        when(repo.findByUserId("u")).thenReturn(List.of(pm1, pm2));

        var out = svc.getAll("u");
        assertEquals(2, out.size());
        assertSame(pm1, out.get(0));
    }

    @Test
    void create_savesAndReturns() {
        var saved = new PaymentMethod("p1", "u", "T");
        when(repo.save(any(PaymentMethod.class))).thenReturn(saved);

        var out = svc.create("u", "p1", "T");
        verify(repo).save(any(PaymentMethod.class));
        assertEquals(saved, out);
    }

    @Test
    void update_changesType() {
        var pm = new PaymentMethod("p1", "u", "OLD");
        when(repo.findById("p1")).thenReturn(Optional.of(pm));
        when(repo.save(pm)).thenReturn(pm);

        var out = svc.update("u", "p1", "NEW");
        assertEquals("NEW", out.getType());
        verify(repo).save(pm);
    }

    @Test
    void update_notFound() {
        when(repo.findById("x")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> svc.update("u", "x", "T"));
    }

    @Test
    void delete_success() {
        var pm = new PaymentMethod("p1", "u", "T");
        when(repo.findById("p1")).thenReturn(Optional.of(pm));

        svc.delete("u", "p1");
        verify(repo).deleteById("p1");
    }

    @Test
    void delete_notFound() {
        when(repo.findById("x")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> svc.delete("u", "x"));
    }
}
