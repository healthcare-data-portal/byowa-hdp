package dev.byowa.hdp.service;

import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.clinical.PersonName;
import dev.byowa.hdp.model.healthsystem.Location;
import dev.byowa.hdp.repository.PatientDoctorAssignmentRepository;
import dev.byowa.hdp.repository.UserRepository;
import dev.byowa.hdp.repository.PersonNameRepository;
import dev.byowa.hdp.repository.LocationRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PatientPdfExportService {

    private final UserRepository userRepository;
    private final PatientDoctorAssignmentRepository assignmentRepository;
    private final PersonNameRepository personNameRepository;
    private final LocationRepository locationRepository;

    public PatientPdfExportService(
            UserRepository userRepository,
            PatientDoctorAssignmentRepository assignmentRepository,
            PersonNameRepository personNameRepository,
            LocationRepository locationRepository
    ) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.personNameRepository = personNameRepository;
        this.locationRepository = locationRepository;
    }

    /**
     * Minimal authorization:
     * - ADMIN: allowed
     * - PATIENT: only own PDF
     * - DOCTOR: only if assigned via patient_doctor_assignment
     */
    @Transactional(readOnly = true)
    public byte[] generatePatientPdf(Long patientUserId) {
        User requester = getRequesterUserOrThrow();
        User patientUser = userRepository.findById(patientUserId)
                .orElseThrow(() -> new IllegalArgumentException("Patient user not found"));

        enforceAccess(requester, patientUser);

        // --- Load linked OMOP entities via the schema you provided ---
        Person person = patientUser.getPerson(); // users.person_id -> person.person_id (already mapped in your project)

        Optional<PersonName> personNameOpt =
                (person != null)
                        ? personNameRepository.findTopByPerson_IdOrderByIdDesc(person.getId())
                        : Optional.empty();

        Optional<Location> locationOpt =
                (person != null && person.getId() != null)
                        ? locationRepository.findById(person.getId())
                        : Optional.empty();

        // --- Build display fields (prefer person_name, then users, then fallback) ---
        PersonName pn = personNameOpt.orElse(null);
        Location loc = locationOpt.orElse(null);

        String name =
                nonBlank(joinName(pn))
                        .orElseGet(() -> nonBlank(patientUser.getFullName())
                                .orElse(patientUser.getUsername()));

        String email =
                nonBlank(pn != null ? pn.getEmail() : null)
                        .orElse(patientUser.getUsername());

        String phone =
                nonBlank(pn != null ? pn.getTelephone() : null)
                        .orElse("—");

        String emergencyName =
                nonBlank(pn != null ? pn.getEmergencyContactName() : null)
                        .orElse("—");

        String emergencyPhone =
                nonBlank(pn != null ? pn.getEmergencyContactPhone() : null)
                        .orElse("—");

        String dob = "—";
        String gender = "—";

        if (person != null) {
            if (person.getGenderSourceValue() != null && !person.getGenderSourceValue().isBlank()) {
                gender = person.getGenderSourceValue();
            } else if (person.getGenderConcept() != null) {
                Integer cid = person.getGenderConcept().getId();
                gender = switch (cid) {
                    case 8507 -> "Male";
                    case 8532 -> "Female";
                    case 8521 -> "Diverse";
                    default -> "—";
                };
            }
        }
        if (person != null) {
            Integer y = person.getYearOfBirth();
            Integer m = person.getMonthOfBirth();
            Integer d = person.getDayOfBirth();
            if (y != null && m != null && d != null) {
                dob = LocalDate.of(y, m, d).toString();
            }
        }

        String address = "—";
        if (loc != null) {
            // address_1, city, zip, state, country_source_value
            String a1 = loc.getAddress1();
            String city = loc.getCity();
            String zip = loc.getZip();
            String state = loc.getState();
            String country = loc.getCountrySourceValue();

            address = String.join(", ",
                    safe(a1),
                    safe(zip),
                    safe(city),
                    safe(state),
                    safe(country)
            ).replaceAll("(,\\s*)+$", ""); // trim trailing commas if some fields are empty
            if (address.isBlank()) address = "—";
        }

        try {
            return renderStyledPatientPdf(
                    requester.getUsername(),
                    requester.getRole().name(),
                    patientUser.getId(),
                    person != null ? person.getId() : null,
                    name,
                    email,
                    phone,
                    address,
                    dob,
                    gender,
                    emergencyName,
                    emergencyPhone
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateMyPatientPdf() {
        User requester = getRequesterUserOrThrow();

        // Minimal: only patients (and admins, for convenience) can use this route
        if (requester.getRole() != Role.PATIENT && requester.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only patients can export their own report");
        }

        // Reuse the existing generator (will also enforce access)
        return generatePatientPdf(requester.getId());
    }


    private User getRequesterUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found in database"));
    }

    private void enforceAccess(User requester, User patientUser) {
        Role role = requester.getRole();

        if (role == Role.ADMIN) return;

        if (role == Role.PATIENT) {
            if (!requester.getId().equals(patientUser.getId())) {
                throw new AccessDeniedException("Patients can only export their own report");
            }
            return;
        }

        if (role == Role.DOCTOR) {
            boolean assigned = assignmentRepository.existsByDoctorAndPatient(requester, patientUser);
            if (!assigned) {
                throw new AccessDeniedException("Doctor is not assigned to this patient");
            }
            return;
        }

        throw new AccessDeniedException("Forbidden");
    }

    // -------- helpers --------

    private Optional<String> nonBlank(String s) {
        return (s == null || s.isBlank()) ? Optional.empty() : Optional.of(s);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String joinName(PersonName pn) {
        if (pn == null) return null;
        String given = safe(pn.getGivenName());
        String middle = safe(pn.getMiddleName());
        String family = safe(pn.getFamilyName());

        String joined = String.join(" ",
                given,
                middle,
                family
        ).replaceAll("\\s+", " ").trim();

        return joined.isBlank() ? null : joined;
    }

    /**
     * Simple PDF renderer using PDFBox.
     * Note: requires dependency org.apache.pdfbox:pdfbox.
     */
    private byte[] renderStyledPatientPdf(
            String generatedBy,
            String generatedByRole,
            Long userId,
            Integer personId,
            String name,
            String email,
            String phone,
            String address,
            String dob,
            String gender,
            String emergencyName,
            String emergencyPhone
    ) throws IOException {

        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfCursor c = new PdfCursor(doc);
            c.newPage();

            // Header
            c.title("Patient Report");
            c.smallLine("Generated by: " + generatedBy + " (" + generatedByRole + ")");
            c.smallLine("Generated at: " + java.time.ZonedDateTime.now().toString());
            c.hr();

            // Section: Identity
            c.section("Identity");
            c.kv("User ID", String.valueOf(userId));
            c.kv("Person ID", personId != null ? String.valueOf(personId) : "—");
            c.kv("Name", name);
            c.kv("Email", email);

            c.hrThin();

            // Section: Contact
            c.section("Contact");
            c.kv("Phone", phone);
            c.kv("Address", address);

            c.hrThin();

            // Section: Demographics
            c.section("Demographics");
            c.kv("Date of Birth", dob);
            c.kv("Gender", gender);

            c.hrThin();

            // Section: Emergency Contact
            c.section("Emergency Contact");
            c.kv("Name", emergencyName);
            c.kv("Phone", emergencyPhone);

            // Footer (page number) – simple version
            c.footer("BYOWA-HDP • Confidential");

            doc.save(out);
            return out.toByteArray();
        }
    }

    /** Small helper that manages cursor position, wrapping, and simple layout. */
    private static class PdfCursor {
        private final PDDocument doc;
        private PDPage page;
        private PDPageContentStream cs;

        private final float margin = 50f;
        private final float pageW = PDRectangle.A4.getWidth();
        private final float pageH = PDRectangle.A4.getHeight();

        private float y;

        private final float titleSize = 18f;
        private final float h2Size = 13f;
        private final float bodySize = 11f;
        private final float smallSize = 9f;

        PdfCursor(PDDocument doc) {
            this.doc = doc;
        }

        void newPage() throws IOException {
            if (cs != null) cs.close();
            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            cs = new PDPageContentStream(doc, page);
            y = pageH - margin;
        }

        void title(String text) throws IOException {
            writeLine(text, PDType1Font.HELVETICA_BOLD, titleSize);
            y -= 6;
        }

        void section(String text) throws IOException {
            y -= 10;
            writeLine(text, PDType1Font.HELVETICA_BOLD, h2Size);
            y -= 2;
        }

        void smallLine(String text) throws IOException {
            writeLine(text, PDType1Font.HELVETICA, smallSize);
        }

        void hr() throws IOException {
            y -= 10;
            line(margin, y, pageW - margin, y);
            y -= 14;
        }

        void hrThin() throws IOException {
            y -= 8;
            line(margin, y, pageW - margin, y);
            y -= 12;
        }

        void kv(String key, String value) throws IOException {
            String safeVal = value == null || value.isBlank() ? "—" : value;

            float keyW = 120f; // left column width
            float gap = 10f;
            float xKey = margin;
            float xVal = margin + keyW + gap;
            float valW = (pageW - margin) - xVal;

            // Wrap value if needed
            String[] wrapped = wrap(safeVal, PDType1Font.HELVETICA, bodySize, valW);

            ensureSpace(14f + (wrapped.length - 1) * 12f);

            // Key (first line)
            writeAt(xKey, y, key + ":", PDType1Font.HELVETICA_BOLD, bodySize);

            // Value (can be multi-line)
            writeAt(xVal, y, wrapped[0], PDType1Font.HELVETICA, bodySize);
            y -= 12f;

            for (int i = 1; i < wrapped.length; i++) {
                ensureSpace(12f);
                writeAt(xVal, y, wrapped[i], PDType1Font.HELVETICA, bodySize);
                y -= 12f;
            }

            y -= 2f;
        }

        void footer(String text) throws IOException {
            float footerY = 30f;
            writeAt(margin, footerY, text, PDType1Font.HELVETICA, smallSize);
            cs.close();
        }

        // ----- drawing/text helpers -----

        private void writeLine(String text, org.apache.pdfbox.pdmodel.font.PDFont font, float size) throws IOException {
            ensureSpace(size + 10);
            writeAt(margin, y, text, font, size);
            y -= (size + 6);
        }

        private void writeAt(float x, float y, String text, org.apache.pdfbox.pdmodel.font.PDFont font, float size) throws IOException {
            cs.beginText();
            cs.setFont(font, size);
            cs.newLineAtOffset(x, y);
            cs.showText(text);
            cs.endText();
        }

        private void line(float x1, float y1, float x2, float y2) throws IOException {
            cs.moveTo(x1, y1);
            cs.lineTo(x2, y2);
            cs.stroke();
        }

        private void ensureSpace(float needed) throws IOException {
            if (y - needed < margin) {
                newPage();
            }
        }

        private static String[] wrap(String text, org.apache.pdfbox.pdmodel.font.PDFont font, float size, float maxWidth) throws IOException {
            // Simple whitespace wrap
            String[] words = text.split("\\s+");
            java.util.List<String> lines = new java.util.ArrayList<>();
            StringBuilder line = new StringBuilder();

            for (String w : words) {
                String candidate = line.length() == 0 ? w : (line + " " + w);
                float width = font.getStringWidth(candidate) / 1000f * size;
                if (width <= maxWidth) {
                    line.setLength(0);
                    line.append(candidate);
                } else {
                    if (line.length() > 0) lines.add(line.toString());
                    line.setLength(0);
                    line.append(w);
                }
            }
            if (line.length() > 0) lines.add(line.toString());
            return lines.toArray(new String[0]);
        }
    }

}
