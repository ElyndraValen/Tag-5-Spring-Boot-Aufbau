# Tag 5: Spring Boot Aufbau - Authorization & Method Security

**Person Management mit Spring Security Authorization**

Von Elyndra Valen, Senior Entwicklerin bei Java Fleet Systems Consulting

---

## 📋 Projekt-Übersicht

Dieses Projekt ist Teil des **Spring Boot Aufbau-Kurses** (Tag 5 von 10) und demonstriert **Authorization** mit **URL-basierter Zugriffskontrolle** und **Method Security**.

### Was du in diesem Projekt lernst:

- ✅ URL-basierte Authorization (hasRole, hasAuthority)
- ✅ Method Security mit @PreAuthorize
- ✅ @EnableMethodSecurity Konfiguration
- ✅ SpEL Expressions für komplexe Regeln
- ✅ Custom SecurityService für Ownership-Checks
- ✅ @PostAuthorize für Response-Filterung
- ✅ Enterprise-Level Authorization Patterns

---

## 🚀 Quick Start

### Voraussetzungen

- Java JDK 17 oder höher
- Maven 3.6+
- IDE deiner Wahl

### Projekt starten

```bash
# 1. Repository klonen oder ZIP entpacken
cd Tag-5-Spring-Boot-Aufbau

# 2. Maven Dependencies laden
mvn clean install

# 3. Anwendung starten
mvn spring-boot:run

# 4. Browser öffnen
http://localhost:8080
```

### Login-Daten & Rollen-Tests

| Username | Passwort | Rolle | Zugriff |
|----------|----------|-------|---------|
| `admin` | `admin123` | ADMIN | Voller Zugriff inkl. /admin/** |
| `user` | `user123` | USER | Eingeschränkter Zugriff |
| `moderator` | `mod123` | MODERATOR | Mittlere Rechte |

---

## 🎯 Was ist neu in Tag 5?

### 1. URL-basierte Authorization

```java
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/api/persons/**").hasAnyRole("USER", "ADMIN")
```

**Test:**
- Als `admin`: `http://localhost:8080/admin/dashboard` ✅
- Als `user`: `http://localhost:8080/admin/dashboard` ❌ 403 Forbidden

### 2. Method Security mit @PreAuthorize

```java
@PreAuthorize("hasRole('ADMIN')")
public Person createPerson(Person person) {
    return personRepository.save(person);
}
```

**Test via API:**
```bash
# Als ADMIN - funktioniert
curl -u admin:admin123 -X POST -H "Content-Type: application/json" \
  -d '{"firstname":"Max","lastname":"Mustermann","email":"max@test.com"}' \
  http://localhost:8080/api/persons

# Als USER - 403 Forbidden
curl -u user:user123 -X POST -H "Content-Type: application/json" \
  -d '{"firstname":"Max","lastname":"Mustermann","email":"max@test.com"}' \
  http://localhost:8080/api/persons
```

### 3. Ownership-basierte Authorization

```java
@PreAuthorize("@securityService.isOwnerOrAdmin(#id)")
public void deletePerson(Long id) {
    personRepository.deleteById(id);
}
```

**Bedeutung:** User kann nur EIGENE Persons löschen, Admin kann alle löschen!

---

## 📁 Neue Dateien in Tag 5

```
Tag-5-Spring-Boot-Aufbau/
├── src/main/java/.../
│   ├── config/
│   │   └── SecurityConfig.java          # @EnableMethodSecurity
│   ├── service/
│   │   └── PersonService.java           # @PreAuthorize, @PostAuthorize
│   ├── security/
│   │   ├── CustomUserDetailsService.java
│   │   └── SecurityService.java         # NEW! Ownership-Checks
│   └── controller/
│       └── AdminController.java         # NEW! Admin-Bereiche
│
└── src/main/resources/templates/
    ├── admin/
    │   └── dashboard.html               # NEW! Admin Dashboard
    └── error/
        └── 403.html                     # NEW! Custom Error Page
```

---

## 🧪 Testing

### Browser-Tests

**1. Als ADMIN einloggen und Admin-Dashboard testen:**
```
Login: admin / admin123
URL: http://localhost:8080/admin/dashboard
Ergebnis: ✅ Dashboard wird angezeigt
```

**2. Als USER einloggen und Admin-Dashboard versuchen:**
```
Login: user / user123
URL: http://localhost:8080/admin/dashboard
Ergebnis: ❌ 403 Forbidden (Custom Error Page)
```

### API-Tests (curl)

**GET Persons (alle dürfen):**
```bash
curl -u user:user123 http://localhost:8080/api/persons
# ✅ Funktioniert für USER
```

**POST Person (nur ADMIN):**
```bash
curl -u admin:admin123 -X POST -H "Content-Type: application/json" \
  -d '{"firstname":"Test","lastname":"User","email":"test@example.com"}' \
  http://localhost:8080/api/persons
# ✅ Funktioniert für ADMIN

curl -u user:user123 -X POST -H "Content-Type: application/json" \
  -d '{"firstname":"Test","lastname":"User","email":"test@example.com"}' \
  http://localhost:8080/api/persons
# ❌ 403 Forbidden für USER
```

**DELETE Person (nur Owner oder ADMIN):**
```bash
curl -u admin:admin123 -X DELETE http://localhost:8080/api/persons/1
# ✅ ADMIN darf löschen

curl -u user:user123 -X DELETE http://localhost:8080/api/persons/1
# ❌ 403 wenn User nicht Owner
```

---

## 🔑 Wichtige Konzepte

### hasRole() vs hasAuthority()

```java
// hasRole("ADMIN") → Prüft auf "ROLE_ADMIN"
.requestMatchers("/admin/**").hasRole("ADMIN")

// hasAuthority("ROLE_ADMIN") → Prüft exakt auf "ROLE_ADMIN"
.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
```

**Faustregel:** Nutze `hasRole()` - ist lesbarer!

### @PreAuthorize vs @PostAuthorize

```java
// VOR Methoden-Ausführung prüfen
@PreAuthorize("hasRole('ADMIN')")
public void delete(Long id) { ... }

// NACH Methoden-Ausführung prüfen (mit Result)
@PostAuthorize("returnObject.owner.username == authentication.name")
public Person getPerson(Long id) { ... }
```

### Custom Security Expressions

```java
// In SecurityService.java
@Service("securityService")
public class SecurityService {
    public boolean isOwnerOrAdmin(Long personId) {
        return isAdmin() || isOwner(personId);
    }
}

// In Service-Methode
@PreAuthorize("@securityService.isOwnerOrAdmin(#id)")
public void update(Long id, Person person) { ... }
```

---

## 🐛 Troubleshooting

### @PreAuthorize funktioniert nicht

**Ursache:** `@EnableMethodSecurity` vergessen.

**Lösung:** In `SecurityConfig.java`:
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // <- WICHTIG!
public class SecurityConfig { ... }
```

### Immer 403 auch als ADMIN

**Ursache:** Role-String falsch.

**Lösung:** In @PreAuthorize einfache Anführungszeichen:
```java
@PreAuthorize("hasRole('ADMIN')")  // ✅ RICHTIG
@PreAuthorize("hasRole(ADMIN)")    // ❌ FALSCH
```

### Custom SecurityService wird nicht gefunden

**Ursache:** Bean-Name fehlt oder falsch.

**Lösung:**
```java
@Service("securityService")  // <- Bean-Name!
public class SecurityService { ... }
```

---

## 📚 Weitere Ressourcen

### Offizielle Dokumentation

- [Spring Security Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [SpEL Expressions](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions)
- [@PreAuthorize Reference](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/access/prepost/PreAuthorize.html)

### Blogbeitrag

Dieser Code ist Teil des Blogbeitrags:  
**"Tag 5: Spring Security - Authorization & Method Security"**

👉 [Zum vollständigen Blogbeitrag](https://java-developer.online)

### Nächster Kurstag

**Tag 6: Caching & Serialisierung**

Was du morgen lernst:
- Spring Cache Abstraction
- Caching Strategies
- Redis Integration
- Performance-Optimierung

---

## 🤝 Kontakt & Support

**Fragen oder Probleme?**

- 📧 Email: elyndra@java-developer.online
- 💬 GitHub Issues: [Issue erstellen](#)
- 📚 Blog: https://java-developer.online

---

## 📝 Lizenz

Dieses Projekt ist Teil des Java Fleet Systems Consulting Kursmaterials.  
Frei verwendbar für Lernzwecke.

---

**"Authorization ist der Unterschied zwischen 'du bist drin' und 'du darfst das'!"**  
*- Elyndra Valen*

Keep coding, keep learning! 💙
