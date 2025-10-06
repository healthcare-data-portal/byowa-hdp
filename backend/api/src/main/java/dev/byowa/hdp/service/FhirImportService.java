package dev.byowa.hdp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.byowa.hdp.mapper.FhirPatientMapper;
import dev.byowa.hdp.model.clinical.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FhirImportService {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private FhirPatientMapper fhirPatientMapper;
    //@Autowired private PersonRepository personRepository;

    public void processFhirJson(String fhirJson) throws Exception {
        JsonNode root = objectMapper.readTree(fhirJson);

        String resourceType = root.path("resourceType").asText();

        if (resourceType == null || resourceType.isEmpty()) {
            throw new IllegalArgumentException("Missing 'resourceType' field.");
        }

        switch (resourceType) {
            case "Bundle" -> processBundle(root);
            case "Patient" -> processPatient(root);
            // Später erweiterbar:
            // case "Condition" -> processCondition(root);
            // case "Observation" -> processObservation(root);
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
                // später:
                // case "Condition" -> processCondition(resource);
                // case "Observation" -> processObservation(resource);
                default -> System.out.println("Skipping unsupported resourceType: " + type);
            }
        }
    }

    private void processPatient(JsonNode patientNode) {
        Person person = fhirPatientMapper.mapFhirPatientToOmop(patientNode);
        //personRepository.save(person);
        System.out.println("Saved Patient → Person: " + person.getId());
    }
}
