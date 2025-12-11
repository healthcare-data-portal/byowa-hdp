package dev.byowa.hdp.mapper;

import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.clinical.PersonName;
import dev.byowa.hdp.model.vocabulary.Concept;
import dev.byowa.hdp.model.healthsystem.Location;
import dev.byowa.hdp.repository.ConceptRepository;
import org.springframework.stereotype.Component;

import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.StringType;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

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
    public Person mapFhirPatientToOmop(Patient fhirPatient) {
        return mapFhirPatientToOmop(fhirPatient, null);
    }

    // Map onto an existing Person (replace name list) or create a new one
    public Person mapFhirPatientToOmop(Patient fhirPatient, Person target) {
        Person person = (target != null) ? target : new Person();

        // Identifier -> person_source_value
        String identifier = null;
        if (fhirPatient.hasIdentifier() && !fhirPatient.getIdentifier().isEmpty()) {
            if (fhirPatient.getIdentifierFirstRep().hasValue()) {
                identifier = fhirPatient.getIdentifierFirstRep().getValue();
            }
        }
        person.setPersonSourceValue(identifier);

        // Email/phone from telecom
        List<ContactPoint> telecomList = fhirPatient.getTelecom();
        String email = extractTelecomValue(telecomList, ContactPoint.ContactPointSystem.EMAIL);
        String telephone = extractTelecomValue(telecomList, ContactPoint.ContactPointSystem.PHONE);

        // Build exactly one PersonName (replace existing list)
        person.getPersonNames().clear();
        HumanName preferredName = getPreferredNameNode(fhirPatient.getName());
        if (preferredName != null) {
            String family = preferredName.getFamily();
            String givenCombined = joinGiven(preferredName.getGiven());

            PersonName pn = new PersonName();
            pn.setPerson(person);
            pn.setFamilyName(blankToNull(family));
            pn.setGivenName(blankToNull(givenCombined));
            pn.setEmail(blankToNull(email));
            pn.setTelephone(blankToNull(telephone));
            // --- Emergency contact mapping from Patient.contact[0] ---
            if (fhirPatient.hasContact() && !fhirPatient.getContact().isEmpty()) {
                Patient.ContactComponent contact = fhirPatient.getContactFirstRep();

                // Emergency contact name
                if (contact.hasName() && contact.getName() != null) {
                    HumanName contactName = contact.getName();
                    String contactGiven = joinGiven(contactName.getGiven());
                    String contactFamily = contactName.getFamily();

                    StringBuilder nameBuilder = new StringBuilder();
                    if (contactGiven != null && !contactGiven.isBlank()) {
                        nameBuilder.append(contactGiven.trim());
                    }
                    if (contactFamily != null && !contactFamily.isBlank()) {
                        if (nameBuilder.length() > 0) nameBuilder.append(" ");
                        nameBuilder.append(contactFamily.trim());
                    }

                    String emergencyName = blankToNull(nameBuilder.toString());
                    pn.setEmergencyContactName(emergencyName);
                }

                // Emergency contact phone (from contact.telecom)
                if (contact.hasTelecom()) {
                    String emergencyPhone = extractTelecomValue(
                            contact.getTelecom(),
                            ContactPoint.ContactPointSystem.PHONE
                    );
                    pn.setEmergencyContactPhone(blankToNull(emergencyPhone));
                }
            }
            person.getPersonNames().add(pn);
        }



        // Gender -> only set Concept if it exists to avoid FK errors
        String gender = (fhirPatient.hasGender() && fhirPatient.getGender() != null)
                ? fhirPatient.getGender().toCode()
                : null;
        Integer genderId = mapGenderToConceptId(gender);
        Concept genderConcept = null;
        if (genderId != null && conceptRepository.existsById(genderId)) {
            genderConcept = conceptRepository.getReferenceById(genderId);
        }
        person.setGenderConcept(genderConcept);
        person.setGenderSourceConcept(genderConcept);

        // Race/Ethnicity placeholders only if present in DB
        Concept raceConcept = null;
        if (conceptRepository.existsById(UNKNOWN_RACE_CONCEPT_ID)) {
            raceConcept = conceptRepository.getReferenceById(UNKNOWN_RACE_CONCEPT_ID);
        }
        person.setRaceConcept(raceConcept);
        person.setRaceSourceConcept(raceConcept);

        Concept ethnicityConcept = null;
        if (conceptRepository.existsById(UNKNOWN_ETHN_CONCEPT_ID)) {
            ethnicityConcept = conceptRepository.getReferenceById(UNKNOWN_ETHN_CONCEPT_ID);
        }
        person.setEthnicityConcept(ethnicityConcept);
        person.setEthnicitySourceConcept(ethnicityConcept);

        // Birthdate -> year/month/day
        if (fhirPatient.hasBirthDate() && fhirPatient.getBirthDate() != null) {
            LocalDate birthDate = fhirPatient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            person.setYearOfBirth(birthDate.getYear());
            person.setMonthOfBirth(birthDate.getMonthValue());
            person.setDayOfBirth(birthDate.getDayOfMonth());
        }

        return person;
    }

    // New: map first/preferred FHIR Address to Location entity
    public Location mapFhirPatientToLocation(Patient fhirPatient) {
        List<Address> addresses = fhirPatient.getAddress();
        if (addresses == null || addresses.isEmpty()) return null;

        Address addr = getPreferredAddress(addresses);
        if (addr == null) return null;

        Location loc = new Location();

        // address lines -> address_1 / address_2 (truncate to DB sizes)
        List<StringType> lines = addr.getLine();
        String address1 = (lines != null && !lines.isEmpty()) ? lines.get(0).getValue() : null;
        String address2 = (lines != null && lines.size() > 1) ? lines.get(1).getValue() : null;
        loc.setAddress1(truncate(blankToNull(address1), 50));
        loc.setAddress2(truncate(blankToNull(address2), 50));

        // city / state / zip / county
        loc.setCity(truncate(blankToNull(addr.getCity()), 50));
        // state column is length 2 per model; keep as-is (caller should ensure format)
        loc.setState(truncate(blankToNull(addr.getState()), 2));
        String postal = blankToNull(addr.getPostalCode());
        if (postal != null && postal.length() > 9) postal = postal.substring(0, 9);
        loc.setZip(postal);
        loc.setCounty(truncate(blankToNull(addr.getDistrict()), 20));

        // country_source_value (store verbatim)
        loc.setCountrySourceValue(truncate(blankToNull(addr.getCountry()), 80));

        // Build a location_source_value (concatenation of key address fields) for deduplication
        StringBuilder sb = new StringBuilder();
        appendIfNotBlank(sb, address1);
        appendIfNotBlank(sb, address2);
        appendIfNotBlank(sb, addr.getCity());
        appendIfNotBlank(sb, addr.getState());
        appendIfNotBlank(sb, postal);
        appendIfNotBlank(sb, addr.getCountry());
        String locationSource = sb.length() == 0 ? null : sb.toString();
        if (locationSource != null && locationSource.length() > 50) {
            locationSource = locationSource.substring(0, 50);
        }
        loc.setLocationSourceValue(locationSource);

        // country_concept left null (requires mapping table); latitude/longitude not mapped here
        return loc;
    }

    // Pick "official" name, fallback to first element
    private HumanName getPreferredNameNode(List<HumanName> names) {
        if (names == null || names.isEmpty()) return null;
        for (HumanName n : names) {
            if (HumanName.NameUse.OFFICIAL.equals(n.getUse())) return n;
        }
        return names.get(0);
    }

    // Join all given names with a space
    private String joinGiven(List<StringType> givenArray) {
        if (givenArray == null || givenArray.isEmpty()) return null;
        String combined = givenArray.stream()
                .map(StringType::getValue)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
        return combined.isBlank() ? null : combined;
    }

    // Extract first telecom value of a given system
    private String extractTelecomValue(List<ContactPoint> telecomList, ContactPoint.ContactPointSystem system) {
        if (telecomList != null) {
            for (ContactPoint cp : telecomList) {
                if (system.equals(cp.getSystem()) && cp.hasValue()) {
                    return cp.getValue();
                }
            }
        }
        return null;
    }

    // Map FHIR gender to OMOP concept id (nullable)
    private Integer mapGenderToConceptId(String gender) {
        if (gender == null) return null;
        return switch (gender.toLowerCase()) {
            case "male" -> 8507;
            case "female" -> 8532;
            default -> null;
        };
    }

    // Helper: pick "official" address, fallback to first
    private Address getPreferredAddress(List<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) return null;
        for (Address a : addresses) {
            if (Address.AddressUse.HOME.equals(a.getUse()) || Address.AddressUse.WORK.equals(a.getUse()))
                return a;
        }
        return addresses.get(0);
    }

    // small helpers
    private void appendIfNotBlank(StringBuilder sb, String v) {
        if (v != null && !v.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(v.trim());
        }
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return null;
        return s.length() <= maxLen ? s : s.substring(0, maxLen);
    }
}