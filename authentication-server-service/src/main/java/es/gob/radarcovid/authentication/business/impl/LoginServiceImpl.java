/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.business.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import es.gob.radarcovid.authentication.api.EmailDto;
import es.gob.radarcovid.authentication.api.LoginDto;
import es.gob.radarcovid.authentication.api.LoginTokenDto;
import es.gob.radarcovid.authentication.api.UserDto;
import es.gob.radarcovid.authentication.business.LoginService;
import es.gob.radarcovid.authentication.persistence.UserDao;
import es.gob.radarcovid.authentication.security.JwtGenerator;
import es.gob.radarcovid.authentication.security.RadarUserDetails;
import es.gob.radarcovid.authentication.util.EmailService;
import es.gob.radarcovid.common.annotation.Loggable;
import es.gob.radarcovid.common.exception.AuthenticationServerException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {
	
    private final UserDao userDao;
    private final JwtGenerator jwtGenerator;    
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
	
	@Loggable
	@Override
	public LoginTokenDto login(LoginDto loginDto) {
		try {
			Authentication auth = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
			UserDto user = ((RadarUserDetails) auth.getPrincipal()).getUser();
			userDao.login(user.getId());
			String token = jwtGenerator.generateJwt(user);
			return LoginTokenDto.builder().token(token).build();
		} catch (AuthenticationException e) {
			throw new AuthenticationServerException(HttpStatus.UNAUTHORIZED, "Invalid username/password supplied");
		}
	}
	
	@Loggable
	@Override
	public void forgetPassword(EmailDto emailDto) {
		UserDto user = userDao.findByEmail(emailDto.getEmail(), false)
				 .orElseThrow(() -> new AuthenticationServerException(HttpStatus.NOT_FOUND, "User not found"));
		String jwtToken = jwtGenerator.generateEmailJwt(user);
	    emailService.sendForgotMessage(user, jwtToken, user.getEmail());
	}

	@Override
	public void sendEmailVerifyUser(EmailDto emailDto) {
		UserDto userDto = userDao.findByEmail(emailDto.getEmail(), false)
				 .orElseThrow(() -> new AuthenticationServerException(HttpStatus.NOT_FOUND, "User not found"));
		String jwtToken = jwtGenerator.generateEmailJwt(userDto);
	    emailService.sendVerifyUserMessage(userDto, jwtToken, userDto.getEmail());
	}

}
