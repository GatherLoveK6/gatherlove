package k6.gatherlove.auth.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String phone;

    @Column(nullable = false)
    private String password;

    private String address;

    @Column(nullable = false)
    private String role; // e.g. "ROLE_USER" or "ROLE_ADMIN"

    // --- UserDetails methods ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return username; // Use username here instead of email
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    // Optional: custom constructor for full user creation
    public User(String id, String fullName, String email, String username,
                String phone, String password, String address, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.role = role;
    }
}
