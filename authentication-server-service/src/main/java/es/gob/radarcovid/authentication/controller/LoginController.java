/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.controller;

import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.gob.radarcovid.authentication.api.ErrorDto;
import es.gob.radarcovid.authentication.api.EmailDto;
import es.gob.radarcovid.authentication.api.LoginDto;
import es.gob.radarcovid.authentication.api.LoginTokenDto;
import es.gob.radarcovid.authentication.business.LoginService;
import es.gob.radarcovid.authentication.etc.Constants;
import es.gob.radarcovid.common.annotation.Loggable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping
@AllArgsConstructor
@Validated
public class LoginController {
	
	public static final String LOGIN_ROUTE = "/login";
	public static final String FORGOT_ROUTE = "/forgot";
	public static final String VERIFY_EMAIL_ROUTE = "/verify/email";
	
    private final LoginService loginService;
    
	@Loggable
	@PostMapping(value = LOGIN_ROUTE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get an authorized token", security = @SecurityRequirement(name = Constants.BASIC_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = LoginTokenDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "401", description = "Invalid username/password supplied", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) 
	})
	public ResponseEntity<LoginTokenDto> login(
			@Parameter(description = "User/password for token", required = true, schema = @Schema(implementation = LoginDto.class)) @RequestBody LoginDto loginDto) {
		MDC.put(Constants.TRACKING, "LOGIN|EMAIL:" + loginDto.getEmail());
		return ResponseEntity.ok(loginService.login(loginDto));
	}
	
	@Loggable
	@PostMapping(value = FORGOT_ROUTE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Forgot service. Send an email to reset password", security = @SecurityRequirement(name = Constants.BASIC_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Email sent", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) })
	public ResponseEntity<Void> forgotPassword(
			@Parameter(description = "Email to send message", required = true, schema = @Schema(implementation = EmailDto.class)) @RequestBody EmailDto emailDto) {
		MDC.put(Constants.TRACKING, "FORGET_PASSWORD|EMAIL:" + emailDto.getEmail());
		loginService.forgetPassword(emailDto);
		return ResponseEntity.ok(null);
	}
	
	@Loggable
	@PostMapping(value = VERIFY_EMAIL_ROUTE,produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Verify email service. Send an email to verify an user", security = @SecurityRequirement(name = Constants.BASIC_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Email sent", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) })
	public ResponseEntity<Void> sendVerifyEmail(
			@Parameter(description = "Email to send message", required = true, schema = @Schema(implementation = EmailDto.class)) @RequestBody EmailDto emailDto) {
		MDC.put(Constants.TRACKING, "SEND_VERIFY_EMAIL|EMAIL:" + emailDto.getEmail());
		loginService.sendEmailVerifyUser(emailDto);
		return ResponseEntity.ok(null);
	}
	
}
