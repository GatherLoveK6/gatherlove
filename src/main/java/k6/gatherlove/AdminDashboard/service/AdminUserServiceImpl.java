package k6.gatherlove.AdminDashboard.service;

import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Override
    public void deleteUserById(Long id) {
        System.out.println("Deleted user with ID: " + id);
    }
}
