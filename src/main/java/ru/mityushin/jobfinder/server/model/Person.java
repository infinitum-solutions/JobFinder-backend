package ru.mityushin.jobfinder.server.model;

import lombok.*;
import ru.mityushin.jobfinder.server.util.enums.Sex;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
@Table(name = "PERSON")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "UUID", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "SEX")
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(name = "COUNTRY")
    private String country;

    @ManyToMany(mappedBy = "subscribers")
    private Set<Organization> organizations;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "PERSON_ROLE",
            joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

    @Column(name = "LOCKED", nullable = false)
    private Boolean locked;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @Column(name = "EXPIRE")
    private ZonedDateTime expire;

    @Column(name = "CREDENTIALS_EXPIRE")
    private ZonedDateTime credentialsExpire;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(uuid, person.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
