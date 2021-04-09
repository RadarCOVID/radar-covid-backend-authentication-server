/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package es.gob.radarcovid.authentication.util;

import es.gob.radarcovid.authentication.api.UserDto;

public interface EmailService {
	
	void sendMessage(String subject, String htmlText, String... to);
	
	void sendVerifyUserMessage(UserDto userDto, String jwtToken, String... to);
	
	void sendForgotMessage(UserDto userDto, String jwtToken, String... to);
	
	void sendResetMessage(UserDto userDto, String... to);
}
