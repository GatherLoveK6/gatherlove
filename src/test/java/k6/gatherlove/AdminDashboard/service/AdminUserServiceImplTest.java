package k6.gatherlove.AdminDashboard.service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminUserServiceImplTest {

    private final AdminUserServiceImpl service = new AdminUserServiceImpl();

    @Test
    void testDeleteUserById() {
        // Setup to capture console output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        Long userId = 123L;
        service.deleteUserById(userId);

        // Restore original System.out
        System.setOut(originalOut);

        String output = outputStream.toString().trim();
        assertTrue(output.contains("Deleted user with ID: " + userId));
    }
}
