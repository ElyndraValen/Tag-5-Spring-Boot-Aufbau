package com.javafleet.personmanagement.security;

import com.javafleet.personmanagement.entity.Person;
import com.javafleet.personmanagement.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Custom Security Service für Ownership-Checks
 * 
 * Wird in @PreAuthorize verwendet: @securityService.isOwnerOrAdmin(#id)
 */
@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {
    
    private final PersonRepository personRepository;
    
    /**
     * Prüft ob aktueller User Owner der Person ist
     */
    public boolean isOwner(Long personId) {
        String username = getCurrentUsername();
        if (username == null) {
            return false;
        }
        
        Person person = personRepository.findById(personId).orElse(null);
        if (person == null || person.getOwner() == null) {
            return false;
        }
        
        return person.getOwner().getUsername().equals(username);
    }
    
    /**
     * Prüft ob User Owner IST oder Admin ist
     */
    public boolean isOwnerOrAdmin(Long personId) {
        if (isAdmin()) {
            return true; // Admin darf alles
        }
        return isOwner(personId);
    }
    
    /**
     * Prüft ob aktueller User Admin ist
     */
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    
    /**
     * Gibt den aktuellen Username zurück
     */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return auth.getName();
    }
}
