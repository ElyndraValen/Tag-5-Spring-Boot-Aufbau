package com.javafleet.personmanagement.service;

import com.javafleet.personmanagement.entity.Person;
import com.javafleet.personmanagement.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Person Service mit Method Security
 * 
 * Demonstriert @PreAuthorize und @PostAuthorize für Authorization
 */
@Service
@RequiredArgsConstructor
public class PersonService {
    
    private final PersonRepository personRepository;
    
    /**
     * Alle Persons abrufen - nur eingeloggte User
     */
    @PreAuthorize("isAuthenticated()")
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }
    
    /**
     * Person nach ID - nur Owner oder Admin
     */
    @PostAuthorize("returnObject.owner == null or returnObject.owner.username == authentication.name or hasRole('ADMIN')")
    public Person getPersonById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found: " + id));
    }
    
    /**
     * Neue Person erstellen - nur ADMIN
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Person createPerson(Person person) {
        return personRepository.save(person);
    }
    
    /**
     * Person aktualisieren - nur Owner oder ADMIN
     */
    @PreAuthorize("@securityService.isOwnerOrAdmin(#id)")
    public Person updatePerson(Long id, Person personDetails) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found: " + id));
        
        person.setFirstname(personDetails.getFirstname());
        person.setLastname(personDetails.getLastname());
        person.setEmail(personDetails.getEmail());
        
        return personRepository.save(person);
    }
    
    /**
     * Person löschen - nur Owner oder ADMIN
     */
    @PreAuthorize("@securityService.isOwnerOrAdmin(#id)")
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }
}
