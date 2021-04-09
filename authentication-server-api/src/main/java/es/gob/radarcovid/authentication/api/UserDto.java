/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.api;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import es.gob.radarcovid.authentication.vo.RoleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto implements Serializable {
	
	private Long id;
	
    @NotNull
    @Size(max = 255)
    private String email;
    
    @NotNull
    @Size(max = 72)
    private String password;
    
    @NotNull
    @Size(max = 50)
    private String name;
    
    @Size(max = 100)
    private String subname;
    
    @Size(max = 15)
    private String phone;
    
    @Schema(description = "User creation date in milliseconds since January 1, 1970, 00:00:00 GTM")
    private Long creationDate;

    @Schema(description = "User last update date in milliseconds since January 1, 1970, 00:00:00 GTM")
    private Long updateDate;

    @NotNull
    @Size(min = 2, max = 2)
    private String ccaa;
    
    private boolean enabled;
    
    @NotNull
    @Size(min = 1)
	private List<RoleTypeEnum> roles;
    
}
