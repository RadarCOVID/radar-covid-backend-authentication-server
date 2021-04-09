/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.etc;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("application")
@Getter
@Setter
public class AuthenticationProperties {
	
	private final Jwt jwt = new Jwt();
	private final Credentials credentials = new Credentials();
	private final Client client = new Client();
	private final Mail mail = new Mail();
	
    @Getter
    @Setter
    public static class Jwt {
        private String issuer;
        private final Expired expired = new Expired();
        
        @Getter
        @Setter
        public static class Expired {
        	private Duration login;
            private Duration email;
        }
    }

    @Getter
    @Setter
    public static class Credentials {
    	private String privateKey;
        private String publicKey;
        private String algorithm;
    }
    
    @Getter
    @Setter
    public static class Client {
    	private String id;
        private String secret;
    }
    
    @Getter
    @Setter
    public static class Mail {
    	private boolean enabled;
        private String from;
        private final Subjects subjects = new Subjects();
        private String changePassUrl;
        
        @Getter
        @Setter
        public static class Subjects {
        	private String verifyUser;
            private String forgot;
            private String reset;
        }
    }

}
