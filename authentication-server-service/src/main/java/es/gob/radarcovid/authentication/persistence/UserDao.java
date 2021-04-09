/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import es.gob.radarcovid.authentication.api.UserDto;

public interface UserDao {

	List<UserDto> getAll(Integer page, Integer pageSize);
	
    Optional<UserDto> findById(Long id);
    
    @Transactional
    Optional<UserDto> findByEmail(String email, boolean withPassword);

    @Transactional
    UserDto save(UserDto userDto);
    
    @Transactional
    Optional<UserDto> update(UserDto userDto);

    @Transactional
    void delete(Long id);
    
    @Transactional
    void login(Long id);
    
}
