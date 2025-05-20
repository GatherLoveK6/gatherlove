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

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/wallet")
public class WalletController {
    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/topup")
    public ResponseEntity<Transaction> topUp(
            @PathVariable String userId,
            @RequestBody TopUpRequest body
    ) {
        Transaction tx = walletService.topUp(userId, body.getAmount(), body.getPaymentMethodId());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/transactions/{txId}")
                .buildAndExpand(tx.getTransactionId())
                .toUri();

        return ResponseEntity.created(location).body(tx);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdraw(
            @PathVariable String userId,
            @RequestBody WithdrawRequest body
    ) {
        Transaction tx = walletService.withdraw(userId, body.getAmount());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/transactions/{txId}")
                .buildAndExpand(tx.getTransactionId())
                .toUri();

        return ResponseEntity.created(location).body(tx);
    }

    @GetMapping
    public ResponseEntity<Wallet> getWallet(@PathVariable String userId) {
        return ResponseEntity.ok(walletService.getWallet(userId));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> transactionHistory(@PathVariable String userId) {
        return ResponseEntity.ok(walletService.getTransactions(userId));
    }
}
