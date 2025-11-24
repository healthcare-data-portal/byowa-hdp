package dev.byowa.hdp.service;

import dev.byowa.hdp.dto.ProviderDto;
import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.repository.ProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProviderService {
    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Transactional(readOnly = true)
    public Optional<ProviderDto> getProviderById(Integer id) {
        return providerRepository.findById(id).map(this::toDto);
    }

    private ProviderDto toDto(Provider p) {
        ProviderDto dto = new ProviderDto();
        dto.setId(p.getId());
        dto.setProviderName(p.getProviderName());
        dto.setNpi(p.getNpi());
        dto.setDea(p.getDea());
        dto.setYearOfBirth(p.getYearOfBirth());
        dto.setProviderSourceValue(p.getProviderSourceValue());
        dto.setSpecialtySourceValue(p.getSpecialtySourceValue());
        dto.setGenderSourceValue(p.getGenderSourceValue());

        if (p.getSpecialtySourceConcept() != null) dto.setSpecialtySourceConceptId(p.getSpecialtySourceConcept().getId());
        if (p.getGenderSourceConcept() != null) dto.setGenderSourceConceptId(p.getGenderSourceConcept().getId());
        if (p.getSpecialtyConcept() != null) dto.setSpecialtyConceptId(p.getSpecialtyConcept().getId());
        if (p.getGenderConcept() != null) dto.setGenderConceptId(p.getGenderConcept().getId());
        if (p.getCareSite() != null) dto.setCareSiteId(p.getCareSite().getId());

        String initials = "";
        if (dto.getProviderName() != null && !dto.getProviderName().isBlank()) {
            String[] parts = dto.getProviderName().trim().split("\\s+");
            if (parts.length >= 1 && parts[0].length() > 0) initials += parts[0].charAt(0);
            if (parts.length >= 2 && parts[parts.length - 1].length() > 0) initials += parts[parts.length - 1].charAt(0);
            else if (parts[0].length() >= 2) initials += parts[0].charAt(1);
        }
        dto.setInitials(initials.toUpperCase());

        // populate counts using repository native queries
        try {
            Integer pc = providerRepository.countPatientsAssigned(p.getId());
            dto.setPatientCount(pc == null ? 0 : pc);
        } catch (Exception e) {
            dto.setPatientCount(0);
        }

        try {
            Integer rc = providerRepository.countRecordsCreated(p.getId());
            dto.setRecordsCreated(rc == null ? 0 : rc);
        } catch (Exception e) {
            dto.setRecordsCreated(0);
        }

        return dto;
    }
}