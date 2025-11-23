package dev.byowa.hdp.service;

import dev.byowa.hdp.mapper.FhirPatientMapper;
import dev.byowa.hdp.mapper.FhirPractitionerMapper;
import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.healthsystem.Location;
import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.repository.LocationRepository;
import dev.byowa.hdp.repository.PersonRepository;
import dev.byowa.hdp.repository.ProviderRepository;
import dev.byowa.hdp.repository.UserRepository;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FhirImportServiceTest {

    @Mock private FhirPatientMapper fhirPatientMapper;
    @Mock private FhirPractitionerMapper fhirPractitionerMapper;
    @Mock private PersonRepository personRepository;
    @Mock private UserRepository userRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private ProviderRepository providerRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private FhirImportService service;

    private Patient buildPatient(String identifier, String given, String family) {
        Patient p = new Patient();
        p.addIdentifier().setValue(identifier);
        p.addName().addGiven(given).setFamily(family);
        p.setActive(true);
        return p;
    }

    private Practitioner buildPractitioner(String identifier, String given, String family) {
        Practitioner pr = new Practitioner();
        pr.addIdentifier().setValue(identifier);
        pr.addName().addGiven(given).setFamily(family);
        return pr;
    }

    @Test
    void processPatient_new_createsPersonLocationAndUser() {
        Patient fhir = buildPatient("PID123", "Alice", "Doe");

        // No existing person
        when(personRepository.findByPersonSourceValue("PID123")).thenReturn(Optional.empty());

        Person mapped = new Person();
        mapped.setPersonSourceValue("PID123");
        when(fhirPatientMapper.mapFhirPatientToOmop(eq(fhir))).thenReturn(mapped);

        when(personRepository.findMaxId()).thenReturn(10);

        Location loc = new Location();
        loc.setLocationSourceValue("LOC-1");
        when(fhirPatientMapper.mapFhirPatientToLocation(fhir)).thenReturn(loc);
        when(locationRepository.findByLocationSourceValue("LOC-1")).thenReturn(Optional.empty());
        when(locationRepository.findMaxId()).thenReturn(5);
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> {
            Location l = inv.getArgument(0);
            l.setId(6);
            return l;
        });

        when(userRepository.existsByUsername("PID123")).thenReturn(false);
        when(passwordEncoder.encode("PID123")).thenReturn("hashed");

        service.processPatient(fhir);

        // Person saved twice (before and after location set)
        verify(personRepository, atLeastOnce()).save(any(Person.class));

        // Location saved
        verify(locationRepository).save(any(Location.class));

        // User created with email and encoded password
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("PID123@hdp.com", savedUser.getUsername());
        assertEquals("hashed", savedUser.getPassword());
        assertSame(mapped, savedUser.getPerson());
    }

    @Test
    void processPatient_existing_updatesPerson_noNewIdAssigned() {
        Patient fhir = buildPatient("PID999", "Bob", "Smith");
        Person existing = new Person();
        existing.setId(42);
        existing.setPersonSourceValue("PID999");
        when(personRepository.findByPersonSourceValue("PID999")).thenReturn(Optional.of(existing));

        Person updated = new Person();
        updated.setId(42);
        updated.setPersonSourceValue("PID999");
        when(fhirPatientMapper.mapFhirPatientToOmop(fhir, existing)).thenReturn(updated);

        // No location change
        when(fhirPatientMapper.mapFhirPatientToLocation(fhir)).thenReturn(null);

        when(userRepository.existsByUsername("PID999")).thenReturn(true); // no new user

        service.processPatient(fhir);

        // Ensure we saved the updated person, not created new id
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository, atLeastOnce()).save(captor.capture());
        assertEquals(42, captor.getValue().getId());
    }

    @Test
    void processPatient_missingIdentifier_throws() {
        Patient fhir = new Patient();
        // no identifier set
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.processPatient(fhir));
        assertTrue(ex.getMessage().toLowerCase().contains("identifier"));
        verifyNoInteractions(personRepository, userRepository, locationRepository, fhirPatientMapper);
    }

    @Test
    void processPatient_inactive_skips() {
        Patient fhir = new Patient();
        fhir.setActive(false);
        // Should return early and not touch repositories or mappers
        service.processPatient(fhir);
        verifyNoInteractions(personRepository, userRepository, locationRepository, fhirPatientMapper);
    }

    @Test
    void processPatient_locationExists_reuseNoNewLocationSaved() {
        Patient fhir = buildPatient("PID777", "Eve", "Adams");

        when(personRepository.findByPersonSourceValue("PID777")).thenReturn(Optional.empty());
        Person mapped = new Person();
        mapped.setPersonSourceValue("PID777");
        when(fhirPatientMapper.mapFhirPatientToOmop(eq(fhir))).thenReturn(mapped);
        when(personRepository.findMaxId()).thenReturn(11);

        Location existingLoc = new Location();
        existingLoc.setId(99);
        existingLoc.setLocationSourceValue("LOC-EXIST");
        when(fhirPatientMapper.mapFhirPatientToLocation(fhir)).thenReturn(existingLoc);
        when(locationRepository.findByLocationSourceValue("LOC-EXIST")).thenReturn(Optional.of(existingLoc));

        when(userRepository.existsByUsername("PID777")).thenReturn(true);

        service.processPatient(fhir);

        // Ensure we did not save a new location
        verify(locationRepository, never()).save(any(Location.class));

        // Person saved and should be associated with existing location at some point
        ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository, atLeastOnce()).save(personCaptor.capture());
        Person lastSaved = personCaptor.getValue();
        assertNotNull(lastSaved.getLocation());
        assertEquals(99, lastSaved.getLocation().getId());
    }

    @Test
    void processPractitioner_existing_updatesProvider_noUserCreated() {
        Practitioner pr = buildPractitioner("DOC2", "Jane", "Doe");
        Provider existing = new Provider();
        existing.setId(7);
        existing.setProviderSourceValue("DOC2");
        when(providerRepository.findByProviderSourceValue("DOC2")).thenReturn(Optional.of(existing));

        Provider updated = new Provider();
        updated.setId(7);
        updated.setProviderSourceValue("DOC2");
        when(fhirPractitionerMapper.mapFhirPractitionerToProvider(pr, existing)).thenReturn(updated);

        when(userRepository.existsByUsername("DOC2")).thenReturn(true); // no new user

        service.processPractitioner(pr);

        verify(providerRepository).save(updated);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void processFhirJson_bundleMissingEntry_throws() {
        Bundle empty = new Bundle();
        String json = ca.uhn.fhir.context.FhirContext.forR4().newJsonParser().encodeResourceToString(empty);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.processFhirJson(json));
        assertTrue(ex.getMessage().toLowerCase().contains("bundle"));
    }

    @Test
    void processFhirJson_unsupportedResourceType_throws() {
        Organization org = new Organization();
        org.setName("ACME Health");
        String json = ca.uhn.fhir.context.FhirContext.forR4().newJsonParser().encodeResourceToString(org);
        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class, () -> service.processFhirJson(json));
        assertTrue(ex.getMessage().toLowerCase().contains("unsupported"));
    }

    @Test
    void processPractitioner_new_createsProviderAndDoctorUser() {
        Practitioner pr = buildPractitioner("DOC1", "John", "Who");
        when(providerRepository.findByProviderSourceValue("DOC1")).thenReturn(Optional.empty());

        Provider mapped = new Provider();
        mapped.setProviderSourceValue("DOC1");
        when(fhirPractitionerMapper.mapFhirPractitionerToProvider(eq(pr), isNull())).thenReturn(mapped);
        when(providerRepository.findMaxId()).thenReturn(3);

        when(userRepository.existsByUsername("DOC1")).thenReturn(false);
        when(passwordEncoder.encode("DOC1")).thenReturn("pwd");

        service.processPractitioner(pr);

        verify(providerRepository).save(mapped);

        ArgumentCaptor<User> uc = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(uc.capture());
        assertEquals("DOC1@hdp.com", uc.getValue().getUsername());
        assertEquals(Role.DOCTOR, uc.getValue().getRole());
    }

    @Test
    void processBundle_mixed_resources_callsEachProcessor() throws Exception {
        Patient pat = buildPatient("P1", "A", "B");
        Practitioner prac = buildPractitioner("D1", "C", "D");
        Bundle bundle = new Bundle();
        bundle.addEntry().setResource(pat);
        bundle.addEntry().setResource(prac);

        // Stub internals to avoid NPEs
        when(personRepository.findByPersonSourceValue("P1")).thenReturn(Optional.empty());
        Person person = new Person();
        person.setPersonSourceValue("P1");
        when(fhirPatientMapper.mapFhirPatientToOmop(any(Patient.class))).thenReturn(person);
        when(personRepository.findMaxId()).thenReturn(1);
        when(fhirPatientMapper.mapFhirPatientToLocation(any(Patient.class))).thenReturn(null);
        when(userRepository.existsByUsername("P1")).thenReturn(true);

        when(providerRepository.findByProviderSourceValue("D1")).thenReturn(Optional.empty());
        Provider provider = new Provider();
        provider.setProviderSourceValue("D1");
        when(fhirPractitionerMapper.mapFhirPractitionerToProvider(any(Practitioner.class), isNull())).thenReturn(provider);
        when(providerRepository.findMaxId()).thenReturn(1);
        when(userRepository.existsByUsername("D1")).thenReturn(true);

        // Process through JSON parser entry point
        String json = ca.uhn.fhir.context.FhirContext.forR4().newJsonParser().encodeResourceToString(bundle);
        service.processFhirJson(json);

        verify(personRepository, atLeastOnce()).save(any(Person.class));
        verify(providerRepository, atLeastOnce()).save(any(Provider.class));
    }
}
