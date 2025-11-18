// Java
package dev.byowa.hdp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import dev.byowa.hdp.mapper.FhirPatientMapper;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.healthsystem.Location;
import dev.byowa.hdp.repository.PersonRepository;
import dev.byowa.hdp.repository.UserRepository;
import dev.byowa.hdp.repository.LocationRepository;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FhirImportService {

    @Autowired private FhirPatientMapper fhirPatientMapper;
    @Autowired private PersonRepository personRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // new FHIR-Context (R4)
    private final FhirContext fhirContext = FhirContext.forR4();

    public void processFhirJson(String fhirJson) throws Exception {
        IParser parser = fhirContext.newJsonParser();
        Resource resource = (Resource) parser.parseResource(fhirJson);

        String resourceType = resource.fhirType();
        if (resourceType == null || resourceType.isEmpty()) {
            throw new IllegalArgumentException("Missing 'resourceType' field.");
        }

        switch (resourceType) {
            case "Bundle" -> processBundle((Bundle) resource);
            case "Patient" -> processPatient((Patient) resource);
            default -> throw new UnsupportedOperationException("Unsupported resourceType: " + resourceType);
        }
    }

    private void processBundle(Bundle bundle) {
        if (bundle == null || bundle.getEntry().isEmpty()) {
            throw new IllegalArgumentException("FHIR Bundle missing 'entry' array.");
        }
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            Resource resource = entry.getResource();
            if (resource == null) continue;

            String type = resource.fhirType();
            switch (type) {
                case "Patient" -> processPatient((Patient) resource);
                default -> System.out.println("Skipping unsupported resourceType: " + type);
            }
        }
    }

    @Transactional
    public void processPatient(Patient patient) {
        // Skip inactive patients
        if (patient.hasActive() && Boolean.FALSE.equals(patient.getActive())) {
            System.out.println("Skipping inactive patient.");
            return;
        }

        // Extract identifier value for de-duplication and user creation
        String identifier = extractIdentifierValue(patient);
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Patient has no identifier value (cannot ensure uniqueness).");
        }

        // Find existing person by source value
        Optional<Person> existing = personRepository.findByPersonSourceValue(identifier);

        Person person = existing
                .map(p -> fhirPatientMapper.mapFhirPatientToOmop(patient, p)) // update existing
                .orElseGet(() -> {
                    Person created = fhirPatientMapper.mapFhirPatientToOmop(patient);
                    Integer maxId = personRepository.findMaxId();
                    created.setId(maxId == null ? 1 : maxId + 1);
                    return created;
                });



        // Save person (PersonName is cascaded with orphanRemoval)
        personRepository.save(person);

        // Map and persist Location (deduplicate by location_source_value)
        Location resolvedLocation = null;
        Location loc = fhirPatientMapper.mapFhirPatientToLocation(patient);
        if (loc != null && loc.getLocationSourceValue() != null && !loc.getLocationSourceValue().isBlank()) {
            Optional<Location> existingLoc = locationRepository.findByLocationSourceValue(loc.getLocationSourceValue());
            if (existingLoc.isPresent()) {
                resolvedLocation = existingLoc.get();
            } else {
                Integer maxLocId = locationRepository.findMaxId();
                loc.setId(maxLocId == null ? 1 : maxLocId + 1);
                resolvedLocation = locationRepository.save(loc);
                System.out.println("Saved Location → id: " + resolvedLocation.getId());
            }
        }

        // Link Location to Person
        if (resolvedLocation != null) {
            person.setLocation(resolvedLocation);
        }
        personRepository.save(person);

        // Create user if not exists (username = identifier)
        if (!userRepository.existsByUsername(identifier)) {
            String username = identifier + "@hdp.com";
            String hashed = passwordEncoder.encode(identifier);
            User user = new User(username, hashed);
            user.setPerson(person);
            userRepository.save(user);
        }

        System.out.println("Saved/Updated Patient → Person: " + person.getId());
    }

    // Helper: take the first identifier.value from HAPI Patient
    private String extractIdentifierValue(Patient patient) {
        if (patient.hasIdentifier() && !patient.getIdentifier().isEmpty()) {
            if (patient.getIdentifierFirstRep().hasValue()) {
                return patient.getIdentifierFirstRep().getValue();
            }
        }
        return null;
    }
}
