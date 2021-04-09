/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.persistence.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.gob.radarcovid.authentication.api.UserDto;
import es.gob.radarcovid.authentication.persistence.entity.UserEntity;
import es.gob.radarcovid.authentication.vo.RoleTypeEnum;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Mappings({
		@Mapping(target = "ccaa", source = "ccaa.id"),
		@Mapping(target = "password", ignore = true)
	})
	public abstract UserDto asDto(UserEntity userEntity);
	
	@Mappings({
		@Mapping(target = "ccaa", source = "ccaa.id")
	})
	public abstract UserDto asDtoWithPass(UserEntity userEntity);
    
	@Mappings({
		@Mapping(target = "ccaa.id", source = "ccaa"),
		@Mapping(target = "password", source = "password", qualifiedByName = "encode")
	})
	public abstract UserEntity asEntity(UserDto userDto);
	
	protected Long fromLocalDateTime(LocalDateTime localDateTime) {
		return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
	}
	
	protected LocalDateTime fromEpochMilli(Long epochMilli) {
		return epochMilli == null ? null : Instant.ofEpochMilli(epochMilli).atZone(ZoneOffset.UTC).toLocalDateTime();
	}
	
	@Named("encode")
	protected String encode(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}
	
	protected RoleTypeEnum fromString(String str) {
		return RoleTypeEnum.valueFromId(str);
	}
	
	protected String fromRoleTypeEnum(RoleTypeEnum roleTypeEnum) {
		return roleTypeEnum.getId();
	}
}
