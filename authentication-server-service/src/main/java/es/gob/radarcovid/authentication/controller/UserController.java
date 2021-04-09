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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.gob.radarcovid.authentication.api.ErrorDto;
import es.gob.radarcovid.authentication.api.PasswordDto;
import es.gob.radarcovid.authentication.api.ResetPasswordDto;
import es.gob.radarcovid.authentication.api.UserDto;
import es.gob.radarcovid.authentication.business.UserService;
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
@RequestMapping(UserController.USER_ROUTE)
@AllArgsConstructor
@Validated
public class UserController {
	
	public static final String USER_ROUTE = "/user";
	
    private final UserService userService;
	
	@Loggable
	@Secured({ Constants.AUTH_ADMIN, Constants.AUTH_TRACKER })
    @GetMapping(value = "/me")
	@Operation(summary = "Me service", security = @SecurityRequirement(name = Constants.API_KEY_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(schema = @Schema(implementation = UserDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
	})
    public ResponseEntity<UserDto> me(Authentication authentication) {
        return ResponseEntity.ok(userService.findUser(authentication.getName()));
    }
    
	@Loggable
	@Secured({ Constants.AUTH_ADMIN, Constants.AUTH_TRACKER })
	@PostMapping(value = "/password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Change an user password", security = @SecurityRequirement(name = Constants.API_KEY_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Password changed", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) })
	public ResponseEntity<Void> changePassword(
			@Parameter(description = "New and old password to be changed", required = true, schema = @Schema(implementation = PasswordDto.class)) @RequestBody PasswordDto passwordDto,
			Authentication authentication) {
		MDC.put(Constants.TRACKING, "CHANGE_PASSWORD|EMAIL:" + authentication.getName());
		userService.changePassword(authentication.getName(), passwordDto);
		return ResponseEntity.ok(null);
	}
	
	@Loggable
	@Secured({ Constants.AUTH_ADMIN, Constants.AUTH_EMAIL })
	@PostMapping(value = "/password/reset", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Reset an user password", security = @SecurityRequirement(name = Constants.API_KEY_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Password restored", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) })
	public ResponseEntity<Void> resetPassword(
			@Parameter(description = "New password to be changed", required = true, schema = @Schema(implementation = ResetPasswordDto.class)) @RequestBody ResetPasswordDto resetPasswordDto,
			Authentication authentication) {
		MDC.put(Constants.TRACKING, "RESET_PASSWORD|EMAIL:" + authentication.getName());
		userService.resetPassword(authentication.getName(), resetPasswordDto);
		return ResponseEntity.ok(null);
	}
	
}
