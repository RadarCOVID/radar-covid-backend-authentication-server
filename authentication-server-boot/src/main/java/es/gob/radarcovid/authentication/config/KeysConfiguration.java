/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.config;

import java.io.IOException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import es.gob.radarcovid.authentication.etc.AuthenticationProperties;
import es.gob.radarcovid.authentication.etc.Constants;
import es.gob.radarcovid.common.exception.AuthenticationServerException;
import es.gob.radarcovid.common.security.KeyVault;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class KeysConfiguration {
    
    private final AuthenticationProperties authProperties;

    @Bean
    KeyVault keyVault() {
        Security.addProvider(new BouncyCastleProvider());
        Security.setProperty("crypto.policy", "unlimited");

        try {
            var radar = createRadarKeys();
            log.debug("Loaded radar keys");
            
            return new KeyVault(radar);
            
        } catch (KeyVault.PrivateKeyNoSuitableEncodingFoundException | KeyVault.PublicKeyNoSuitableEncodingFoundException | IOException e) {
            log.warn("Error loading keys: {}", e.getMessage(), e);
            throw new AuthenticationServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
	private KeyVault.KeyVaultEntry createRadarKeys() throws IOException {
		var privateKey = KeyVault.loadKey(authProperties.getCredentials().getPrivateKey());
		var publicKey = KeyVault.loadKey(authProperties.getCredentials().getPublicKey());

		return new KeyVault.KeyVaultEntry(Constants.PAIR_KEY_RADAR, privateKey, publicKey,
				authProperties.getCredentials().getAlgorithm());
	}
    
}
