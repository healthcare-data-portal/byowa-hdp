package dev.byowa.hdp.controller;

import dev.byowa.hdp.dto.PersonalInfoResponse;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.clinical.PersonName;
import dev.byowa.hdp.model.healthsystem.Location;
import dev.byowa.hdp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/personal-info") 
public class PersonalInfoController {

    private static final Logger logger = LoggerFactory.getLogger(PersonalInfoController.class);
    private final UserRepository userRepository;

    public PersonalInfoController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getPersonalInfo(Authentication authentication) {
        logger.debug("Received request for personal info");
        
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauthenticated request");
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        String username = authentication.getName();
        logger.debug("Username from authentication: {}", username);

        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null) {
            logger.error("User not found: {}", username);
            return ResponseEntity.status(404).body("User not found");
        }
        
        logger.debug("User found: {}", user.getUsername());
        
        if (user.getPerson() == null) {
            logger.error("Person data not found for user: {}", username);
            return ResponseEntity.status(404).body("Person data not found");
        }

        Person person = user.getPerson();
        logger.debug("Person found: {}", person.getId());
        
        PersonalInfoResponse response = new PersonalInfoResponse();

        if (person.getPersonNames() != null && !person.getPersonNames().isEmpty()) {
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

        logger.debug("Returning personal info response");
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
