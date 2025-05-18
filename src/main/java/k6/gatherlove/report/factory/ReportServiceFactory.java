package k6.gatherlove.report.factory;

import k6.gatherlove.report.service.AdminReportServiceImpl;
import k6.gatherlove.report.service.ReportService;
import k6.gatherlove.report.service.UserReportServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class ReportServiceFactory {

    private final UserReportServiceImpl userReportServiceImpl;
    private final AdminReportServiceImpl adminReportActionImpl;

    public ReportServiceFactory(UserReportServiceImpl userReportServiceImpl, AdminReportServiceImpl adminReportActionImpl) {
        this.userReportServiceImpl = userReportServiceImpl;
        this.adminReportActionImpl = adminReportActionImpl;
    }

    public ReportService getReportAction(String role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        switch (role.toUpperCase()) {
            case "ADMIN":
                return adminReportActionImpl;
            case "USER":
                return userReportServiceImpl;
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }
}
