package k6.gatherlove.fundraising.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for user-related operations specific to the fundraising module.
 * This avoids modifying the auth module which belongs to another team member.
 */
@RestController
@RequestMapping("/api/fundraising/user")
@Slf4j
public class FundraisingUserController {

    /**
     * Get information about the currently authenticated user for fundraising purposes
     * 
     * @return User information including ID and roles
     */
    @GetMapping("/info")
    public ResponseEntity<?> getCurrentUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            log.warn("Unauthenticated user attempted to access user info");
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        Map<String, Object> userInfo = new HashMap<>();
        String userId = auth.getName();
        userInfo.put("id", userId);
        userInfo.put("username", userId); // Using ID as username for compatibility
        userInfo.put("authorities", auth.getAuthorities());
        
        // For fundraising module, also add the numeric ID used internally
        try {
            Long numericId = Long.parseLong(userId);
            userInfo.put("numericId", numericId);
        } catch (NumberFormatException e) {
            // If the ID is not numeric (e.g., it's a UUID or email), create a consistent hash
            userInfo.put("numericId", Math.abs(userId.hashCode()));
        }
        
        log.debug("Returning user info for user: {}", userId);
        return ResponseEntity.ok(userInfo);
    }
    
    /**
     * Check if the current user is authenticated
     * 
     * @return Boolean indicating authentication status
     */
    @GetMapping("/authenticated")
    public ResponseEntity<?> isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
        
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", authenticated);
        
        if (authenticated) {
            response.put("userId", auth.getName());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check if the current user has admin role
     * 
     * @return Boolean indicating admin status
     */
    @GetMapping("/is-admin")
    public ResponseEntity<?> isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && 
                         auth.isAuthenticated() && 
                         auth.getAuthorities().stream()
                             .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        return ResponseEntity.ok(Map.of("isAdmin", isAdmin));
    }
}
