// src/main/java/k6/gatherlove/controller/HelloController.java
package k6.gatherlove.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Authentication authentication, Model model) {
        // If no one is authenticated, redirect to login
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        // Extract username (email) from the Authentication principal
        String username = authentication.getName();

        // Determine if the user has the ROLE_ADMIN authority
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);

        return "home";
    }
}


