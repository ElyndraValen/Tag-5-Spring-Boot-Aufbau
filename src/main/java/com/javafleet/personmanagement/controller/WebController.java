package com.javafleet.personmanagement.controller;

import com.javafleet.personmanagement.repository.PersonRepository;
import com.javafleet.personmanagement.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web Controller für HTML-Seiten (Thymeleaf)
 * 
 * @Controller statt @RestController - gibt HTML zurück, nicht JSON!
 */
@Controller
@RequiredArgsConstructor
public class WebController {
    
    private final PersonService personService;
    
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
    public String home(Model m) {
        m.addAttribute("personList", personService.getAllPersons());
        return "persons";
    }
}
