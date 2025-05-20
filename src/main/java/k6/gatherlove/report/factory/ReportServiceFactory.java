package k6.gatherlove.report.factory;

import k6.gatherlove.enums.Role;
import k6.gatherlove.report.service.AdminReportServiceImpl;
import k6.gatherlove.report.service.ReportService;
import k6.gatherlove.report.service.UserReportServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class ReportServiceFactory {

    private final UserReportServiceImpl userReportServiceImpl;
    private final AdminReportServiceImpl adminReportServiceImpl;

    public ReportServiceFactory(UserReportServiceImpl userReportServiceImpl, AdminReportServiceImpl adminReportServiceImpl) {
        this.userReportServiceImpl = userReportServiceImpl;
        this.adminReportServiceImpl = adminReportServiceImpl;
    }

    public ReportService getReportAction(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        switch (role) {
            case ADMIN:
                return adminReportServiceImpl;
            case USER:
                return userReportServiceImpl;
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }
}
