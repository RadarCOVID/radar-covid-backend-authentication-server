/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.business;

import java.util.List;
import java.util.Optional;

import es.gob.radarcovid.authentication.api.PasswordDto;
import es.gob.radarcovid.authentication.api.ResetPasswordDto;
import es.gob.radarcovid.authentication.api.UserDto;

public interface UserService {
	
	List<UserDto> getUsers(Integer page, Integer pageSize);
	
	Optional<UserDto> getUserById(Long id);
	
	UserDto save(UserDto userDto);

	Optional<UserDto> update(UserDto userDto);

	void delete(Long id);
	
	UserDto findUser(String email);
	
	void changePassword(String email, PasswordDto passwordDto);
	
	void resetPassword(String email, ResetPasswordDto passwordDto);

}
