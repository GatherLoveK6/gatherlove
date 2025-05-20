package k6.gatherlove.auth.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class UserResponseTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        UserResponse r = new UserResponse(
                "id123", "Kate", "kate@ex.com", "555", "somewhere"
        );
        assertThat(r.getId()).isEqualTo("id123");
        assertThat(r.getFullName()).isEqualTo("Kate");
        assertThat(r.getEmail()).isEqualTo("kate@ex.com");
        assertThat(r.getPhone()).isEqualTo("555");
        assertThat(r.getAddress()).isEqualTo("somewhere");
    }

    @Test
    void settersAndToStringWork() {
        UserResponse r = new UserResponse();  // now available
        r.setId("X");
        r.setFullName("Y");
        r.setEmail("Z");
        r.setPhone("1");
        r.setAddress("2");

        assertThat(r)
                .hasFieldOrPropertyWithValue("id", "X")
                .hasFieldOrPropertyWithValue("fullName", "Y")
                .hasFieldOrPropertyWithValue("email", "Z")
                .hasFieldOrPropertyWithValue("phone", "1")
                .hasFieldOrPropertyWithValue("address", "2");

        // toString should include all fields
        String repr = r.toString();
        assertThat(repr).contains("X", "Y", "Z", "1", "2");
    }
}
