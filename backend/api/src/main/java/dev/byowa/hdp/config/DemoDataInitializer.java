package dev.byowa.hdp.config;

import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.model.clinical.Person;
import dev.byowa.hdp.model.clinical.PersonName;
import dev.byowa.hdp.model.healthsystem.Location;
import dev.byowa.hdp.model.vocabulary.Concept;
import dev.byowa.hdp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Order(2) 
public class DemoDataInitializer {

    @Bean
    public CommandLineRunner initDemoData(
            UserRepository userRepository,
            PersonRepository personRepository,
            LocationRepository locationRepository,
            PersonNameRepository personNameRepository,
            ConceptRepository conceptRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("patient1@example.com").isEmpty()) {

                // 1. Create Location
                Location location = new Location();
                location.setId(1);
                location.setAddress1("123 Health St");
                location.setCity("Eindhoven");
                location.setZip("5611");
                location.setCountrySourceValue("Netherlands");
                locationRepository.save(location);

                // 2. Get concepts that should exist from ConceptInitializer
                // If concepts don't exist, use any available concept as fallback
                Concept maleGender = conceptRepository.findById(8507)
                        .or(() -> conceptRepository.findById(8532)) 
                        .or(() -> conceptRepository.findAll().stream().findFirst())
                        .orElseThrow(() -> new RuntimeException("No concepts found in database. ConceptInitializer may not have run."));
                
                Concept race = conceptRepository.findById(8515) 
                        .or(() -> conceptRepository.findAll().stream().findFirst())
                        .orElse(maleGender);
                
                Concept ethnicity = conceptRepository.findById(38003563) 
                        .or(() -> conceptRepository.findAll().stream().findFirst())
                        .orElse(maleGender);

                // 3. Create Person
                Person person = new Person();
                person.setId(1);
                person.setYearOfBirth(1990);
                person.setMonthOfBirth(5);
                person.setDayOfBirth(15);
                person.setPersonSourceValue("BSN123456789");
                person.setLocation(location);
                person.setGenderConcept(maleGender);
                person.setRaceConcept(race);
                person.setEthnicityConcept(ethnicity);
                personRepository.save(person);

                // 4. Create PersonName
                PersonName name = new PersonName();
                name.setGivenName("John");
                name.setFamilyName("Doe");
                name.setEmail("john.doe@example.com");
                name.setTelephone("+31 6 12345678");
                name.setPerson(person);
                personNameRepository.save(name);

                // 5. Create User
                User user = new User();
                user.setUsername("patient1@example.com"); 
                user.setPassword(passwordEncoder.encode("password"));
                user.setRole(Role.PATIENT);
                user.setPerson(person);
                userRepository.save(user);

                System.out.println("Demo data initialized: User 'patient1@example.com' created.");
            }
        };
    }
}
