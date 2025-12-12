package com.javafleet.personmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web Controller für HTML-Seiten (Thymeleaf)
 * 
 * @Controller statt @RestController - gibt HTML zurück, nicht JSON!
 */
@Controller
public class WebController {
    
    /**
     * Login-Seite anzeigen
     * 
     * @return Name des Thymeleaf-Templates (login.html)
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Root-URL "/" leitet zu /persons um
     * 
     * @return Redirect zur Persons-Seite
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/persons";
    }
}
