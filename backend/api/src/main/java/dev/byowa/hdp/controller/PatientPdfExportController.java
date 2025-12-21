package dev.byowa.hdp.controller;

import dev.byowa.hdp.service.PatientPdfExportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/export")
public class PatientPdfExportController {

    private final PatientPdfExportService patientPdfExportService;

    public PatientPdfExportController(PatientPdfExportService patientPdfExportService) {
        this.patientPdfExportService = patientPdfExportService;
    }

    /**
     * Patient self-export: uses the currently authenticated user (from JWT / SecurityContext).
     *
     * Example: GET /api/export/my/patient/pdf
     */
    @GetMapping("/my/patient/pdf")
    public ResponseEntity<byte[]> downloadMyPatientPdf() {
        byte[] pdfBytes = patientPdfExportService.generateMyPatientPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("my_patient_report.pdf")
                        .build()
        );

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    /**
     * Doctor/Admin export for a specific patient userId.
     *
     * Example: GET /api/export/patients/123/pdf
     */
    @GetMapping("/patients/{userId}/pdf")
    public ResponseEntity<byte[]> downloadPatientPdf(@PathVariable Long userId) {
        byte[] pdfBytes = patientPdfExportService.generatePatientPdf(userId);

        String filename = "patient_" + userId + "_report.pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(filename)
                        .build()
        );

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
