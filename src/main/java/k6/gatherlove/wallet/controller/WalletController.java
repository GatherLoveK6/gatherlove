package k6.gatherlove.wallet.controller;

import k6.gatherlove.wallet.domain.Transaction;
import k6.gatherlove.wallet.domain.Wallet;
import k6.gatherlove.wallet.dto.TopUpRequest;
import k6.gatherlove.wallet.dto.WithdrawRequest;
import k6.gatherlove.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/users/{userId}/wallet")
public class WalletController {
    private final WalletService svc;

    @Autowired
    public WalletController(WalletService svc) {
        this.svc = svc;
    }

    @PostMapping("/topup")
    public CompletableFuture<ResponseEntity<Transaction>> topUp(
            @PathVariable String userId,
            @RequestBody TopUpRequest body
    ) {
        UriComponentsBuilder base = ServletUriComponentsBuilder.fromCurrentRequest();

        return svc.topUpAsync(userId, body.getAmount(), body.getPaymentMethodId())
                .thenApply(tx -> {
                    URI location = base
                            .path("/transactions/{txId}")
                            .buildAndExpand(tx.getTransactionId())
                            .toUri();
                    return ResponseEntity.created(location).body(tx);
                });
    }

    @PostMapping("/withdraw")
    public CompletableFuture<ResponseEntity<Transaction>> withdraw(
            @PathVariable String userId,
            @RequestBody WithdrawRequest body
    ) {
        UriComponentsBuilder base = ServletUriComponentsBuilder.fromCurrentRequest();

        return svc.withdrawAsync(userId, body.getAmount())
                .thenApply(tx -> {
                    URI location = base
                            .path("/transactions/{txId}")
                            .buildAndExpand(tx.getTransactionId())
                            .toUri();
                    return ResponseEntity.created(location).body(tx);
                });
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<Wallet>> getWallet(@PathVariable String userId) {
        return svc.getWalletAsync(userId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/transactions")
    public CompletableFuture<ResponseEntity<List<Transaction>>> transactionHistory(
            @PathVariable String userId
    ) {
        return svc.getTransactionsAsync(userId)
                .thenApply(ResponseEntity::ok);
    }
}