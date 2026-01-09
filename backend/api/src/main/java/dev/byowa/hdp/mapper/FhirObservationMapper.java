package dev.byowa.hdp.mapper;

import dev.byowa.hdp.model.clinical.Measurement;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.vocabulary.Concept;
import dev.byowa.hdp.repository.ConceptRepository;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class FhirObservationMapper {

    // Must match ConceptInitializer IDs
    private static final int FHIR_MEASUREMENT_TYPE_CONCEPT_ID = 99999001;

    private static final int C_ABO_GROUP = 99999010;     // 883-9
    private static final int C_RH_TYPE = 99999011;       // 88027-8
    private static final int C_HEART_RATE = 99999012;    // 8867-4
    private static final int C_BODY_TEMP = 99999013;     // 8310-5
    private static final int C_BODY_WEIGHT = 99999014;   // 29463-7
    private static final int C_BODY_HEIGHT = 99999015;   // 8302-2
    private static final int C_BP_SYSTOLIC = 99999016;   // 8480-6
    private static final int C_BP_DIASTOLIC = 99999017;  // 8462-4
    private static final int C_SPO2 = 99999018;          // 59408-5
    private static final int C_RESP_RATE = 99999019;     // 9279-1
    private static final int C_GENERIC_MEASUREMENT = 99999099;

    private final ConceptRepository conceptRepository;

    public FhirObservationMapper(ConceptRepository conceptRepository) {
        this.conceptRepository = conceptRepository;
    }

    /**
     * Map one FHIR Observation into 1..n OMOP Measurement rows.
     * - valueQuantity -> 1 Measurement
     * - valueCodeableConcept -> 1 Measurement (value stored in value_source_value)
     * - component[] -> 1 Measurement per component (e.g. BP sys + dia)
     */
    public List<Measurement> mapObservationToMeasurements(Observation obs, Person person) {
        List<Measurement> out = new ArrayList<>();
        if (obs == null || person == null) return out;

        Concept typeConcept = conceptRepository.getReferenceById(FHIR_MEASUREMENT_TYPE_CONCEPT_ID);

        Instant dt = extractEffectiveInstant(obs);
        LocalDate date = (dt != null)
                ? LocalDateTime.ofInstant(dt, ZoneOffset.UTC).toLocalDate()
                : LocalDate.now();

        // If components exist (e.g. BP), create one Measurement per component
        if (obs.hasComponent() && !obs.getComponent().isEmpty()) {
            for (Observation.ObservationComponentComponent c : obs.getComponent()) {
                Measurement m = baseMeasurement(person, typeConcept, date, dt);

                String loinc = extractLoincCode(c.getCode());
                Concept measConcept = conceptRepository.getReferenceById(mapLoincToConceptId(loinc));
                m.setMeasurementConcept(measConcept);

                // source values
                m.setMeasurementSourceValue(truncate(buildSourceValue(loinc, extractDisplay(c.getCode())), 50));

                // valueQuantity in component
                if (c.hasValueQuantity()) {
                    applyQuantity(m, c.getValueQuantity());
                } else if (c.hasValueCodeableConcept()) {
                    applyCodeableConcept(m, c.getValueCodeableConcept());
                }

                out.add(m);
            }
            return out;
        }

        // Normal: one row for the Observation itself
        Measurement m = baseMeasurement(person, typeConcept, date, dt);

        String loinc = extractLoincCode(obs.getCode());
        Concept measConcept = conceptRepository.getReferenceById(mapLoincToConceptId(loinc));
        m.setMeasurementConcept(measConcept);

        m.setMeasurementSourceValue(truncate(buildSourceValue(loinc, extractDisplay(obs.getCode())), 50));

        if (obs.hasValueQuantity()) {
            applyQuantity(m, obs.getValueQuantity());
        } else if (obs.hasValueCodeableConcept()) {
            applyCodeableConcept(m, obs.getValueCodeableConcept());
        } else {
            // demo-safe fallback: store something
            m.setValueSourceValue(truncate("NO_VALUE", 50));
        }

        out.add(m);
        return out;
    }

    private Measurement baseMeasurement(Person person, Concept typeConcept, LocalDate date, Instant dt) {
        Measurement m = new Measurement();
        m.setPerson(person);
        m.setMeasurementTypeConcept(typeConcept);
        m.setMeasurementDate(date);
        m.setMeasurementDatetime(dt);

        if (dt != null) {
            LocalTime t = LocalDateTime.ofInstant(dt, ZoneOffset.UTC).toLocalTime().withNano(0);
            m.setMeasurementTime(t.toString()); // "HH:mm:ss"
        }
        return m;
    }

    private void applyQuantity(Measurement m, Quantity q) {
        if (q == null) return;

        if (q.hasValue()) {
            m.setValueAsNumber(new BigDecimal(q.getValue().toString()));
        }
        // store unit as text for demo (unitConcept is optional in your entity)
        String unit = q.hasUnit() ? q.getUnit() : (q.hasCode() ? q.getCode() : null);
        if (unit != null) {
            m.setUnitSourceValue(truncate(unit, 50));
        }
    }

    private void applyCodeableConcept(Measurement m, CodeableConcept cc) {
        if (cc == null) return;

        // For demo: store code/display/text in value_source_value (<= 50)
        String value = null;
        if (cc.hasCoding() && cc.getCodingFirstRep().hasCode()) {
            value = cc.getCodingFirstRep().getCode();
        } else if (cc.hasText()) {
            value = cc.getText();
        }
        if (value == null && cc.hasCoding() && cc.getCodingFirstRep().hasDisplay()) {
            value = cc.getCodingFirstRep().getDisplay();
        }

        if (value != null) {
            m.setValueSourceValue(truncate(value, 50));
        }
    }

    private Instant extractEffectiveInstant(Observation obs) {
        if (obs == null) return null;

        // effectiveDateTime
        if (obs.hasEffectiveDateTimeType() && obs.getEffectiveDateTimeType().getValue() != null) {
            return obs.getEffectiveDateTimeType().getValue().toInstant();
        }

        // fallback: issued
        if (obs.hasIssued()) {
            return obs.getIssued().toInstant();
        }

        return null;
    }

    private String extractLoincCode(CodeableConcept code) {
        if (code == null || !code.hasCoding()) return null;
        // Prefer LOINC system, else take first coding code
        for (var coding : code.getCoding()) {
            if ("http://loinc.org".equalsIgnoreCase(coding.getSystem()) && coding.hasCode()) {
                return coding.getCode();
            }
        }
        return code.getCodingFirstRep().hasCode() ? code.getCodingFirstRep().getCode() : null;
    }

    private String extractDisplay(CodeableConcept code) {
        if (code == null) return null;
        if (code.hasCoding() && code.getCodingFirstRep().hasDisplay()) return code.getCodingFirstRep().getDisplay();
        return code.hasText() ? code.getText() : null;
    }

    private String buildSourceValue(String loinc, String display) {
        if (loinc == null && display == null) return "FHIR_OBSERVATION";
        if (loinc != null && display != null) return "LOINC:" + loinc + " " + display;
        if (loinc != null) return "LOINC:" + loinc;
        return display;
    }

    private int mapLoincToConceptId(String loinc) {
        if (loinc == null) return C_GENERIC_MEASUREMENT;
        return switch (loinc) {
            case "883-9" -> C_ABO_GROUP;
            case "88027-8" -> C_RH_TYPE;
            case "8867-4" -> C_HEART_RATE;
            case "8310-5" -> C_BODY_TEMP;
            case "29463-7" -> C_BODY_WEIGHT;
            case "8302-2" -> C_BODY_HEIGHT;
            case "8480-6" -> C_BP_SYSTOLIC;
            case "8462-4" -> C_BP_DIASTOLIC;
            case "59408-5" -> C_SPO2;
            case "9279-1" -> C_RESP_RATE;
            default -> C_GENERIC_MEASUREMENT;
        };
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
