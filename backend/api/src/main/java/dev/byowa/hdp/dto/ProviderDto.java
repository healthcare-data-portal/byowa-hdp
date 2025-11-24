package dev.byowa.hdp.dto;

public class ProviderDto {
    private Integer id;
    private String providerName;
    private String npi;
    private String dea;
    private Integer yearOfBirth;
    private String providerSourceValue;
    private String specialtySourceValue;
    private Integer specialtySourceConceptId;
    private String genderSourceValue;
    private Integer genderSourceConceptId;
    private Integer specialtyConceptId;
    private Integer genderConceptId;
    private Integer careSiteId;
    private String initials;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public String getNpi() { return npi; }
    public void setNpi(String npi) { this.npi = npi; }
    public String getDea() { return dea; }
    public void setDea(String dea) { this.dea = dea; }
    public Integer getYearOfBirth() { return yearOfBirth; }
    public void setYearOfBirth(Integer yearOfBirth) { this.yearOfBirth = yearOfBirth; }
    public String getProviderSourceValue() { return providerSourceValue; }
    public void setProviderSourceValue(String providerSourceValue) { this.providerSourceValue = providerSourceValue; }
    public String getSpecialtySourceValue() { return specialtySourceValue; }
    public void setSpecialtySourceValue(String specialtySourceValue) { this.specialtySourceValue = specialtySourceValue; }
    public Integer getSpecialtySourceConceptId() { return specialtySourceConceptId; }
    public void setSpecialtySourceConceptId(Integer specialtySourceConceptId) { this.specialtySourceConceptId = specialtySourceConceptId; }
    public String getGenderSourceValue() { return genderSourceValue; }
    public void setGenderSourceValue(String genderSourceValue) { this.genderSourceValue = genderSourceValue; }
    public Integer getGenderSourceConceptId() { return genderSourceConceptId; }
    public void setGenderSourceConceptId(Integer genderSourceConceptId) { this.genderSourceConceptId = genderSourceConceptId; }
    public Integer getSpecialtyConceptId() { return specialtyConceptId; }
    public void setSpecialtyConceptId(Integer specialtyConceptId) { this.specialtyConceptId = specialtyConceptId; }
    public Integer getGenderConceptId() { return genderConceptId; }
    public void setGenderConceptId(Integer genderConceptId) { this.genderConceptId = genderConceptId; }
    public Integer getCareSiteId() { return careSiteId; }
    public void setCareSiteId(Integer careSiteId) { this.careSiteId = careSiteId; }
    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }
}