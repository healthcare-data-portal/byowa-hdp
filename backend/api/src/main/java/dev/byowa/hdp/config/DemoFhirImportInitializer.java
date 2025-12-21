package dev.byowa.hdp.config;

import dev.byowa.hdp.repository.PersonRepository;
import dev.byowa.hdp.service.FhirImportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

@Configuration
@Order(2)
public class DemoFhirImportInitializer {

    private static final String PATIENT_PERSON_SOURCE_VALUE = "patient123";
    private static final String PRACTITIONER_PERSON_SOURCE_VALUE = "doctor123";

    @Bean
    public CommandLineRunner importDemoFhirExamples(
            PersonRepository personRepository,
            FhirImportService fhirImportService
    ) {
        return args -> {

            boolean patientAlreadyImported =
                    personRepository.existsByPersonSourceValue(PATIENT_PERSON_SOURCE_VALUE);

            boolean practitionerAlreadyImported =
                    personRepository.existsByPersonSourceValue(PRACTITIONER_PERSON_SOURCE_VALUE);

            if (patientAlreadyImported && practitionerAlreadyImported) {
                System.out.println("Demo FHIR import skipped: examples already imported (person_source_value present).");
                return;
            }

            // Practitioner first (optional, but nice)
            if (!practitionerAlreadyImported) {
                importFromResources(
                        "fhir-examples/Practitioner/Example Practitioner/Practitioner-Example.json",
                        fhirImportService
                );
            }

            // Patient
            if (!patientAlreadyImported) {
                importFromResources(
                        "fhir-examples/Patient/Example Patient/GeneralPersonExample.json",
                        fhirImportService
                );
            }

            System.out.println("Demo FHIR import finished.");
        };
    }

    private void importFromResources(String classpathLocation, FhirImportService fhirImportService) {
        try {
            ClassPathResource res = new ClassPathResource(classpathLocation);
            if (!res.exists()) {
                System.out.println("Demo FHIR file not found: " + classpathLocation);
                return;
            }

            String json = new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            // This should be the SAME method your /api/fhir/import endpoint uses
            fhirImportService.processFhirJson(json);

            System.out.println("Imported demo FHIR: " + classpathLocation);
        } catch (Exception e) {
            System.out.println("Failed to import demo FHIR: " + classpathLocation);
            e.printStackTrace();
        }
    }
}
