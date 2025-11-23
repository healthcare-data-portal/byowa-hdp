package dev.byowa.hdp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates example FHIR JSON resources under src/main/resources/fhir-examples using HAPI FHIR (R4).
 * These tests ensure the JSON examples are syntactically valid and contain essential fields
 * expected by our import flow (identifier, name, etc.).
 */
class FhirExamplesValidationTest {

    private final FhirContext ctx = FhirContext.forR4();
    private final IParser parser = ctx.newJsonParser();

    private String readResource(String path) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        assertNotNull(is, "Resource not found on classpath: " + path);
        return new String(is.readAllBytes());
    }

    @Test
    void patient_minimal_isValidAndHasIdentifierAndName() throws Exception {
        String json = readResource("fhir-examples/Patient/Example Patient/Patient-Minimal.json");
        Resource res = (Resource) parser.parseResource(json);
        assertEquals("Patient", res.fhirType());
        Patient p = (Patient) res;
        assertTrue(p.hasIdentifier());
        assertTrue(p.getIdentifierFirstRep().hasValue());
        assertTrue(p.hasName());
        assertTrue(p.getNameFirstRep().hasGiven());
    }

    @Test
    void patient_inactive_isValidAndMarkedInactive() throws Exception {
        String json = readResource("fhir-examples/Patient/Example Patient/Patient-Inactive.json");
        Patient p = (Patient) parser.parseResource(json);
        assertEquals("Patient", p.fhirType());
        assertTrue(p.hasActive());
        assertFalse(p.getActive());
        assertTrue(p.hasIdentifier());
    }

    @Test
    void practitioner_example_isValidWithIdentifierAndName() throws Exception {
        String json = readResource("fhir-examples/Practitioner/Example Practitioner/Practitioner-Example.json");
        Practitioner pr = (Practitioner) parser.parseResource(json);
        assertEquals("Practitioner", pr.fhirType());
        assertTrue(pr.hasIdentifier());
        assertTrue(pr.getIdentifierFirstRep().hasValue());
        assertTrue(pr.hasName());
        assertTrue(pr.getNameFirstRep().hasGiven());
    }

    @Test
    void practitioner_withBirthDate_isValidAndHasBirthDate() throws Exception {
        String json = readResource("fhir-examples/Practitioner/Example Practitioner/Practitioner-WithBirthDate.json");
        Practitioner pr = (Practitioner) parser.parseResource(json);
        assertTrue(pr.hasBirthDate());
    }

    @Test
    void bundle_patientPractitioner_isValidAndContainsEntries() throws Exception {
        String json = readResource("fhir-examples/Bundle/Example Bundle/Bundle-Patient-Practitioner.json");
        Bundle bundle = (Bundle) parser.parseResource(json);
        assertEquals("Bundle", bundle.fhirType());
        assertTrue(bundle.hasEntry());
        assertEquals(2, bundle.getEntry().size());
        assertEquals("Patient", bundle.getEntry().get(0).getResource().fhirType());
        assertEquals("Practitioner", bundle.getEntry().get(1).getResource().fhirType());
    }
}
