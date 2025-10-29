// FhirPatientMapper.java
package dev.byowa.hdp.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.clinical.PersonName;
import dev.byowa.hdp.model.vocabulary.Concept;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class FhirPatientMapper {

    public Person mapFhirPatientToOmop(JsonNode fhirPatient) {
        Person person = new Person();

        // Identifier as personSourceValue
        String identifier = null;
        if (fhirPatient.has("identifier") && fhirPatient.get("identifier").isArray() && fhirPatient.get("identifier").size() > 0) {
            identifier = fhirPatient.get("identifier").get(0).path("value").asText(null);
        }
        person.setPersonSourceValue(identifier);

        //name
        // Extract names from FHIR Patient and map to PersonName entity
        if (fhirPatient.has("name") && fhirPatient.get("name").isArray()) {
            JsonNode telecomArray = fhirPatient.get("telecom");
            String email = extractTelecomValue(telecomArray, "email");
            String telephone = extractTelecomValue(telecomArray, "phone");

            for (JsonNode nameNode : fhirPatient.get("name")) {
                String familyName = nameNode.path("family").asText(null);
                String givenName = null;
                String middleName = null;
                // Collect all given names
                if (nameNode.has("given") && nameNode.get("given").isArray()) {
                    if (nameNode.get("given").size() > 0) {
                        givenName = nameNode.get("given").get(0).asText(null);
                    }
                    if (nameNode.get("given").size() > 1) {
                        // Join all further given names as middleName
                        StringBuilder middle = new StringBuilder();
                        for (int i = 1; i < nameNode.get("given").size(); i++) {
                            if (middle.length() > 0) middle.append(" ");
                            middle.append(nameNode.get("given").get(i).asText());
                        }
                        middleName = middle.toString();
                    }
                }
                String use = nameNode.path("use").asText(null);

                // Create and fill PersonName entity
                PersonName personName = new PersonName();
                personName.setPerson(person);
                personName.setFamilyName(familyName);
                personName.setGivenName(givenName);
                personName.setMiddleName(middleName);
                personName.setUse(use);
                personName.setEmail(email);
                personName.setTelephone(telephone);
                person.getPersonNames().add(personName);

            }
        }
        // Gender
        String gender = fhirPatient.path("gender").asText();
        Concept genderConcept = new Concept();
        genderConcept.setId(mapGenderToConceptId(gender));
        person.setGenderConcept(genderConcept);
        person.setGenderSourceConcept(genderConcept);

        // Race (Dummy-Concept, till Mapping implemented)
        Concept raceConcept = new Concept();
        raceConcept.setId(0);
        person.setRaceConcept(raceConcept);
        person.setRaceSourceConcept(raceConcept);

        // Ethnicity (Dummy-Concept, till Mapping implemented)
        Concept ethnicityConcept = new Concept();
        ethnicityConcept.setId(0);
        person.setEthnicityConcept(ethnicityConcept);
        person.setEthnicitySourceConcept(ethnicityConcept);

        // Birthdate
        String birthDateStr = fhirPatient.path("birthDate").asText();
        if (birthDateStr != null && !birthDateStr.isEmpty()) {
            LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_DATE);
            person.setYearOfBirth(birthDate.getYear());
            person.setMonthOfBirth(birthDate.getMonthValue());
            person.setDayOfBirth(birthDate.getDayOfMonth());
        }

        // deceased (ignore for now)
        // maritalStatus (ignore for now)
        // photos (ignore for now)
        // contact (ignore for now)
        // address
        // TODO: "maybe add address to PersonName(PersonInformation)?"

        return person;
    }

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

    private int mapGenderToConceptId(String gender) {
        return switch (gender.toLowerCase()) {
            case "male" -> 8507;
            case "female" -> 8532;
            default -> 0;
        };
    }
}