package k6.gatherlove.AdminDashboard.controller;


import k6.gatherlove.AdminDashboard.service.AdminDonationService;
import k6.gatherlove.AdminDashboard.service.AdminDonationServiceImpl;
import k6.gatherlove.donation.repository.DonationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
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

    @Autowired
    private DonationRepository donationRepository;



    @TestConfiguration
    static class TestConfig {
        @Bean
        public DonationRepository donationRepository() {
            return Mockito.mock(DonationRepository.class);
        }

        @Bean
        public AdminDonationService adminDonationService(DonationRepository donationRepository) {
            return new AdminDonationServiceImpl(donationRepository);
        }
    }

    @BeforeEach
    void setUp() {
        when(donationRepository.findAll()).thenReturn(List.of());
    }


    @Test
    void testGetDonationHistories() throws Exception {
        mockMvc.perform(get("/admin/donations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
