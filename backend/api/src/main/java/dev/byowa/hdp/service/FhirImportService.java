package dev.byowa.hdp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import dev.byowa.hdp.mapper.FhirPatientMapper;
import dev.byowa.hdp.mapper.FhirPractitionerMapper;
import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.clinical.PersonName;
import dev.byowa.hdp.model.healthsystem.Location;
import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.repository.LocationRepository;
import dev.byowa.hdp.repository.PersonRepository;
import dev.byowa.hdp.repository.ProviderRepository;
import dev.byowa.hdp.repository.UserRepository;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FhirImportService {

    @Autowired
    private FhirPatientMapper fhirPatientMapper;

    @Autowired
    private FhirPractitionerMapper fhirPractitionerMapper;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
            case "Practitioner" -> processPractitioner((Practitioner) resource);
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
                case "Practitioner" -> processPractitioner((Practitioner) resource);
                default -> System.out.println("Skipping unsupported resourceType: " + type);
            }
        }
    }

    @Transactional
    public void processPatient(Patient patient) {
        if (patient.hasActive() && Boolean.FALSE.equals(patient.getActive())) {
            System.out.println("Skipping inactive patient.");
            return;
        }

        String identifier = extractPatientIdentifierValue(patient);
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Patient has no identifier value (cannot ensure uniqueness).");
        }

        Optional<Person> existing = personRepository.findByPersonSourceValue(identifier);

        Person person = existing
                .map(p -> fhirPatientMapper.mapFhirPatientToOmop(patient, p))
                .orElseGet(() -> {
                    Person created = fhirPatientMapper.mapFhirPatientToOmop(patient);
                    Integer maxId = personRepository.findMaxId();
                    created.setId(maxId == null ? 1 : maxId + 1);
                    return created;
                });

        personRepository.save(person);

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

        if (resolvedLocation != null) {
            person.setLocation(resolvedLocation);
        }
        personRepository.save(person);

        // Build full name from mapped Person/PersonName
        String fullName = buildFullNameFromPerson(person);

        String username = identifier + "@hdp.com";

        // Check username existence using the actual username
        if (!userRepository.existsByUsername(username)) {
            String hashed = passwordEncoder.encode(identifier);
            User user = new User(username, hashed);
            user.setPerson(person);
            user.setRole(Role.PATIENT);

            // Set full name if available
            if (fullName != null) {
                user.setFullName(fullName);
            }

            userRepository.save(user);
        } else {
            // Optional: keep User.fullName in sync on updates
            User existingUser = userRepository.findByUsername(username).orElse(null);
            if (existingUser != null && fullName != null) {
                existingUser.setFullName(fullName);
                userRepository.save(existingUser);
            }
        }

        System.out.println("Saved/Updated Patient → Person: " + person.getId());
    }


    @Transactional
    public void processPractitioner(Practitioner practitioner) {
        String identifier = extractPractitionerIdentifierValue(practitioner);
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Practitioner has no identifier value (cannot ensure uniqueness).");
        }

        Optional<Provider> existing = providerRepository.findByProviderSourceValue(identifier);

        Provider provider = existing
                .map(p -> fhirPractitionerMapper.mapFhirPractitionerToProvider(practitioner, p))
                .orElseGet(() -> {
                    Provider created = fhirPractitionerMapper.mapFhirPractitionerToProvider(practitioner, null);
                    Integer maxId = providerRepository.findMaxId();
                    created.setId(maxId == null ? 1 : maxId + 1);
                    return created;
                });

        providerRepository.save(provider);

        // Build username from identifier
        String username = identifier + "@hdp.com";

        // Use provider name (mapped from Practitioner) as full name for the doctor user
        String fullName = provider.getProviderName();

        if (!userRepository.existsByUsername(username)) {
            String hashed = passwordEncoder.encode(identifier);
            User user = new User(username, hashed);
            user.setRole(Role.DOCTOR);

            if (fullName != null && !fullName.isBlank()) {
                user.setFullName(fullName.trim());
            }

            userRepository.save(user);
        } else {
            // Optionally keep User.fullName in sync when provider name changes
            userRepository.findByUsername(username).ifPresent(existingUser -> {
                if (fullName != null && !fullName.isBlank()
                        && (existingUser.getFullName() == null
                        || !fullName.trim().equals(existingUser.getFullName()))) {
                    existingUser.setFullName(fullName.trim());
                    userRepository.save(existingUser);
                }
            });
        }

        System.out.println("Saved/Updated Practitioner → Provider: " + provider.getId());
    }


    private String extractPatientIdentifierValue(Patient patient) {
        if (patient.hasIdentifier() && !patient.getIdentifier().isEmpty()) {
            if (patient.getIdentifierFirstRep().hasValue()) {
                return patient.getIdentifierFirstRep().getValue();
            }
        }
        return null;
    }

    private String extractPractitionerIdentifierValue(Practitioner practitioner) {
        if (practitioner.hasIdentifier() && !practitioner.getIdentifier().isEmpty()) {
            if (practitioner.getIdentifierFirstRep().hasValue()) {
                return practitioner.getIdentifierFirstRep().getValue();
            }
        }
        return null;
    }




    private String buildFullNameFromPerson(Person person) {
        if (person == null || person.getPersonNames() == null || person.getPersonNames().isEmpty()) {
            return null;
        }

        PersonName pn = person.getPersonNames().get(0);

        StringBuilder sb = new StringBuilder();
        if (pn.getGivenName() != null && !pn.getGivenName().isBlank()) {
            sb.append(pn.getGivenName().trim());
        }
        if (pn.getMiddleName() != null && !pn.getMiddleName().isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(pn.getMiddleName().trim());
        }
        if (pn.getFamilyName() != null && !pn.getFamilyName().isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(pn.getFamilyName().trim());
        }

        String fullName = sb.toString().trim();
        return fullName.isEmpty() ? null : fullName;
    }

}
