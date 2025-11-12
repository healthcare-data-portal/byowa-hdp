package dev.byowa.hdp.controller;

import dev.byowa.hdp.dto.PersonalInfoResponse;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.clinical.PersonName;
import dev.byowa.hdp.model.healthsystem.Location;
import dev.byowa.hdp.repository.UserRepository;
import dev.byowa.hdp.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personal-info")
public class PersonalInfoController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public PersonalInfoController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<?> getPersonalInfo(HttpServletRequest req) {
        // Extract user from JWT token
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        String token = auth.substring(7);
        String username;
        try {
            username = jwtService.extractUsername(token);
            if (jwtService.isExpired(token)) {
                return ResponseEntity.status(401).body("Token expired");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        // Find user and associated person
        User user = userRepository.findByUsername(username)
            .orElse(null);
        
        if (user == null || user.getPerson() == null) {
            return ResponseEntity.status(404).body("User or Person data not found");
        }

        Person person = user.getPerson();
        PersonalInfoResponse response = new PersonalInfoResponse();

        // Extract personal information
        if (!person.getPersonNames().isEmpty()) {
            PersonName name = person.getPersonNames().get(0);
            response.setFullName(buildFullName(name));
            response.setEmail(name.getEmail());
            response.setPhoneNumber(name.getTelephone());
        }

        // Date of birth
        response.setDateOfBirth(buildDateOfBirth(person));

        // Social security number
        response.setSocialSecurityNumber(person.getPersonSourceValue());

        // Gender
        if (person.getGenderConcept() != null) {
            response.setGender(person.getGenderConcept().getConceptName());
        }

        // Address information
        Location location = person.getLocation();
        if (location != null) {
            response.setStreetAddress(buildStreetAddress(location));
            response.setCity(location.getCity());
            response.setState(location.getState());
            response.setZipCode(location.getZip());
            response.setCountry(location.getCountrySourceValue());
        }

        return ResponseEntity.ok(response);
    }

    private String buildFullName(PersonName name) {
        StringBuilder fullName = new StringBuilder();
        if (name.getGivenName() != null) {
            fullName.append(name.getGivenName());
        }
        if (name.getMiddleName() != null && !name.getMiddleName().isBlank()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(name.getMiddleName());
        }
        if (name.getFamilyName() != null) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(name.getFamilyName());
        }
        return fullName.toString();
    }

    private String buildDateOfBirth(Person person) {
        if (person.getYearOfBirth() == null) return null;
        
        int year = person.getYearOfBirth();
        int month = person.getMonthOfBirth() != null ? person.getMonthOfBirth() : 1;
        int day = person.getDayOfBirth() != null ? person.getDayOfBirth() : 1;
        
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private String buildStreetAddress(Location location) {
        StringBuilder address = new StringBuilder();
        if (location.getAddress1() != null) {
            address.append(location.getAddress1());
        }
        if (location.getAddress2() != null && !location.getAddress2().isBlank()) {
            if (address.length() > 0) address.append(", ");
            address.append(location.getAddress2());
        }
        return address.toString();
    }
}
