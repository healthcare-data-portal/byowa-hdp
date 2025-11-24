BEGIN;

-- Minimal concept rows used by Person/Provider (safe: will not overwrite existing)
INSERT INTO omop_cdm.concept (concept_id, concept_name, domain_id, vocabulary_id, concept_class_id, concept_code, standard_concept, valid_start_date, valid_end_date)
VALUES
  (9000001, 'Male (test)', 'Gender', 'TEST', 'Standard', 'M', 'S', '2000-01-01', '2099-12-31')
ON CONFLICT (concept_id) DO NOTHING;

INSERT INTO omop_cdm.concept (concept_id, concept_name, domain_id, vocabulary_id, concept_class_id, concept_code, standard_concept, valid_start_date, valid_end_date)
VALUES
  (9000002, 'Female (test)', 'Gender', 'TEST', 'Standard', 'F', 'S', '2000-01-01', '2099-12-31')
ON CONFLICT (concept_id) DO NOTHING;

INSERT INTO omop_cdm.concept (concept_id, concept_name, domain_id, vocabulary_id, concept_class_id, concept_code, standard_concept, valid_start_date, valid_end_date)
VALUES
  (9100001, 'Race: Unknown (test)', 'Race', 'TEST', 'Standard', 'UNK', 'S', '2000-01-01', '2099-12-31')
ON CONFLICT (concept_id) DO NOTHING;

INSERT INTO omop_cdm.concept (concept_id, concept_name, domain_id, vocabulary_id, concept_class_id, concept_code, standard_concept, valid_start_date, valid_end_date)
VALUES
  (9200001, 'Ethnicity: Unknown (test)', 'Ethnicity', 'TEST', 'Standard', 'UNK', 'S', '2000-01-01', '2099-12-31')
ON CONFLICT (concept_id) DO NOTHING;

-- Care site (optional)
INSERT INTO omop_cdm.care_site (care_site_id, care_site_name, place_of_service_concept_id)
VALUES (7001, 'Test Clinic A', 0)
ON CONFLICT (care_site_id) DO NOTHING;

-- Provider: the doctor used by the UI tests
INSERT INTO omop_cdm.provider (
  provider_id, provider_name, npi, dea, specialty_concept_id, care_site_id, year_of_birth,
  gender_concept_id, provider_source_value, specialty_source_value, specialty_source_concept_id,
  gender_source_value, gender_source_concept_id
)
VALUES (
  1001,
  'Dr. John Smith',
  '9999999999',
  'DEA-TEST',
  NULL,
  7001,
  1980,
  9000001,
  'dr.john.smith',
  'Cardiology',
  NULL,
  'M',
  9000001
)
ON CONFLICT (provider_id) DO NOTHING;

-- Persons (patients) assigned to provider 1001
INSERT INTO omop_cdm.person (person_id, gender_concept_id, year_of_birth, month_of_birth, day_of_birth, race_concept_id, ethnicity_concept_id, provider_id, person_source_value)
VALUES
  (2001, 9000001, 1975, 6, 12, 9100001, 9200001, 1001, 'patient-2001'),
  (2002, 9000002, 1988, 11, 3, 9100001, 9200001, 1001, 'patient-2002'),
  (2003, 9000001, 1992, 1, 20, 9100001, 9200001, 1001, 'patient-2003')
ON CONFLICT (person_id) DO NOTHING;

-- Optional: person_name table if present (some OMOP variants)
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema='omop_cdm' AND table_name='person_name') THEN
    INSERT INTO omop_cdm.person_name (person_id, person_name, given_name, family_name, name_type)
    VALUES
      (2001, 'Alice Example', 'Alice', 'Example', 'Official'),
      (2002, 'Bob Sample', 'Bob', 'Sample', 'Official'),
      (2003, 'Carl Demo', 'Carl', 'Demo', 'Official')
    ON CONFLICT DO NOTHING;
  END IF;
END$$;

-- Visits for patients (provider_id included)
INSERT INTO omop_cdm.visit_occurrence (visit_occurrence_id, person_id, visit_concept_id, visit_start_date, visit_end_date, provider_id, visit_source_value)
VALUES
  (3001, 2001, 9201, '2025-01-10', '2025-01-10', 1001, 'visit-3001'),
  (3002, 2002, 9201, '2025-02-05', '2025-02-05', 1001, 'visit-3002')
ON CONFLICT (visit_occurrence_id) DO NOTHING;

-- Condition occurrences authored/assigned to the provider
INSERT INTO omop_cdm.condition_occurrence (condition_occurrence_id, person_id, condition_concept_id, condition_start_date, condition_type_concept_id, provider_id)
VALUES
  (4001, 2001, 100, '2025-01-10', 38000275, 1001),
  (4002, 2002, 200, '2025-02-05', 38000275, 1001)
ON CONFLICT (condition_occurrence_id) DO NOTHING;

-- Measurements (labs)
INSERT INTO omop_cdm.measurement (measurement_id, person_id, measurement_concept_id, measurement_date, measurement_type_concept_id, provider_id, value_as_number)
VALUES
  (5001, 2001, 300, '2025-01-10', 44818702, 1001, 5.4),
  (5002, 2003, 301, '2025-03-01', 44818702, 1001, 120)
ON CONFLICT (measurement_id) DO NOTHING;

-- Observations
INSERT INTO omop_cdm.observation (observation_id, person_id, observation_concept_id, observation_date, observation_type_concept_id, provider_id, value_as_string)
VALUES
  (6001, 2001, 400, '2025-01-10', 38000275, 1001, 'smoker: no'),
  (6002, 2002, 401, '2025-02-05', 38000275, 1001, 'allergy: penicillin')
ON CONFLICT (observation_id) DO NOTHING;

-- Procedure occurrences
INSERT INTO omop_cdm.procedure_occurrence (procedure_occurrence_id, person_id, procedure_concept_id, procedure_date, procedure_type_concept_id, provider_id)
VALUES
  (7001, 2001, 500, '2025-01-10', 38000275, 1001)
ON CONFLICT (procedure_occurrence_id) DO NOTHING;

-- Drug exposures
INSERT INTO omop_cdm.drug_exposure (drug_exposure_id, person_id, drug_concept_id, drug_exposure_start_date, drug_type_concept_id, provider_id)
VALUES
  (8001, 2002, 600, '2025-02-06', 38000177, 1001)
ON CONFLICT (drug_exposure_id) DO NOTHING;

-- Device exposures
INSERT INTO omop_cdm.device_exposure (device_exposure_id, person_id, device_concept_id, device_exposure_start_date, provider_id)
VALUES
  (8101, 2003, 700, '2025-03-01', 1001)
ON CONFLICT (device_exposure_id) DO NOTHING;

-- Notes authored by provider
INSERT INTO omop_cdm.note (note_id, person_id, note_date, note_type_concept_id, note_class_concept_id, provider_id, note_text)
VALUES
  (9001, 2001, '2025-01-10', 38000275, 38000275, 1001, 'Initial consult - test data'),
  (9002, 2002, '2025-02-05', 38000275, 38000275, 1001, 'Followup - test data')
ON CONFLICT (note_id) DO NOTHING;

COMMIT;