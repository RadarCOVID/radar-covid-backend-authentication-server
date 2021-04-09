/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.security.impl;

import java.security.interfaces.ECPublicKey;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import es.gob.radarcovid.authentication.etc.AuthenticationProperties;
import es.gob.radarcovid.authentication.etc.AuthenticationProperties.Jwt.Expired;
import es.gob.radarcovid.authentication.etc.Constants;
import es.gob.radarcovid.authentication.security.JwtValidator;
import es.gob.radarcovid.common.security.KeyVault;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidatorImpl implements JwtValidator {

    private final KeyVault keyVault;
    private final UserDetailsService userDetailsService;
    private final AuthenticationProperties authProperties;
    
    @Override
	public Optional<Authentication> validateToken(String jwtToken) throws JWTVerificationException {
    	ECPublicKey publicKey = (ECPublicKey) keyVault.get(Constants.PAIR_KEY_RADAR).getPublic();   	
		try {
	        Algorithm algorithm = Algorithm.ECDSA512(publicKey, null);
	        JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT jwt = verifier.verify(jwtToken);
			
			Expired expired = authProperties.getJwt().getExpired();
			String scope = jwt.getClaim(Constants.CLAIM_SCOPE_NAME).asString();
			Duration jwtExpiration = Constants.CLAIM_SCOPE_EMAIL.equals(scope) ? expired.getEmail()
					: expired.getLogin();
			
			if (checkTokenExpiration(jwt.getIssuedAt(), jwt.getExpiresAt(), jwtExpiration)) {
				final Set<GrantedAuthority> authorizationList = getAuthorities(scope, jwt.getSubject());
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						jwt.getSubject(), null, authorizationList);
				log.debug("PRINCIPAL:{}|AUTHORITIES:{}", jwt.getSubject(), authorizationList);
				return Optional.of(authenticationToken);
			}

		} catch (TokenExpiredException ex) {
			log.warn("JWT expired ({}). {}", jwtToken, ex.getMessage());
		} catch (UsernameNotFoundException ex) {
			log.warn(ex.getMessage());
		} catch (Exception ex) {
			throw new JWTVerificationException("Token verify error");
		}
		return Optional.empty();
	}
    
    private Set<GrantedAuthority> getAuthorities(String scope, String username) {   	
    	Set<GrantedAuthority> authorizationList = new HashSet<>();
		switch (scope) {
		case Constants.CLAIM_SCOPE_ADMIN:
			authorizationList.add(new SimpleGrantedAuthority(Constants.AUTH_ADMIN));
			break;
		case Constants.CLAIM_SCOPE_EMAIL:
			authorizationList.add(new SimpleGrantedAuthority(Constants.AUTH_EMAIL));
			break;
		case Constants.CLAIM_SCOPE_LOGIN:
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			authorizationList.addAll(userDetails.getAuthorities().stream().collect(Collectors.toSet()));
			break;
		default:
			break;
		}
		return authorizationList;
    }
}
