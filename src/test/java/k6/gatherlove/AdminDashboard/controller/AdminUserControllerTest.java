package k6.gatherlove.AdminDashboard.controller;

import k6.gatherlove.AdminDashboard.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
public class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminUserService adminUserService;

    @Test
    void testDeleteUserById() throws Exception {
        doNothing().when(adminUserService).deleteUserById(1L);

        mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isNoContent());
    }
}
