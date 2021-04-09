/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.auth0.jwt.exceptions.JWTVerificationException;

import es.gob.radarcovid.authentication.etc.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	
	private final JwtValidator jwtValidator;
	private final HandlerExceptionResolver resolver;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		try {
			SecurityContextHolder.clearContext();
			Optional<String> jwtToken = getTokenJWT(request);
			if (jwtToken.isPresent()) {
				Optional<Authentication> authentication = jwtValidator.validateToken(jwtToken.get());
				authentication.ifPresent(SecurityContextHolder.getContext()::setAuthentication);
			} else {
				log.warn("JWT not exists({})", request.getServletPath());
			}
			chain.doFilter(request, response);
		} catch (JWTVerificationException e) {
			log.error("Exception reading token JWT: {}", e.getMessage());
			resolver.resolveException(request, response, null, e);
		}
	}
	
	private Optional<String> getTokenJWT(HttpServletRequest request) {
		String authenticationHeader = request.getHeader(Constants.AUTHORIZATION_HEADER);
		if (!StringUtils.isEmpty(authenticationHeader)
				&& authenticationHeader.startsWith(Constants.AUTHORIZATION_PREFIX)) {
			return Optional.of(authenticationHeader.replace(Constants.AUTHORIZATION_PREFIX, ""));
		}
		return Optional.empty();
	}
}
