package dev.byowa.hdp.service;

import dev.byowa.hdp.dto.ProviderDto;
import dev.byowa.hdp.model.healthsystem.Provider;
import dev.byowa.hdp.repository.ProviderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Transactional(readOnly = true)
    public Optional<ProviderDto> getCurrentProviderForLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String username = authentication.getName();
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        String identifier = username;
        int atIdx = username.indexOf('@');
        if (atIdx > 0) {
            identifier = username.substring(0, atIdx);
        }

        return providerRepository.findByProviderSourceValue(identifier)
                .map(this::toDto);
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

        if (p.getSpecialtySourceConcept() != null)
            dto.setSpecialtySourceConceptId(p.getSpecialtySourceConcept().getId());
        if (p.getGenderSourceConcept() != null)
            dto.setGenderSourceConceptId(p.getGenderSourceConcept().getId());
        if (p.getSpecialtyConcept() != null)
            dto.setSpecialtyConceptId(p.getSpecialtyConcept().getId());
        if (p.getGenderConcept() != null)
            dto.setGenderConceptId(p.getGenderConcept().getId());
        if (p.getCareSite() != null)
            dto.setCareSiteId(p.getCareSite().getId());

        String initials = "";
        if (p.getProviderName() != null && !p.getProviderName().isBlank()) {
            String[] parts = p.getProviderName().trim().split("\\s+");
            if (parts.length >= 1 && !parts[0].isEmpty())
                initials += parts[0].charAt(0);
            if (parts.length >= 2 && !parts[parts.length - 1].isEmpty())
                initials += parts[parts.length - 1].charAt(0);
            else if (parts[0].length() >= 2)
                initials += parts[0].charAt(1);
        }
        dto.setInitials(initials.toUpperCase());

        dto.setPatientCount(0);
        dto.setRecordsCreated(0);

        return dto;
    }
}
