/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.persistence.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.gob.radarcovid.authentication.api.UserDto;
import es.gob.radarcovid.authentication.persistence.UserDao;
import es.gob.radarcovid.authentication.persistence.entity.UserEntity;
import es.gob.radarcovid.authentication.persistence.mapper.UserMapper;
import es.gob.radarcovid.authentication.persistence.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
	
    private final UserEntityRepository userEntityRepository;
    private final UserMapper userMapper;
    
    @Override
    public List<UserDto> getAll(Integer page, Integer pageSize) {
    	Pageable pageable = PageRequest.of(page, pageSize);
    	return userEntityRepository.findAll(pageable).stream().map(userMapper::asDto).collect(Collectors.toList());
    }
    
    @Override
    public Optional<UserDto> findById(Long id) {
        return userEntityRepository.findById(id).map(userMapper::asDto);
    }
    
	@Override
	public Optional<UserDto> findByEmail(String email, boolean withPassword) {
		Optional<UserEntity> user = userEntityRepository.findByEmail(email);
		return withPassword ? user.map(userMapper::asDtoWithPass) : user.map(userMapper::asDto);
	}

    @Override
    public UserDto save(UserDto userDto) {
    	UserEntity user = userMapper.asEntity(userDto);
    	user.setCreationDate(LocalDateTime.now());
        return userMapper.asDto(userEntityRepository.save(user));
    }
    
    @Override
    public Optional<UserDto> update(UserDto userDto) {
    	Optional<UserEntity> user = userEntityRepository.findById(userDto.getId());
    	if (user.isPresent()) {
    		UserEntity oldUser = user.get();
    		UserEntity newUser = userMapper.asEntity(userDto);
    		oldUser.setEmail(newUser.getEmail());
    		oldUser.setPassword(newUser.getPassword());
    		oldUser.setName(newUser.getName());
    		oldUser.setSubname(newUser.getSubname());
    		oldUser.setPhone(newUser.getPhone());
    		oldUser.setCcaa(newUser.getCcaa());
    		oldUser.setEnabled(newUser.isEnabled());
    		oldUser.setRoles(newUser.getRoles());
    		return Optional.of(userMapper.asDto(userEntityRepository.save(oldUser)));
    	}
    	return Optional.empty();
    }

    @Override
    public void delete(Long id) {
    	if (userEntityRepository.existsById(id)) {
    		userEntityRepository.deleteById(id);
    	}
    }

	@Override
	public void login(Long id) {
		Optional<UserEntity> user = userEntityRepository.findById(id);
		if (user.isPresent()) {
			user.get().setLastLogin(LocalDateTime.now());
			userEntityRepository.save(user.get());
		}
	}

}
