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
            logger.info("No Person linked for user {}, returning minimal profile", username);
            PersonalInfoResponse response = new PersonalInfoResponse();

            String fullName = user.getFullName();
            if (fullName == null || fullName.isBlank()) {
                fullName = user.getUsername();
            }

            response.setFullName(fullName);
            response.setEmail(user.getUsername());
            // Fallback values stored on User when no Person record exists
            response.setPhoneNumber(user.getContactPhone());
            response.setStreetAddress(user.getAddressStreet());
            response.setCity(user.getAddressCity());
            response.setState(user.getAddressState());
            response.setZipCode(user.getAddressZip());
            response.setCountry(user.getAddressCountry());
            response.setEmergencyContactName(user.getEmergencyContactName());
            response.setEmergencyContactPhone(user.getEmergencyContactPhone());
            response.setEmergencyContactRelation(user.getEmergencyContactRelation());

            return ResponseEntity.ok(response);
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

        response.setDateOfBirth(buildDateOfBirth(person));
        response.setSocialSecurityNumber(person.getPersonSourceValue());

        if (person.getGenderConcept() != null) {
            response.setGender(person.getGenderConcept().getConceptName());
        }

        Location location = person.getLocation();
        if (location != null) {
            response.setStreetAddress(buildStreetAddress(location));
            response.setCity(location.getCity());
            response.setState(location.getState());
            response.setZipCode(location.getZip());
            response.setCountry(location.getCountrySourceValue());
        }

        // Fallbacks from User if OMOP data is missing
        if (response.getPhoneNumber() == null || response.getPhoneNumber().isBlank()) {
            response.setPhoneNumber(user.getContactPhone());
        }
        if (response.getStreetAddress() == null || response.getStreetAddress().isBlank()) {
            response.setStreetAddress(user.getAddressStreet());
        }
        if (response.getCity() == null || response.getCity().isBlank()) {
            response.setCity(user.getAddressCity());
        }
        if (response.getState() == null || response.getState().isBlank()) {
            response.setState(user.getAddressState());
        }
        if (response.getZipCode() == null || response.getZipCode().isBlank()) {
            response.setZipCode(user.getAddressZip());
        }
        if (response.getCountry() == null || response.getCountry().isBlank()) {
            response.setCountry(user.getAddressCountry());
        }

        // Emergency contact from User
        response.setEmergencyContactName(user.getEmergencyContactName());
        response.setEmergencyContactPhone(user.getEmergencyContactPhone());
        response.setEmergencyContactRelation(user.getEmergencyContactRelation());

        logger.debug("Returning personal info response");
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> updatePersonalInfo(@RequestBody PersonalInfoUpdateRequest request,
                                                Authentication authentication) {
        logger.debug("Received update for personal info");

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauthenticated update request");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            logger.error("User not found: {}", username);
            return ResponseEntity.status(404).body("User not found");
        }

        Person person = user.getPerson();

        // Update phone number on first PersonName if available
        if (person != null) {
            if (person.getPersonNames() != null && !person.getPersonNames().isEmpty()) {
                PersonName name = person.getPersonNames().get(0);
                name.setTelephone(request.getPhoneNumber());
                personNameRepository.save(name);
            } else {
                // Create a PersonName entry if missing
                PersonName name = new PersonName();
                name.setPerson(person);
                name.setTelephone(request.getPhoneNumber());
                personNameRepository.save(name);
                person.getPersonNames().add(name);
                personRepository.save(person);
            }
        }

        // Update or create Location if any address field provided
        boolean hasAddress = (request.getStreetAddress() != null && !request.getStreetAddress().isBlank())
                || (request.getCity() != null && !request.getCity().isBlank())
                || (request.getState() != null && !request.getState().isBlank())
                || (request.getZipCode() != null && !request.getZipCode().isBlank())
                || (request.getCountry() != null && !request.getCountry().isBlank());

        if (hasAddress && person != null) {
            Location loc = person.getLocation();
            if (loc == null) {
                loc = new Location();
                Integer maxLocId = locationRepository.findMaxId();
                loc.setId((maxLocId == null ? 1 : maxLocId + 1));
            }
            if (request.getStreetAddress() != null) {
                // Split optional address2 if a comma pattern is used
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

            loc = locationRepository.save(loc);
            person.setLocation(loc);
            personRepository.save(person);
        }

        // Persist fallback fields on User so UI always reflects updates even if OMOP data missing
        user.setContactPhone(request.getPhoneNumber());
        user.setAddressStreet(request.getStreetAddress());
        user.setAddressCity(request.getCity());
        user.setAddressState(request.getState());
        user.setAddressZip(request.getZipCode());
        user.setAddressCountry(request.getCountry());

        // Update emergency contact on User
        user.setEmergencyContactName(request.getEmergencyContactName());
        user.setEmergencyContactPhone(request.getEmergencyContactPhone());
        user.setEmergencyContactRelation(request.getEmergencyContactRelation());
        userRepository.save(user);

        // Return updated view
        return getPersonalInfo(authentication);
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
