package k6.gatherlove.wallet.controller;

import k6.gatherlove.wallet.domain.PaymentMethod;
import k6.gatherlove.wallet.dto.PaymentMethodDto;
import k6.gatherlove.wallet.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/payment-methods")
public class PaymentMethodController {
    private final PaymentMethodService svc;

    @Autowired
    public PaymentMethodController(PaymentMethodService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<PaymentMethod> create(
            @PathVariable String userId,
            @RequestBody PaymentMethodDto body
    ) {
        PaymentMethod pm = svc.create(userId, body.getPaymentMethodId(), body.getType());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{pmId}")
                .buildAndExpand(pm.getPaymentMethodId())
                .toUri();

        return ResponseEntity.created(location).body(pm);
    }

    @GetMapping
    public ResponseEntity<List<PaymentMethod>> list(@PathVariable String userId) {
        return ResponseEntity.ok(svc.getAll(userId));
    }

    @PutMapping("/{pmId}")
    public ResponseEntity<PaymentMethod> update(
            @PathVariable String userId,
            @PathVariable String pmId,
            @RequestBody PaymentMethodDto body
    ) {
        PaymentMethod updated = svc.update(userId, pmId, body.getType());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{pmId}")
    public ResponseEntity<Void> delete(
            @PathVariable String userId,
            @PathVariable String pmId
    ) {
        svc.delete(userId, pmId);
        return ResponseEntity.noContent().build();
    }
}
