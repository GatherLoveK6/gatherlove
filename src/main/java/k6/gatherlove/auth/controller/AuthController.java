package k6.gatherlove.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import k6.gatherlove.auth.dto.RegisterUserRequest;
import k6.gatherlove.auth.strategy.AuthStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthStrategy authStrategy;

    @GetMapping("/")
    public String root() {
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/register")
    public String showRegisterPage() {
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String register(
            @ModelAttribute @Valid RegisterUserRequest req,
            BindingResult binding,
            RedirectAttributes rttrs
    ) {
        if (binding.hasErrors()) {
            rttrs.addFlashAttribute("errors", binding.getAllErrors());
            return "redirect:/auth/register";
        }

        authStrategy.register(req);
        rttrs.addFlashAttribute("message", "Registration successful – please log in.");
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @PostMapping("/auth/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request,
            RedirectAttributes rttrs
    ) {
        try {
            var user = authStrategy.login(email, password);
            request.getSession().setAttribute("currentUser", user);
            rttrs.addFlashAttribute("message", "Login successful – welcome!");
            return "redirect:/auth/login"; // Reuse login page with success message
        } catch (RuntimeException ex) {
            rttrs.addFlashAttribute("error", ex.getMessage());
            return "redirect:/auth/login";
        }
    }
}
