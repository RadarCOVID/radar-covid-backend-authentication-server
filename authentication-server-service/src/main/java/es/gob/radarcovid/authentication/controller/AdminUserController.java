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

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import es.gob.radarcovid.authentication.api.ErrorDto;
import es.gob.radarcovid.authentication.api.UserDto;
import es.gob.radarcovid.authentication.business.UserService;
import es.gob.radarcovid.authentication.etc.Constants;
import es.gob.radarcovid.common.annotation.Loggable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;

@RestController
@Secured({ Constants.AUTH_ADMIN })
@RequestMapping(AdminUserController.ADMIN_USER_ROUTE)
@AllArgsConstructor
@Validated
public class AdminUserController {
	
	public static final String ADMIN_USER_ROUTE = "/user/admin";
	
    private final UserService userService;
    
	@Loggable
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get all users", security = @SecurityRequirement(name = Constants.API_KEY_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Get all users", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) })
	public ResponseEntity<List<UserDto>> getUsers(
			@Parameter(description = "Page number", required = false, schema = @Schema(implementation = Integer.class)) @RequestParam(value = "page", required = false) Integer page, 
			@Parameter(description = "Page size", required = false, schema = @Schema(implementation = Integer.class)) @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		MDC.put(Constants.TRACKING, "GET_USERS");
		Integer nPage = Optional.ofNullable(page).orElse(0);
		Integer nPageSize = Optional.ofNullable(pageSize).orElse(Constants.DEFAULT_PAGE_SIZE);
		return ResponseEntity.ok(userService.getUsers(nPage, nPageSize));
	}

	@Loggable
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Find user by ID", security = @SecurityRequirement(name = Constants.API_KEY_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Find user by ID", content = @Content(schema = @Schema(implementation = UserDto.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) })
	public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
		MDC.put(Constants.TRACKING, "GET_USER|ID:" + id);
		Optional<UserDto> user = userService.getUserById(id);
		if (user.isPresent()) {
			return ResponseEntity.ok(user.get());
		}
		return ResponseEntity.notFound().build();
	}

	@Loggable
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Saves a new user", security = @SecurityRequirement(name = Constants.API_KEY_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Saves a new user", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) })
	public ResponseEntity<Void> saveUser(
			@Parameter(description = "User to be saved", required = true, schema = @Schema(implementation = UserDto.class)) @Valid @RequestBody UserDto userDto,
			UriComponentsBuilder uriComponentsBuilder) {
		MDC.put(Constants.TRACKING, "INSERT_USER");
		userDto = userService.save(userDto);
	    UriComponents uriComponents = uriComponentsBuilder.path(ADMIN_USER_ROUTE).path("/{id}").buildAndExpand(userDto.getId());
		return ResponseEntity.created(uriComponents.toUri()).build();
	}
	
	@Loggable

	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Updates an user", security = @SecurityRequirement(name = Constants.API_KEY_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Updates an user", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) })
	public ResponseEntity<Void> updateUser(
			@PathVariable Long id,
			@Parameter(description = "User to modify", required = true, schema = @Schema(implementation = UserDto.class)) @Valid @RequestBody UserDto userDto,
			UriComponentsBuilder uriComponentsBuilder) {
		MDC.put(Constants.TRACKING, "UPDATE_USER|ID:" + id);
		userDto.setId(id);
		Optional<UserDto> user = userService.update(userDto);
		if (user.isPresent()) {
			UriComponents uriComponents = uriComponentsBuilder.path(ADMIN_USER_ROUTE).path("/{id}").buildAndExpand(id);
			return ResponseEntity.created(uriComponents.toUri()).build();
		}
		return ResponseEntity.notFound().build();

	}

	@Loggable
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete an user", security = @SecurityRequirement(name = Constants.API_KEY_AUTH))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Delete a user", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(responseCode = "500", description = "Exception", content = @Content(schema = @Schema(implementation = ErrorDto.class))) })
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		MDC.put(Constants.TRACKING, "DELETE_USER|ID:" + id);
		userService.delete(id);
		return ResponseEntity.ok(null);
	}
	
}
