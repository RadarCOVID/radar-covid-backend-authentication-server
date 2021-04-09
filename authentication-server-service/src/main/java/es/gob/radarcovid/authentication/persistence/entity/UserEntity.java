/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USER")
public class UserEntity implements Serializable {
    private static final String SEQUENCE_NAME = "SQ_NM_ID_USER";

    @Id
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @Column(name = "NM_USER_ID")
    private Long id;
    
    @Column(name = "DE_EMAIL")
    private String email;
    
    @Column(name = "DE_PASSWORD")
    private String password;

    @Column(name = "DE_NAME")
    private String name;

    @Column(name = "DE_SUBNAME")
    private String subname;

    @Column(name = "DE_PHONE")
    private String phone;

    @Column(name = "FC_CREATION_DATE")
    private LocalDateTime creationDate;

    @Column(name = "FC_UPDATE_DATE")
    private LocalDateTime updateDate;
    
    @Column(name = "FC_LAST_LOGIN")
    private LocalDateTime lastLogin;
    
    @Column(name = "IN_ENABLED")
    private boolean enabled;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DE_CCAA_ID")
    private CcaaEntity ccaa;
    
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "USER_ROLE", joinColumns = @JoinColumn(name = "NM_USER_ID"))
	@Column(name="DE_ROLE_ID")
	private Set<String> roles;
}
