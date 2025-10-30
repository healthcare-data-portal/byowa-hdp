// Java
package dev.byowa.hdp.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.clinical.PersonName;
import dev.byowa.hdp.model.vocabulary.Concept;
import dev.byowa.hdp.repository.ConceptRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@Component
public class FhirPatientMapper {

    // OMOP concept placeholders for unknown values
    private static final int UNKNOWN_GENDER_CONCEPT_ID = 99999997;
    private static final int UNKNOWN_RACE_CONCEPT_ID = 99999998;
    private static final int UNKNOWN_ETHN_CONCEPT_ID = 99999999;

    private final ConceptRepository conceptRepository;

    public FhirPatientMapper(ConceptRepository conceptRepository) {
        this.conceptRepository = conceptRepository;
    }

    // Convenience overload that creates a new Person
    public Person mapFhirPatientToOmop(JsonNode fhirPatient) {
        return mapFhirPatientToOmop(fhirPatient, null);
    }

    // Map onto an existing Person (replace name list) or create a new one
    public Person mapFhirPatientToOmop(JsonNode fhirPatient, Person target) {
        Person person = (target != null) ? target : new Person();

        // Identifier -> person_source_value
        String identifier = null;
        if (fhirPatient.has("identifier") && fhirPatient.get("identifier").isArray() && fhirPatient.get("identifier").size() > 0) {
            identifier = fhirPatient.get("identifier").get(0).path("value").asText(null);
        }
        person.setPersonSourceValue(identifier);

        // Email/phone from telecom
        JsonNode telecomArray = fhirPatient.get("telecom");
        String email = extractTelecomValue(telecomArray, "email");
        String telephone = extractTelecomValue(telecomArray, "phone");

        // Build exactly one PersonName (replace existing list)
        person.getPersonNames().clear();
        JsonNode preferredName = getPreferredNameNode(fhirPatient.path("name"));
        if (preferredName != null && !preferredName.isMissingNode()) {
            String family = preferredName.path("family").asText(null);
            String givenCombined = joinGiven(preferredName.path("given"));

            PersonName pn = new PersonName();
            pn.setPerson(person);
            pn.setFamilyName(family);
            pn.setGivenName(givenCombined);
            pn.setEmail(email);
            pn.setTelephone(telephone);
            person.getPersonNames().add(pn);
        }

        // Gender
        String gender = fhirPatient.path("gender").asText();
        int genderId = mapGenderToConceptId(gender);
        Concept genderConcept = conceptRepository.getReferenceById(genderId);
        person.setGenderConcept(genderConcept);
        person.setGenderSourceConcept(genderConcept);

        // Race/Ethnicity (use unknown placeholders)
        Concept raceConcept = conceptRepository.getReferenceById(UNKNOWN_RACE_CONCEPT_ID);
        person.setRaceConcept(raceConcept);
        person.setRaceSourceConcept(raceConcept);

        Concept ethnicityConcept = conceptRepository.getReferenceById(UNKNOWN_ETHN_CONCEPT_ID);
        person.setEthnicityConcept(ethnicityConcept);
        person.setEthnicitySourceConcept(ethnicityConcept);

        // Birthdate -> year/month/day
        String birthDateStr = fhirPatient.path("birthDate").asText();
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_DATE);
            person.setYearOfBirth(birthDate.getYear());
            person.setMonthOfBirth(birthDate.getMonthValue());
            person.setDayOfBirth(birthDate.getDayOfMonth());
        }

        return person;
    }

    // Pick "official" name, fallback to first element
    private JsonNode getPreferredNameNode(JsonNode namesArray) {
        if (namesArray == null || !namesArray.isArray() || namesArray.size() == 0) return null;
        for (JsonNode n : namesArray) {
            if ("official".equalsIgnoreCase(n.path("use").asText())) return n;
        }
        return namesArray.get(0);
    }

    // Join all given names with a space
    private String joinGiven(JsonNode givenArray) {
        if (givenArray == null || !givenArray.isArray() || givenArray.size() == 0) return null;
        StringBuilder sb = new StringBuilder();
        Iterator<JsonNode> it = givenArray.elements();
        while (it.hasNext()) {
            String part = it.next().asText();
            if (!part.isBlank()) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(part);
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    // Extract first telecom value of a given system
    private String extractTelecomValue(JsonNode telecomArray, String system) {
        if (telecomArray != null && telecomArray.isArray()) {
            for (JsonNode telecom : telecomArray) {
                if (system.equals(telecom.path("system").asText()) && telecom.has("value")) {
                    return telecom.get("value").asText();
                }
            }
        }
        return null;
    }

    // Map FHIR gender to OMOP concept id
    private int mapGenderToConceptId(String gender) {
        return switch (gender == null ? "" : gender.toLowerCase()) {
            case "male" -> 8507;
            case "female" -> 8532;
            default -> UNKNOWN_GENDER_CONCEPT_ID;
        };
    }
}