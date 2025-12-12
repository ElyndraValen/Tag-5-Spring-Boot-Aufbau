package com.javafleet.personmanagement.controller;

import com.javafleet.personmanagement.entity.Person;
import com.javafleet.personmanagement.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller für Person API
 * 
 * Stellt REST-Endpoints für Person-Verwaltung bereit.
 * Diese Endpoints sind durch Spring Security geschützt!
 * 
 * @author Elyndra Valen
 */
@RestController
@RequestMapping("/web/persons")
@RequiredArgsConstructor
public class PersonController {
    
    private final PersonRepository personRepository;
    
    /**
     * Gibt alle Personen als JSON zurück.
     * 
     * Benötigt Authentication!
     * Teste mit: curl -u admin:admin123 http://localhost:8080/api/persons
     * 
     * @return Liste aller Personen
     */
    @GetMapping
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }
}

/**
 * View Controller für Person HTML-Seite
 * 
 * Zeigt eine HTML-Seite mit allen Personen.
 * Nutzt Thymeleaf als Template-Engine.
 * 
 * @author Elyndra Valen
 */
@Controller
@RequiredArgsConstructor
class PersonViewController {
    
    private final PersonRepository personRepository;
    
    /**
     * Zeigt die Persons-Übersicht-Seite.
     * 
     * Der SecurityContext wird genutzt um den aktuell
     * eingeloggten User zu ermitteln und in der View anzuzeigen.
     * 
     * @param model Spring MVC Model für View-Daten
     * @return Name der Thymeleaf-Template (persons.html)
     */
    @GetMapping("/persons")
    public String showPersons(Model model) {
        // Aktuellen User aus SecurityContext holen
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        
        // Daten für View vorbereiten
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", auth.getAuthorities());
        model.addAttribute("persons", personRepository.findAll());
        
        return "persons";
    }
}
