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

public class Constants extends ApiConstants {
	
	public static final String TRACKING = "TRACKING";
	
    public static final String AUTHORIZATION_HEADER = "x-sedia-authorization";
    public static final String AUTHORIZATION_PREFIX = "";
    
    public static final String API_KEY_AUTH = "apiKeyAuth";
    public static final String BASIC_AUTH = "basicAuth";
    public static final String BASIC_SCHEME = "basic";
    
    public static final String PAIR_KEY_RADAR = "radar";
    
    public static final String AUTH_EMAIL = "ROLE_EMAIL";
    
    public static final String CLAIM_SCOPE_NAME = "scope";
    public static final String CLAIM_NAME_NAME = "name";
    public static final String CLAIM_SURNAME_NAME = "surname";
    public static final String CLAIM_AUTHORITIES_NAME = "authorities";
    public static final String CLAIM_CCAA_NAME = "ccaa";
    public static final String CLAIM_SCOPE_ADMIN = "admin";
    public static final String CLAIM_SCOPE_LOGIN = "login";
    public static final String CLAIM_SCOPE_EMAIL = "email";
    
    public static final Integer DEFAULT_PAGE_SIZE = 15;
    
}
