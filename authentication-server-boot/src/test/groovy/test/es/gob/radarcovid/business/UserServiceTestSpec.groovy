/**
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package test.es.gob.radarcovid.business

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

import es.gob.radarcovid.authentication.api.UserDto
import es.gob.radarcovid.authentication.business.UserService
import es.gob.radarcovid.authentication.persistence.repository.UserEntityRepository
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTestSpec extends Specification {

    @Autowired
    private UserEntityRepository userEntityRepository;
	
    @Autowired
    private UserService userService;

    def "save"(String ccaa, String name, String subname) {
        given:
        def size = userEntityRepository.count();

        def userDto = new UserDto();
        userDto.ccaa = ccaa
        userDto.creationDate = new Date().getTime();
        userDto.name = name
        userDto.subname = subname

        when:
        userService.save(userDto)

        then:
        userEntityRepository.count() - size == 1

        where:
        ccaa | name      | subname
        "01" | "Pedro"   | "García Alonso"
        "02" | "Alfonso" | "Marcos García"
    }
}

