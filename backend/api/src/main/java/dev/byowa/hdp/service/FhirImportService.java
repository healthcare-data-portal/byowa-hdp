// Java
package dev.byowa.hdp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.byowa.hdp.mapper.FhirPatientMapper;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.repository.PersonRepository;
import dev.byowa.hdp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FhirImportService {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private FhirPatientMapper fhirPatientMapper;
    @Autowired private PersonRepository personRepository;
    @Autowired private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void processFhirJson(String fhirJson) throws Exception {
        JsonNode root = objectMapper.readTree(fhirJson);

        String resourceType = root.path("resourceType").asText();
        if (resourceType == null || resourceType.isEmpty()) {
            throw new IllegalArgumentException("Missing 'resourceType' field.");
        }

        switch (resourceType) {
            case "Bundle" -> processBundle(root);
            case "Patient" -> processPatient(root);
            default -> throw new UnsupportedOperationException("Unsupported resourceType: " + resourceType);
        }
    }

    private void processBundle(JsonNode bundle) {
        if (!bundle.has("entry")) {
            throw new IllegalArgumentException("FHIR Bundle missing 'entry' array.");
        }
        for (JsonNode entry : bundle.path("entry")) {
            JsonNode resource = entry.path("resource");
            if (resource.isMissingNode()) continue;

            String type = resource.path("resourceType").asText();
            switch (type) {
                case "Patient" -> processPatient(resource);
                default -> System.out.println("Skipping unsupported resourceType: " + type);
            }
        }
    }

    @Transactional
    public void processPatient(JsonNode patientNode) {
        // Skip inactive patients
        if (patientNode.has("active") && !patientNode.get("active").asBoolean()) {
            System.out.println("Skipping inactive patient.");
            return;
        }

        // Extract identifier value for de-duplication and user creation
        String identifier = extractIdentifierValue(patientNode);
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Patient has no identifier value (cannot ensure uniqueness).");
        }

        // Find existing person by source value
        Optional<Person> existing = personRepository.findByPersonSourceValue(identifier);

        Person person = existing
                .map(p -> fhirPatientMapper.mapFhirPatientToOmop(patientNode, p)) // update existing
                .orElseGet(() -> {
                    Person created = fhirPatientMapper.mapFhirPatientToOmop(patientNode);
                    Integer maxId = personRepository.findMaxId();
                    created.setId(maxId == null ? 1 : maxId + 1);
                    return created;
                });

        // Save person (PersonName is cascaded with orphanRemoval)
        personRepository.save(person);

        // Create user if not exists (username = identifier)
        if (!userRepository.existsByUsername(identifier)) {
            String hashed = passwordEncoder.encode(identifier);
            User user = new User(identifier, hashed);
            user.setPerson(person);
            userRepository.save(user);
        }

        System.out.println("Saved/Updated Patient → Person: " + person.getId());
    }

    // Helper: take the first identifier.value
    private String extractIdentifierValue(JsonNode patientNode) {
        if (patientNode.has("identifier") && patientNode.get("identifier").isArray() && patientNode.get("identifier").size() > 0) {
            return patientNode.get("identifier").get(0).path("value").asText(null);
        }
        return null;
    }
}