package k6.gatherlove.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor           // ← add this
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
}
