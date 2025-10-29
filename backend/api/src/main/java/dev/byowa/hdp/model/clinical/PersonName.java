// byowa-hdp/backend/api/src/main/java/dev/byowa/hdp/model/clinical/PersonName.java
package dev.byowa.hdp.model.clinical;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "person_name", schema = "omop_cdm")
public class PersonName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_name_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "family_name", length = 100)
    private String familyName;

    @Column(name = "given_name", length = 100)
    private String givenName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "use", length = 30)
    private String use;

    @Column(name = "valid_start_date")
    private LocalDate validStartDate;

    @Column(name = "valid_end_date")
    private LocalDate validEndDate;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telephone", length = 50)
    private String telephone;

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getUse() { return use; }
    public void setUse(String use) { this.use = use; }

    public LocalDate getValidStartDate() { return validStartDate; }
    public void setValidStartDate(LocalDate validStartDate) { this.validStartDate = validStartDate; }

    public LocalDate getValidEndDate() { return validEndDate; }
    public void setValidEndDate(LocalDate validEndDate) { this.validEndDate = validEndDate; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}