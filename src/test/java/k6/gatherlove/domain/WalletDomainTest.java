package k6.gatherlove.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class WalletDomainTest {

    @Test
    void testWalletTopUp() {
        Wallet wallet = new Wallet("wallet1", "user1");
        assertEquals(BigDecimal.ZERO, wallet.getBalance(), "New wallet balance should be zero.");

        wallet.topUp(BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(100), wallet.getBalance(), "After top-up, wallet balance should be 100.");
    }

    @Test
    void testWalletWithdrawSuccess() {
        Wallet wallet = new Wallet("wallet2", "user2");
        wallet.topUp(BigDecimal.valueOf(100));
        assertTrue(wallet.canWithdraw(BigDecimal.valueOf(50)), "Wallet should allow withdrawing 50.");
        wallet.withdraw(BigDecimal.valueOf(50));
        assertEquals(BigDecimal.valueOf(50), wallet.getBalance(), "After withdrawal, balance should be 50.");
    }

    @Test
    void testWalletWithdrawInsufficientFunds() {
        Wallet wallet = new Wallet("wallet3", "user3");
        wallet.topUp(BigDecimal.valueOf(30));
        assertFalse(wallet.canWithdraw(BigDecimal.valueOf(50)), "Wallet should not allow withdrawing 50 from a balance of 30.");
        assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(BigDecimal.valueOf(50)),
                "Attempting to withdraw more than the balance should throw an exception.");
    }
}
