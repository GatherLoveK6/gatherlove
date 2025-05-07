// WalletControllerTest.java

package k6.gatherlove.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k6.gatherlove.domain.Transaction;
import k6.gatherlove.domain.TransactionType;
import k6.gatherlove.domain.Wallet;
import k6.gatherlove.service.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired MockMvc mvc;
    @MockBean WalletService svc;
    @Autowired ObjectMapper mapper;

    @Test
    void topUpShouldReturn201WithLocation() throws Exception {
        var tx = new Transaction("tx-1","bob",TransactionType.TOP_UP,BigDecimal.valueOf(100));
        tx.markCompleted();
        when(svc.topUp(eq("bob"), eq(BigDecimal.valueOf(100)), eq("pm-1"))).thenReturn(tx);

        mvc.perform(post("/users/bob/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                  {
                    "amount":100,
                    "paymentMethodId":"pm-1"
                  }
                  """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "http://localhost/users/bob/wallet/topup/transactions/tx-1"
                ))
                .andExpect(jsonPath("$.transactionId").value("tx-1"))
                .andExpect(jsonPath("$.amount").value(100));
    }

    @Test
    void withdrawShouldReturn201() throws Exception {
        var tx = new Transaction("tx-2","bob",TransactionType.WITHDRAW,BigDecimal.valueOf(50));
        tx.markCompleted();
        when(svc.withdraw("bob", BigDecimal.valueOf(50))).thenReturn(tx);

        mvc.perform(post("/users/bob/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                  { "amount":50 }
                  """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("tx-2"))
                .andExpect(jsonPath("$.type").value("WITHDRAW"));
    }

    @Test
    void getBalanceShouldReturn200AndWallet() throws Exception {
        var wallet = new Wallet("w-1","bob");
        when(svc.getWallet("bob")).thenReturn(wallet);

        mvc.perform(get("/users/bob/wallet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("bob"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void transactionHistoryShouldReturn200AndJsonArray() throws Exception {
        var tx1 = new Transaction("tx-1","bob",TransactionType.TOP_UP,BigDecimal.valueOf(100));
        when(svc.getTransactions("bob")).thenReturn(List.of(tx1));

        mvc.perform(get("/users/bob/wallet/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value("tx-1"))
                .andExpect(jsonPath("$[0].amount").value(100));
    }
}
