package com.bsep.smart.home.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "person")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
@SuperBuilder
@FieldNameConstants
public class Person extends BaseEntity {
    @Column(name = "email", nullable = false)
    String email;

    @ManyToOne
    @JsonManagedReference
    private Role role;

    @Column(name = "password_hash", nullable = false, length = 60)
    @JsonProperty(access = Access.WRITE_ONLY)
    @NotNull
    String passwordHash;

    @Column(name = "verified", nullable = false)
    boolean verified;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    CSR csr;

    @OneToMany(mappedBy = "owner")
    List<Property> ownedProperties;
}
