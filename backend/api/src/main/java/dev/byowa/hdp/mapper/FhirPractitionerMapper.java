package dev.byowa.hdp.mapper;

import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.model.vocabulary.Concept;
import dev.byowa.hdp.repository.ConceptRepository;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FhirPractitionerMapper {

    private final ConceptRepository conceptRepository;

    private static final int UNKNOWN_RACE_CONCEPT_ID = 99999998;
    private static final int UNKNOWN_ETHN_CONCEPT_ID = 99999999;

    public FhirPractitionerMapper(ConceptRepository conceptRepository) {
        this.conceptRepository = conceptRepository;
    }

    public Provider mapFhirPractitionerToProvider(Practitioner fhir, Provider target) {
        Provider provider = (target != null) ? target : new Provider();

        String identifier = null;
        if (fhir.hasIdentifier() && !fhir.getIdentifier().isEmpty()) {
            if (fhir.getIdentifierFirstRep().hasValue()) {
                identifier = fhir.getIdentifierFirstRep().getValue();
            }
        }
        provider.setProviderSourceValue(identifier);

        HumanName name = getPreferredName(fhir.getName());
        if (name != null) {
            String family = blankToNull(name.getFamily());
            String given = joinGiven(name.getGiven());
            String fullName = joinWithSpace(given, family);
            provider.setProviderName(blankToNull(fullName));
        }

        if (fhir.hasBirthDate() && fhir.getBirthDate() != null) {
            LocalDate bd = fhir.getBirthDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            provider.setYearOfBirth(bd.getYear());
        }

        String gender = (fhir.hasGender() && fhir.getGender() != null)
                ? fhir.getGender().toCode()
                : null;
        Integer genderId = mapGenderToConceptId(gender);
        Concept genderConcept = null;
        if (genderId != null && conceptRepository.existsById(genderId)) {
            genderConcept = conceptRepository.getReferenceById(genderId);
        }
        provider.setGenderConcept(genderConcept);
        provider.setGenderSourceConcept(genderConcept);
        provider.setGenderSourceValue(blankToNull(gender));


        return provider;
    }

    private HumanName getPreferredName(List<HumanName> names) {
        if (names == null || names.isEmpty()) return null;
        return names.stream()
                .filter(n -> n.hasUse() && n.getUse() == HumanName.NameUse.OFFICIAL)
                .findFirst()
                .orElse(names.get(0));
    }

    private String joinGiven(List<StringType> givenArray) {
        if (givenArray == null || givenArray.isEmpty()) return null;
        return givenArray.stream()
                .map(StringType::getValue)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
    }

    private String joinWithSpace(String a, String b) {
        if ((a == null || a.isBlank()) && (b == null || b.isBlank())) return null;
        if (a == null || a.isBlank()) return b;
        if (b == null || b.isBlank()) return a;
        return a + " " + b;
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private Integer mapGenderToConceptId(String genderCode) {
        if (genderCode == null) return null;
        return switch (genderCode.toLowerCase()) {
            case "male" -> 8507;
            case "female" -> 8532;
            default -> null;
        };
    }
}
