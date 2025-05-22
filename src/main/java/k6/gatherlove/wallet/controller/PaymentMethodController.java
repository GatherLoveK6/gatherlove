package k6.gatherlove.wallet.controller;

import k6.gatherlove.wallet.domain.PaymentMethod;
import k6.gatherlove.wallet.dto.PaymentMethodDto;
import k6.gatherlove.wallet.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/users/{userId}/payment-methods")
public class PaymentMethodController {
    private final PaymentMethodService svc;

    @Autowired
    public PaymentMethodController(PaymentMethodService svc) {
        this.svc = svc;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<PaymentMethod>> create(
            @PathVariable String userId,
            @RequestBody PaymentMethodDto body
    ) {
        UriComponentsBuilder base = ServletUriComponentsBuilder.fromCurrentRequest();
        return svc.createAsync(userId, body.getPaymentMethodId(), body.getType())
                .thenApply(pm -> {
                    URI location = base
                            .path("/{pmId}")
                            .buildAndExpand(pm.getPaymentMethodId())
                            .toUri();
                    return ResponseEntity.created(location).body(pm);
                });
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<PaymentMethod>>> list(
            @PathVariable String userId
    ) {
        return svc.getAllAsync(userId)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{pmId}")
    public CompletableFuture<ResponseEntity<PaymentMethod>> update(
            @PathVariable String userId,
            @PathVariable String pmId,
            @RequestBody PaymentMethodDto body
    ) {
        return svc.updateAsync(userId, pmId, body.getType())
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{pmId}")
    public CompletableFuture<ResponseEntity<Void>> delete(
            @PathVariable String userId,
            @PathVariable String pmId
    ) {
        return svc.deleteAsync(userId, pmId)
                .thenApply(v -> ResponseEntity.noContent().build());
    }
}
