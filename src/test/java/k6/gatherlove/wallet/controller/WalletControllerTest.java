package k6.gatherlove.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import k6.gatherlove.wallet.domain.Transaction;
import k6.gatherlove.wallet.domain.TransactionType;
import k6.gatherlove.wallet.domain.Wallet;
import k6.gatherlove.wallet.dto.TopUpRequest;
import k6.gatherlove.wallet.dto.WithdrawRequest;
import k6.gatherlove.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private WalletService svc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void topUpShouldReturn201WithLocation() throws Exception {
        // Arrange: stub the async service
        Transaction tx = new Transaction("tx-1", "bob", TransactionType.TOP_UP, BigDecimal.valueOf(100));
        tx.markCompleted();
        when(svc.topUpAsync(eq("bob"), eq(BigDecimal.valueOf(100)), eq("pm-1")))
                .thenReturn(CompletableFuture.completedFuture(tx));

        // Prepare request body
        TopUpRequest req = new TopUpRequest();
        req.setAmount(BigDecimal.valueOf(100));
        req.setPaymentMethodId("pm-1");
        String json = mapper.writeValueAsString(req);

        // Act: fire request, assert async started
        MvcResult start = mvc.perform(post("/users/bob/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Dispatch and assert final response
        mvc.perform(asyncDispatch(start))
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
        Transaction tx = new Transaction("tx-2", "bob", TransactionType.WITHDRAW, BigDecimal.valueOf(50));
        tx.markCompleted();
        when(svc.withdrawAsync("bob", BigDecimal.valueOf(50)))
                .thenReturn(CompletableFuture.completedFuture(tx));

        WithdrawRequest req = new WithdrawRequest();
        req.setAmount(BigDecimal.valueOf(50));
        String json = mapper.writeValueAsString(req);

        MvcResult start = mvc.perform(post("/users/bob/wallet/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(start))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("tx-2"))
                .andExpect(jsonPath("$.type").value("WITHDRAW"));
    }

    @Test
    void getBalanceShouldReturn200AndWallet() throws Exception {
        Wallet w = new Wallet("w-1","bob");
        when(svc.getWalletAsync("bob"))
                .thenReturn(CompletableFuture.completedFuture(w));

        MvcResult start = mvc.perform(get("/users/bob/wallet"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(start))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("bob"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void transactionHistoryShouldReturn200AndJsonArray() throws Exception {
        Transaction tx1 = new Transaction("tx-1","bob",TransactionType.TOP_UP,BigDecimal.valueOf(100));
        when(svc.getTransactionsAsync("bob"))
                .thenReturn(CompletableFuture.completedFuture(List.of(tx1)));

        MvcResult start = mvc.perform(get("/users/bob/wallet/transactions"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(start))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value("tx-1"))
                .andExpect(jsonPath("$[0].amount").value(100));
    }
}