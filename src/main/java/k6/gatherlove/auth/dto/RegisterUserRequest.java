package k6.gatherlove.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import k6.gatherlove.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserRequest {

    @NotBlank
    private String fullName;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // these two were missing before
    private String phone;
    private String address;

    // if you ever want to capture roles from the form (optional)
    private Set<Role> roles;
}
