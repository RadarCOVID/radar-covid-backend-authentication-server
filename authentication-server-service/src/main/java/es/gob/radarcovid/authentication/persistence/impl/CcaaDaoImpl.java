/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.persistence.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import es.gob.radarcovid.authentication.domain.CcaaDto;
import es.gob.radarcovid.authentication.persistence.CcaaDao;
import es.gob.radarcovid.authentication.persistence.mapper.CcaaMapper;
import es.gob.radarcovid.authentication.persistence.repository.CcaaEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CcaaDaoImpl implements CcaaDao {
	
    private final CcaaEntityRepository ccaaEntityRepository;
    private final CcaaMapper ccaaMapper;

	@Override
	public Optional<CcaaDto> findById(String id) {
		return ccaaEntityRepository.findById(id).map(ccaaMapper::asDto);
	}

}
