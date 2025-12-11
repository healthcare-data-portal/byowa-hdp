package dev.byowa.hdp.controller;

import dev.byowa.hdp.dto.PersonalInfoResponse;
import dev.byowa.hdp.dto.PersonalInfoUpdateRequest;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.clinical.PersonName;
import dev.byowa.hdp.model.healthsystem.Location;
import dev.byowa.hdp.repository.LocationRepository;
import dev.byowa.hdp.repository.PersonNameRepository;
import dev.byowa.hdp.repository.PersonRepository;
import dev.byowa.hdp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/personal-info")
public class PersonalInfoController {

    private static final Logger logger = LoggerFactory.getLogger(PersonalInfoController.class);
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final PersonNameRepository personNameRepository;
    private final LocationRepository locationRepository;

    public PersonalInfoController(UserRepository userRepository,
                                  PersonRepository personRepository,
                                  PersonNameRepository personNameRepository,
                                  LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.personNameRepository = personNameRepository;
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public ResponseEntity<?> getPersonalInfo(Authentication authentication) {
        logger.debug("Received request for personal info");

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        PersonalInfoResponse response = new PersonalInfoResponse();

        // If no Person is linked, return minimal fallback info
        if (user.getPerson() == null) {
            String fullName = (user.getFullName() != null && !user.getFullName().isBlank())
                    ? user.getFullName()
                    : user.getUsername();

            response.setFullName(fullName);
            response.setEmail(user.getUsername());
            return ResponseEntity.ok(response);
        }

        Person person = user.getPerson();

        // Primary source: first PersonName entry
        PersonName primaryName = null;
        if (!person.getPersonNames().isEmpty()) {
            primaryName = person.getPersonNames().get(0);
        }

        if (primaryName != null) {
            response.setFullName(buildFullName(primaryName));
            response.setEmail(primaryName.getEmail());
            response.setPhoneNumber(primaryName.getTelephone());

            response.setEmergencyContactName(primaryName.getEmergencyContactName());
            response.setEmergencyContactPhone(primaryName.getEmergencyContactPhone());
            // Relation is not stored in PersonName → always null
        } else {
            // Fallback name/email from user
            String fullName = (user.getFullName() != null && !user.getFullName().isBlank())
                    ? user.getFullName()
                    : user.getUsername();
            response.setFullName(fullName);
            response.setEmail(user.getUsername());
        }

        // Date of birth (from Person)
        response.setDateOfBirth(buildDateOfBirth(person));
        response.setSocialSecurityNumber(person.getPersonSourceValue());

        if (person.getGenderConcept() != null) {
            response.setGender(person.getGenderConcept().getConceptName());
        }

        // Address via Location entity
        Location loc = person.getLocation();
        if (loc != null) {
            response.setStreetAddress(buildStreetAddress(loc));
            response.setCity(loc.getCity());
            response.setState(loc.getState());
            response.setZipCode(loc.getZip());
            response.setCountry(loc.getCountrySourceValue());
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> updatePersonalInfo(@RequestBody PersonalInfoUpdateRequest request,
                                                Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        Person person = user.getPerson();
        if (person == null) {
            return ResponseEntity.status(400).body("No Person linked to user");
        }

        // Update or create PersonName entry
        PersonName primaryName = null;
        if (!person.getPersonNames().isEmpty()) {
            primaryName = person.getPersonNames().get(0);
        }

        if (primaryName == null) {
            primaryName = new PersonName();
            primaryName.setPerson(person);
            person.getPersonNames().add(primaryName);
        }

        // Update contact info
        if (request.getPhoneNumber() != null) {
            primaryName.setTelephone(request.getPhoneNumber());
        }
        if (request.getEmergencyContactName() != null) {
            primaryName.setEmergencyContactName(request.getEmergencyContactName());
        }
        if (request.getEmergencyContactPhone() != null) {
            primaryName.setEmergencyContactPhone(request.getEmergencyContactPhone());
        }

        // Note: emergencyContactRelation cannot be stored (not present in PersonName)

        personNameRepository.save(primaryName);
        personRepository.save(person);

        // Update or create Address (Location)
        boolean hasAddress =
                (request.getStreetAddress() != null && !request.getStreetAddress().isBlank()) ||
                        (request.getCity() != null && !request.getCity().isBlank()) ||
                        (request.getState() != null && !request.getState().isBlank()) ||
                        (request.getZipCode() != null && !request.getZipCode().isBlank()) ||
                        (request.getCountry() != null && !request.getCountry().isBlank());

        if (hasAddress) {
            Location loc = person.getLocation();
            if (loc == null) {
                loc = new Location();
                Integer maxLocId = locationRepository.findMaxId();
                loc.setId((maxLocId == null ? 1 : maxLocId + 1));
            }

            // Handle street address + optional address2 via comma
            if (request.getStreetAddress() != null) {
                String street = request.getStreetAddress();
                if (street.contains(",")) {
                    String[] parts = street.split(",", 2);
                    loc.setAddress1(parts[0].trim());
                    loc.setAddress2(parts[1].trim());
                } else {
                    loc.setAddress1(street);
                    loc.setAddress2(null);
                }
            }

            if (request.getCity() != null) loc.setCity(request.getCity());
            if (request.getState() != null) loc.setState(request.getState());
            if (request.getZipCode() != null) loc.setZip(request.getZipCode());
            if (request.getCountry() != null) loc.setCountrySourceValue(request.getCountry());

            locationRepository.save(loc);
            person.setLocation(loc);
            personRepository.save(person);
        }

        // Return updated response
        return getPersonalInfo(authentication);
    }

    private String buildFullName(PersonName name) {
        StringBuilder sb = new StringBuilder();
        if (name.getGivenName() != null) sb.append(name.getGivenName());
        if (name.getMiddleName() != null && !name.getMiddleName().isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(name.getMiddleName());
        }
        if (name.getFamilyName() != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(name.getFamilyName());
        }
        return sb.toString();
    }

    private String buildDateOfBirth(Person person) {
        if (person.getYearOfBirth() == null) return null;

        int year = person.getYearOfBirth();
        int month = (person.getMonthOfBirth() != null) ? person.getMonthOfBirth() : 1;
        int day = (person.getDayOfBirth() != null) ? person.getDayOfBirth() : 1;

        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private String buildStreetAddress(Location loc) {
        StringBuilder sb = new StringBuilder();
        if (loc.getAddress1() != null) sb.append(loc.getAddress1());
        if (loc.getAddress2() != null && !loc.getAddress2().isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(loc.getAddress2());
        }
        return sb.toString();
    }
}
