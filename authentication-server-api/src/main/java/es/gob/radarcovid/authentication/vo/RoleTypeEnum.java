/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.vo;

import java.util.HashMap;
import java.util.Map;

import es.gob.radarcovid.authentication.etc.ApiConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RoleTypeEnum {
	
	ROLE_ADMIN("A", ApiConstants.AUTH_ADMIN), 
	ROLE_TRACKER("T", ApiConstants.AUTH_TRACKER);
	
    private static final Map<String, RoleTypeEnum> roleTypeEnumById;

    static {
    	roleTypeEnumById = new HashMap<>();
        for (RoleTypeEnum roleTypeEnum : RoleTypeEnum.values()) {
        	roleTypeEnumById.put(roleTypeEnum.getId(), roleTypeEnum);
        }
    }
	
    @Getter
    private final String id;
    
    @Getter
    private final String code;

    public static RoleTypeEnum valueFromId(String id) {
    	RoleTypeEnum result = roleTypeEnumById.get(id);
        if (result == null)
            throw new IllegalArgumentException("No RoleTypeEnum with id " + id);
        return result;
    }

}
