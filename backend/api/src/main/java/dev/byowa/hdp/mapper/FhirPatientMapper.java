package dev.byowa.hdp.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.vocabulary.Concept;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class FhirPatientMapper {

    public Person mapFhirPatientToOmop(JsonNode fhirPatient) {
        Person person = new Person();

        // id
        person.setId(parseId(fhirPatient.path("id").asText()));

        // gender
        String gender = fhirPatient.path("gender").asText();
        Concept genderConcept = new Concept();
        genderConcept.setId(mapGenderToConceptId(gender)); // setId() muss im Concept-Modell existieren
        person.setGenderSourceConcept(genderConcept);

        // birthDate
        String birthDateStr = fhirPatient.path("birthDate").asText();
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_DATE);
            person.setYearOfBirth(birthDate.getYear());
            person.setMonthOfBirth(birthDate.getMonthValue());
            person.setDayOfBirth(birthDate.getDayOfMonth());
        }

        person.setPersonSourceValue(fhirPatient.path("id").asText());
        return person;
    }

    private Integer parseId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int mapGenderToConceptId(String gender) {
        return switch (gender.toLowerCase()) {
            case "male" -> 8507;
            case "female" -> 8532;
            default -> 0;
        };
    }
}
