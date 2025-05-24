// src/main/java/k6/gatherlove/auth/controller/AuthController.java
package k6.gatherlove.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import k6.gatherlove.auth.dto.RegisterUserRequest;
import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.strategy.AuthStrategy;
import k6.gatherlove.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthStrategy authStrategy;
    private final JwtUtil jwtUtil;

    @GetMapping("/login")
    public String showLogin() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response,
            RedirectAttributes redirectAttrs
    ) {
        try {
            User user = authStrategy.login(email, password);
            String token = jwtUtil.generateToken(user);
            ResponseCookie cookie = ResponseCookie.from("JWT", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofMillis(jwtUtil.getExpirationMs()))
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            redirectAttrs.addFlashAttribute("message", "Login successful!");
            return "redirect:/home";
        } catch (RuntimeException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
            return "redirect:/auth/login";
        }
    }

    // ——— THIS IS WHAT MATTERS FOR REGISTER ———
    @GetMapping("/register")
    public String showRegister(Model model) {
        // if we're coming to the form *after* a redirect with errors,
        // Spring will have already flash-added "registerDto" and "errors".
        // But on a *fresh* GET we need to supply an empty one.
        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new RegisterUserRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute("registerDto") @Valid RegisterUserRequest req,
            BindingResult binding,
            RedirectAttributes redirectAttrs
    ) {
        if (binding.hasErrors()) {
            // re-flash both the bean and its errors
            redirectAttrs.addFlashAttribute("registerDto", req);
            redirectAttrs.addFlashAttribute("errors", binding.getAllErrors());
            return "redirect:/auth/register";
        }
        authStrategy.register(req);
        redirectAttrs.addFlashAttribute("message", "Registration successful – please log in.");
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response,
                         RedirectAttributes redirectAttrs) {
        ResponseCookie cookie = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        redirectAttrs.addFlashAttribute("message", "You’ve been logged out.");
        return "redirect:/auth/login";
    }
}