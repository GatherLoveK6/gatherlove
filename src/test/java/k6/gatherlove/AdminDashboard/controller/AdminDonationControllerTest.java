package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.dto.DonationHistoryResponse;
import k6.gatherlove.AdminDashboard.service.AdminDonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDonationController.class)
public class AdminDonationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminDonationService adminDonationService;

    private DonationHistoryResponse mockDonation;

    @BeforeEach
    void setUp() {
        mockDonation = new DonationHistoryResponse(
                1L,
                "Jane Doe",
                "Bantu Sekolah",
                50000,
                "2025-04-10T13:45:00"
        );
    }

    @Test
    void testGetAllDonations() throws Exception {
        when(adminDonationService.getAllDonations()).thenReturn(List.of(mockDonation));

        mockMvc.perform(get("/admin/donations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].donorName").value("Jane Doe"))
                .andExpect(jsonPath("$[0].campaignTitle").value("Bantu Sekolah"))
                .andExpect(jsonPath("$[0].amount").value(50000));
    }
}
