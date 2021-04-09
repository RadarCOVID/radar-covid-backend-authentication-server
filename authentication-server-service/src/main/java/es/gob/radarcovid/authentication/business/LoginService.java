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

import es.gob.radarcovid.authentication.api.EmailDto;
import es.gob.radarcovid.authentication.api.LoginDto;
import es.gob.radarcovid.authentication.api.LoginTokenDto;

public interface LoginService {
	
	LoginTokenDto login(LoginDto loginDto);
	void forgetPassword(EmailDto emailDto);
	void sendEmailVerifyUser(EmailDto emailDto);
	
}
