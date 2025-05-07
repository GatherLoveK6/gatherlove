package k6.gatherlove.controller;

import k6.gatherlove.domain.Transaction;
import k6.gatherlove.domain.Wallet;
import k6.gatherlove.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    private final WalletService walletService;
    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/topup")
    public Transaction topUp(@RequestParam String userId,
                             @RequestParam BigDecimal amount,
                             @RequestParam String paymentMethodId) {
        return walletService.topUp(userId, amount, paymentMethodId);
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestParam String userId,
                                @RequestParam BigDecimal amount) {
        return walletService.withdraw(userId, amount);
    }

    @GetMapping("/balance")
    public Wallet getWallet(@RequestParam String userId) {
        return walletService.getWallet(userId);
    }

    @GetMapping("/transactions")
    public List<Transaction> transactionHistory(@RequestParam String userId) {
        return walletService.getTransactions(userId);
    }
}