package com.aew.ManagmentAccount.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

/**
 * An authority (a security role) used by Spring Security.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "authority")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@AllArgsConstructor
@NoArgsConstructor
public class Authority implements Serializable {

    private static final long serialVersionUID = -2888662833082264871L;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Id
    @Column(length = 50,name = "name")
    private RoleName name;

}