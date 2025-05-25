package k6.gatherlove.wallet.controller;

import k6.gatherlove.wallet.domain.PaymentMethod;
import k6.gatherlove.wallet.dto.PaymentMethodDto;
import k6.gatherlove.wallet.service.PaymentMethodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentMethodControllerTest {

    private PaymentMethodService service;
    private PaymentMethodController controller;

    @BeforeEach
    void setUp() {
        service = mock(PaymentMethodService.class);
        controller = new PaymentMethodController(service);
    }

    @Test
    void testCreate() {
        String userId = "user1";
        String pmId = "pm1";
        String type = "BANK";

        PaymentMethodDto dto = new PaymentMethodDto();
        dto.setPaymentMethodId(pmId);
        dto.setType(type);

        PaymentMethod mockPM = new PaymentMethod();
        mockPM.setPaymentMethodId(pmId);
        mockPM.setType(type);

        when(service.createAsync(userId, pmId, type))
                .thenReturn(CompletableFuture.completedFuture(mockPM));

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost/users/user1/payment-methods/pm1");


        ResponseEntity<PaymentMethod> response = controller.create(userId, dto, uriBuilder).join();

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().contains(pmId));
        assertEquals(pmId, response.getBody().getPaymentMethodId());
        verify(service).createAsync(userId, pmId, type);
    }

    @Test
    void testList() {
        String userId = "user1";
        List<PaymentMethod> mockList = List.of(new PaymentMethod(), new PaymentMethod());

        when(service.getAllAsync(userId))
                .thenReturn(CompletableFuture.completedFuture(mockList));

        ResponseEntity<List<PaymentMethod>> response = controller.list(userId).join();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(service).getAllAsync(userId);
    }

    @Test
    void testUpdate() {
        String userId = "user1";
        String pmId = "pm1";
        String newType = "E-WALLET";

        PaymentMethodDto dto = new PaymentMethodDto();
        dto.setPaymentMethodId(pmId);
        dto.setType(newType);

        PaymentMethod updatedPM = new PaymentMethod();
        updatedPM.setPaymentMethodId(pmId);
        updatedPM.setType(newType);

        when(service.updateAsync(userId, pmId, newType))
                .thenReturn(CompletableFuture.completedFuture(updatedPM));

        ResponseEntity<PaymentMethod> response = controller.update(userId, pmId, dto).join();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(pmId, response.getBody().getPaymentMethodId());
        assertEquals(newType, response.getBody().getType());
        verify(service).updateAsync(userId, pmId, newType);
    }

    @Test
    void testDelete() {
        String userId = "user1";
        String pmId = "pm1";

        when(service.deleteAsync(userId, pmId))
                .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<Void> response = controller.delete(userId, pmId).join();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(service).deleteAsync(userId, pmId);
    }
}