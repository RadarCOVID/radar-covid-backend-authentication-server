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

import es.gob.radarcovid.authentication.domain.CcaaDto;
import es.gob.radarcovid.authentication.persistence.entity.CcaaEntity;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CcaaMapper {

	CcaaDto asDto(CcaaEntity ccaaEntity);

	CcaaEntity asEntity(CcaaDto ccaaDto);
	
}
