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

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.gob.radarcovid.authentication.api.PasswordDto;
import es.gob.radarcovid.authentication.api.ResetPasswordDto;
import es.gob.radarcovid.authentication.api.UserDto;
import es.gob.radarcovid.authentication.business.UserService;
import es.gob.radarcovid.authentication.persistence.CcaaDao;
import es.gob.radarcovid.authentication.persistence.UserDao;
import es.gob.radarcovid.authentication.security.JwtGenerator;
import es.gob.radarcovid.authentication.security.RadarUserDetails;
import es.gob.radarcovid.authentication.util.EmailService;
import es.gob.radarcovid.common.annotation.Loggable;
import es.gob.radarcovid.common.exception.AuthenticationServerException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
	
    private final UserDao userDao;
    private final CcaaDao ccaaDao;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    
    @Loggable
    @Override
    public List<UserDto> getUsers(Integer page, Integer pageSize) {
        return userDao.getAll(page, pageSize);
    }
    
    @Loggable
    @Override
    public Optional<UserDto> getUserById(Long id) {
        return userDao.findById(id);
    }

	@Loggable
	@Override
	public UserDto save(UserDto userDto) {
		checkUserDto(userDto);
		UserDto result = userDao.save(userDto);
		String jwtToken = jwtGenerator.generateEmailJwt(result);
	    emailService.sendVerifyUserMessage(userDto, jwtToken, userDto.getEmail());
		return result;
	}

    @Loggable
    @Override
    public Optional<UserDto> update(UserDto userDto) {
		checkUserDto(userDto);
		return userDao.update(userDto);
    }

    @Loggable
    @Override
    public void delete(Long id) {
        userDao.delete(id);
    }
    
	private void checkUserDto(UserDto userDto) {
		Optional<UserDto> user = userDao.findByEmail(userDto.getEmail(), false);
		if (user.isPresent() && user.get().getId() != userDto.getId()) {
			throw new AuthenticationServerException(HttpStatus.BAD_REQUEST,
					"Email is already used: " + userDto.getEmail());
		}
		if (ccaaDao.findById(userDto.getCcaa()).isEmpty()) {
			throw new AuthenticationServerException(HttpStatus.BAD_REQUEST,
					"CCAA not found: " + userDto.getCcaa());
		}
	}
	
	@Loggable
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDto user = userDao.findByEmail(username, true)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));				
		return RadarUserDetails.builder().user(user).build();
	}
	
	@Override
	public UserDto findUser(String email) {
		return userDao.findByEmail(email, false)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found: " + email));
	}
	
	@Loggable
	@Override
	public void changePassword(String email, PasswordDto passwordDto) {
		UserDto user = userDao.findByEmail(email, true)
				 .orElseThrow(() -> new AuthenticationServerException(HttpStatus.NOT_FOUND, "User not found"));
		 if (!passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
			throw new AuthenticationServerException(HttpStatus.BAD_REQUEST, "Invalid old password");
		 }
		 user.setPassword(passwordDto.getPassword());
		 userDao.update(user);
	}
	
	@Loggable
	@Override
	public void resetPassword(String email, ResetPasswordDto resetPasswordDto) {	
		UserDto user = userDao.findByEmail(email, false)
				 .orElseThrow(() -> new AuthenticationServerException(HttpStatus.NOT_FOUND, "User not found"));
		user.setPassword(resetPasswordDto.getPassword());
		user.setEnabled(true);
		userDao.update(user);
		emailService.sendResetMessage(user, user.getEmail());
	}

}
