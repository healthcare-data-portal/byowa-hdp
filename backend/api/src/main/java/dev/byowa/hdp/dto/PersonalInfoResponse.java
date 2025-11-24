package dev.byowa.hdp.dto;

public class PersonalInfoResponse {
    // Personal Information
    private String fullName;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String socialSecurityNumber;
    private String gender;

    // Address Information
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getSocialSecurityNumber() { return socialSecurityNumber; }
    public void setSocialSecurityNumber(String socialSecurityNumber) { 
        this.socialSecurityNumber = socialSecurityNumber; 
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
