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

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;

public interface JwtValidator {

	public Optional<Authentication> validateToken(String jwtToken) throws JWTVerificationException;
	
    default boolean checkTokenExpiration(Date issuedAt, Date expiresAt, Duration jwtExpiration) throws TokenExpiredException {       
        boolean result = false;
        if (issuedAt != null && expiresAt != null) {           
            Instant expiresAtInstant = Instant.ofEpochMilli(expiresAt.getTime());
            Instant issuedAtInstant = Instant.ofEpochMilli(issuedAt.getTime());
            Instant issuedAtPlusExpiration = issuedAtInstant.plus(jwtExpiration).plusMillis(1000);
            
            boolean isBefore = issuedAtInstant.isBefore(expiresAtInstant) && expiresAtInstant.isBefore(issuedAtPlusExpiration);
            result = issuedAtInstant.isBefore(Instant.now()) && isBefore;
        }
        return result;
    }

}
