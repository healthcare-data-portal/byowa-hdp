package dev.byowa.hdp.controller;
import dev.byowa.hdp.service.FhirImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fhir")
public class FhirImportController {

    @Autowired
    private FhirImportService fhirToOmopService;

    @PostMapping("/import")
    public ResponseEntity<String> importFhirResource(@RequestBody String fhirJson) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            fhirToOmopService.processFhirJson(fhirJson, username);
            return ResponseEntity.ok("FHIR resource(s) imported successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Import failed: " + e.getMessage());
        }
    }
}
