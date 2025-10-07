package dev.byowa.hdp.model;

import dev.byowa.hdp.model.clinical.Person;
import jakarta.persistence.*;

@Entity
@Table(name = "users", schema = "omop_cdm")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.PATIENT;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = Role.PATIENT;
    }

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
}