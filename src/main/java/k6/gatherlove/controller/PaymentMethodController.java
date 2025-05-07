package k6.gatherlove.controller;

import k6.gatherlove.domain.PaymentMethod;
import k6.gatherlove.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/wallet/payment-methods")
public class PaymentMethodController {
    private final PaymentMethodService svc;

    @Autowired
    public PaymentMethodController(PaymentMethodService svc) {
        this.svc = svc;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethod create(@RequestParam String userId,
                                @RequestParam String paymentMethodId,
                                @RequestParam String type) {
        return svc.create(userId, paymentMethodId, type);
    }

    @GetMapping
    public List<PaymentMethod> list(@RequestParam String userId) {
        return svc.getAll(userId);
    }

    @PutMapping("/{pmId}")
    public PaymentMethod update(@PathVariable String pmId,
                                @RequestParam String userId,
                                @RequestParam String type) {
        return svc.update(userId, pmId, type);
    }

    @DeleteMapping("/{pmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String pmId,
                       @RequestParam String userId) {
        svc.delete(userId, pmId);
    }
}

