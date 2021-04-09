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

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import es.gob.radarcovid.authentication.api.UserDto;
import es.gob.radarcovid.authentication.etc.AuthenticationProperties;
import es.gob.radarcovid.authentication.etc.Constants;
import es.gob.radarcovid.authentication.security.JwtGenerator;
import es.gob.radarcovid.authentication.vo.RoleTypeEnum;
import es.gob.radarcovid.common.security.KeyVault;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtGeneratorImpl implements JwtGenerator {

    private final KeyVault keyVault;
    private final AuthenticationProperties authProperties;
    
    @Override
    public String generateJwt(UserDto userDto) {
        KeyPair keyPair = keyVault.get(Constants.PAIR_KEY_RADAR);
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        Algorithm algorithm = Algorithm.ECDSA512(publicKey, privateKey);
        String jwtId = UUID.randomUUID().toString();
        Date issuedAt = new Date();
        Date validUntil = Date.from(Instant.now().plus(authProperties.getJwt().getExpired().getLogin()));
        
        List<String> authorities = userDto.getRoles().stream()
        		.map(RoleTypeEnum::getCode)
        		.filter(Objects::nonNull)
        		.collect(Collectors.toList());
        
        return JWT.create()
                .withJWTId(jwtId)
                .withSubject(userDto.getEmail())
                .withIssuer(authProperties.getJwt().getIssuer())
                .withIssuedAt(issuedAt)
                .withExpiresAt(validUntil)
                .withClaim(Constants.CLAIM_NAME_NAME, userDto.getName())
                .withClaim(Constants.CLAIM_SURNAME_NAME, userDto.getSubname())
                .withClaim(Constants.CLAIM_AUTHORITIES_NAME, authorities)
                .withClaim(Constants.CLAIM_CCAA_NAME, userDto.getCcaa())
                .withClaim(Constants.CLAIM_SCOPE_NAME, Constants.CLAIM_SCOPE_LOGIN)
                .sign(algorithm);
    }

	@Override
	public String generateEmailJwt(UserDto userDto) {
        KeyPair keyPair = keyVault.get(Constants.PAIR_KEY_RADAR);
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        
        Algorithm algorithm = Algorithm.ECDSA512(publicKey, privateKey);
        String jwtId = UUID.randomUUID().toString();
        Date issuedAt = new Date();
        Date validUntil = Date.from(Instant.now().plus(authProperties.getJwt().getExpired().getEmail()));
        
        return JWT.create()
                .withJWTId(jwtId)
                .withSubject(userDto.getEmail())
                .withIssuer(authProperties.getJwt().getIssuer())
                .withIssuedAt(issuedAt)
                .withExpiresAt(validUntil)
                .withClaim(Constants.CLAIM_SCOPE_NAME, Constants.CLAIM_SCOPE_EMAIL)
                .sign(algorithm);
	}

}