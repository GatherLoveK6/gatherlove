package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.DonationHistoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDonationServiceImpl implements AdminDonationService {

    @Override
    public List<DonationHistoryResponse> getAllDonations() {
        return List.of(
                new DonationHistoryResponse(
                        1L,
                        "Jane Doe",
                        "Bantu Sekolah",
                        50000,
                        "2025-04-10T13:45:00"
                ),
                new DonationHistoryResponse(
                        2L,
                        "John Smith",
                        "Bantu Panti Jompo",
                        75000,
                        "2025-04-11T09:20:00"
                )
        );
    }
}
