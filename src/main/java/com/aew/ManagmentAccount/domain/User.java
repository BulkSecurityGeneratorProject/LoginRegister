package com.aew.ManagmentAccount.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A user.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    private static final long serialVersionUID = 7894114616549874L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "*Please provide your username")
    @Size(min = 1, max = 60, message = "*Your username must have at least 1 characters")
    @Column(length = 60, unique = true, nullable = false)
    private String login;

    @NotNull(message = "*Please provide your password")
    @Size(min = 5, max = 60, message = "*Your password must have at least 5 characters")
    @Column(name = "password_hash", length = 60, nullable = false)
    private String password;

    @NotNull(message = "*Please provide your first name")
    @Size(min = 1, max = 60, message = "*Your name must have at least 1 characters")
    @Column(name = "first_name", length = 50)
    private String firstName;

    @NotNull(message = "*Please provide your last name")
    @Size(min = 1, max = 60, message = "*Your last name must have at least 1 characters")
    @Column(name = "last_name", length = 50)
    private String lastName;

    @NotNull(message = "*Please provide an email")
    @Email(message = "Please provide a valid Email")
    @Size(min = 5, max = 254)
    @Column(length = 254, unique = true)
    private String email;

    @Builder.Default
    @NotNull
    @Column(nullable = false)
    private boolean activated = false;

    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_authority", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
                    @JoinColumn(name = "authority_name", referencedColumnName = "name") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @BatchSize(size = 20)
    private Set<Authority> authorities = new HashSet<>();

    @Builder.Default
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    @JsonIgnore
    private Instant createdDate = Instant.now();

}