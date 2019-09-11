package com.mgmtp.internship.experiences.controllers.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Login Controller.
 *
 * @author ntynguyen
 */
@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(Principal principal) {
        return principal == null ? "login" : "redirect:/";
    }
}
