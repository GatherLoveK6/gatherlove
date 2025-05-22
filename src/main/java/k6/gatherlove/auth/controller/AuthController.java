// src/main/java/k6/gatherlove/auth/controller/AuthController.java
package k6.gatherlove.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import k6.gatherlove.auth.dto.RegisterUserRequest;
import k6.gatherlove.auth.dto.UpdateProfileRequest;
import k6.gatherlove.auth.model.User;
import k6.gatherlove.auth.repository.UserRepository;
import k6.gatherlove.auth.strategy.AuthStrategy;
import k6.gatherlove.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
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
    private final UserRepository userRepository;    // ← new

    @GetMapping("/login")
    public String showLogin() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttrs) {
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

    @GetMapping("/register")
    public String showRegister() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute @Valid RegisterUserRequest req,
                           BindingResult binding,
                           RedirectAttributes redirectAttrs) {
        if (binding.hasErrors()) {
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

    // ─── PROFILE ───────────────────────────────────────────────────

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "auth/profile/view";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("profileDto", new UpdateProfileRequest(
                user.getFullName(), user.getPhone(), user.getAddress()
        ));
        return "auth/profile/edit";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @ModelAttribute("profileDto") @Valid UpdateProfileRequest dto,
            BindingResult binding,
            Authentication auth,
            RedirectAttributes redirectAttrs) {

        if (binding.hasErrors()) {
            redirectAttrs.addFlashAttribute("errors", binding.getAllErrors());
            redirectAttrs.addFlashAttribute("profileDto", dto);
            return "redirect:/auth/profile/edit";
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        userRepository.save(user);

        redirectAttrs.addFlashAttribute("message", "Profile updated!");
        return "redirect:/auth/profile";
    }
}
