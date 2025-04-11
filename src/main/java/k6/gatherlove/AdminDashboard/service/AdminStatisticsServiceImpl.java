package k6.gatherlove.AdminDashboard.service;

import k6.gatherlove.AdminDashboard.dto.AdminStatisticsResponse;
import org.springframework.stereotype.Service;

@Service
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    @Override
    public AdminStatisticsResponse getPlatformStatistics() {
        // Dummy values for now
        return new AdminStatisticsResponse(100, 500, 300);
    }
}
