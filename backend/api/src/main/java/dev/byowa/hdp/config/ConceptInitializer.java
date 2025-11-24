// Java
package dev.byowa.hdp.config;

import dev.byowa.hdp.model.vocabulary.Concept;
import dev.byowa.hdp.model.vocabulary.ConceptClass;
import dev.byowa.hdp.model.vocabulary.Domain;
import dev.byowa.hdp.model.vocabulary.Vocabulary;
import dev.byowa.hdp.repository.ConceptClassRepository;
import dev.byowa.hdp.repository.ConceptRepository;
import dev.byowa.hdp.repository.DomainRepository;
import dev.byowa.hdp.repository.VocabularyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Order(1)
@Transactional
public class ConceptInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ConceptInitializer.class);

    private static final int ROOT_CONCEPT_ID = 90000000;
    private static final String ROOT_KEY = "ROOT";

    private static final int UNKNOWN_RACE_CONCEPT_ID = 99999998;
    private static final int UNKNOWN_ETHN_CONCEPT_ID = 99999999;

    private final ConceptRepository conceptRepository;
    private final DomainRepository domainRepository;
    private final VocabularyRepository vocabularyRepository;
    private final ConceptClassRepository conceptClassRepository;

    public ConceptInitializer(ConceptRepository conceptRepository,
                              DomainRepository domainRepository,
                              VocabularyRepository vocabularyRepository,
                              ConceptClassRepository conceptClassRepository) {
        this.conceptRepository = conceptRepository;
        this.domainRepository = domainRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.conceptClassRepository = conceptClassRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("[ConceptInitializer] start");
        initRootGraph();
        initUnknownConcepts();
        log.info("[ConceptInitializer] done");
    }

    private void initRootGraph() {
        if (conceptRepository.existsById(ROOT_CONCEPT_ID)) {
            log.info("[ConceptInitializer] ROOT concept already exists");
            return;
        }

        log.info("[ConceptInitializer] 1/3 create Domain/Vocabulary/ConceptClass (no back-refs)");
        Domain d = new Domain();
        d.setDomainId(ROOT_KEY);
        d.setDomainName("Root Domain");
        d = domainRepository.save(d);

        Vocabulary v = new Vocabulary();
        v.setVocabularyId(ROOT_KEY);
        v.setVocabularyName("Root Vocabulary");
        v = vocabularyRepository.save(v);

        ConceptClass cc = new ConceptClass();
        cc.setConceptClassId(ROOT_KEY);
        cc.setConceptClassName("Root ConceptClass");
        cc = conceptClassRepository.save(cc);

        log.info("[ConceptInitializer] 2/3 create ROOT Concept with required FKs set");
        Concept c = new Concept();
        c.setId(ROOT_CONCEPT_ID);
        c.setConceptName("Unknown Root Concept");
        c.setConceptCode(String.valueOf(ROOT_CONCEPT_ID));
        c.setValidStartDate(LocalDate.now());
        c.setValidEndDate(LocalDate.of(2099, 12, 31));
        c.setDomain(d);
        c.setVocabulary(v);
        c.setConceptClass(cc);
        c = conceptRepository.save(c);

        log.info("[ConceptInitializer] 3/3 set back-refs to ROOT Concept");
        d.setDomainConcept(c);
        v.setVocabularyConcept(c);
        cc.setConceptClassConcept(c);
        domainRepository.save(d);
        vocabularyRepository.save(v);
        conceptClassRepository.save(cc);
    }

// Java
// Ergänzung innerhalb von ConceptInitializer:

    private static final int UNKNOWN_GENDER_CONCEPT_ID = 99999997;
    private static final int MALE_CONCEPT_ID = 8507;
    private static final int FEMALE_CONCEPT_ID = 8532;

    private void initUnknownConcepts() {
        Domain d = domainRepository.findById(ROOT_KEY).orElseThrow();
        Vocabulary v = vocabularyRepository.findById(ROOT_KEY).orElseThrow();
        ConceptClass cc = conceptClassRepository.findById(ROOT_KEY).orElseThrow();

        // Gender
        if (!conceptRepository.existsById(MALE_CONCEPT_ID)) {
            Concept male = new Concept();
            male.setId(MALE_CONCEPT_ID);
            male.setConceptName("Male");
            male.setConceptCode(String.valueOf(MALE_CONCEPT_ID));
            male.setValidStartDate(LocalDate.now());
            male.setValidEndDate(LocalDate.of(2099, 12, 31));
            male.setDomain(d);
            male.setVocabulary(v);
            male.setConceptClass(cc);
            conceptRepository.save(male);
            log.info("[ConceptInitializer] created Male {}", MALE_CONCEPT_ID);
        }
        if (!conceptRepository.existsById(FEMALE_CONCEPT_ID)) {
            Concept female = new Concept();
            female.setId(FEMALE_CONCEPT_ID);
            female.setConceptName("Female");
            female.setConceptCode(String.valueOf(FEMALE_CONCEPT_ID));
            female.setValidStartDate(LocalDate.now());
            female.setValidEndDate(LocalDate.of(2099, 12, 31));
            female.setDomain(d);
            female.setVocabulary(v);
            female.setConceptClass(cc);
            conceptRepository.save(female);
            log.info("[ConceptInitializer] created Female {}", FEMALE_CONCEPT_ID);
        }
        if (!conceptRepository.existsById(UNKNOWN_GENDER_CONCEPT_ID)) {
            Concept unknownGender = new Concept();
            unknownGender.setId(UNKNOWN_GENDER_CONCEPT_ID);
            unknownGender.setConceptName("Unknown Gender");
            unknownGender.setConceptCode(String.valueOf(UNKNOWN_GENDER_CONCEPT_ID));
            unknownGender.setValidStartDate(LocalDate.now());
            unknownGender.setValidEndDate(LocalDate.of(2099, 12, 31));
            unknownGender.setDomain(d);
            unknownGender.setVocabulary(v);
            unknownGender.setConceptClass(cc);
            conceptRepository.save(unknownGender);
            log.info("[ConceptInitializer] created Unknown Gender {}", UNKNOWN_GENDER_CONCEPT_ID);
        }

        // Unknown Race
        if (!conceptRepository.existsById(UNKNOWN_RACE_CONCEPT_ID)) {
            Concept unknownRace = new Concept();
            unknownRace.setId(UNKNOWN_RACE_CONCEPT_ID);
            unknownRace.setConceptName("Unknown Race");
            unknownRace.setConceptCode(String.valueOf(UNKNOWN_RACE_CONCEPT_ID));
            unknownRace.setValidStartDate(LocalDate.now());
            unknownRace.setValidEndDate(LocalDate.of(2099, 12, 31));
            unknownRace.setDomain(d);
            unknownRace.setVocabulary(v);
            unknownRace.setConceptClass(cc);
            conceptRepository.save(unknownRace);
            log.info("[ConceptInitializer] created Unknown Race {}", UNKNOWN_RACE_CONCEPT_ID);
        }

        // Unknown Ethnicity
        if (!conceptRepository.existsById(UNKNOWN_ETHN_CONCEPT_ID)) {
            Concept unknownEthn = new Concept();
            unknownEthn.setId(UNKNOWN_ETHN_CONCEPT_ID);
            unknownEthn.setConceptName("Unknown Ethnicity");
            unknownEthn.setConceptCode(String.valueOf(UNKNOWN_ETHN_CONCEPT_ID));
            unknownEthn.setValidStartDate(LocalDate.now());
            unknownEthn.setValidEndDate(LocalDate.of(2099, 12, 31));
            unknownEthn.setDomain(d);
            unknownEthn.setVocabulary(v);
            unknownEthn.setConceptClass(cc);
            conceptRepository.save(unknownEthn);
            log.info("[ConceptInitializer] created Unknown Ethnicity {}", UNKNOWN_ETHN_CONCEPT_ID);
        }
    }
}