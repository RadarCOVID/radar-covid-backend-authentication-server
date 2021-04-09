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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import es.gob.radarcovid.authentication.controller.LoginController;
import es.gob.radarcovid.authentication.controller.UserController;
import es.gob.radarcovid.authentication.etc.AuthenticationProperties;
import es.gob.radarcovid.authentication.security.JwtAuthorizationFilter;
import es.gob.radarcovid.authentication.security.JwtValidator;
import es.gob.radarcovid.common.config.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {
	
	private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;
    
    @RequiredArgsConstructor
    @Order(0)
    public class JwtConfiguration extends WebSecurityConfigurerAdapter {
    	
    	private final JwtValidator jwtValidator;
    	
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                .headers()
                    .and()
                .antMatcher(UserController.USER_ROUTE + "/**")
                .addFilterAfter(new JwtAuthorizationFilter(jwtValidator, handlerExceptionResolver), 
                		UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                    .and()
                .csrf().disable()
                .cors();
            // @formatter.on
        }
        
        @Override
        @Primary
        @Bean("jwtAuthenticationManagerBean")
        public AuthenticationManager authenticationManagerBean() throws Exception {
          return super.authenticationManagerBean();
        }

    }
    
    @RequiredArgsConstructor
    @Order(1)
    public class BasicConfiguration extends WebSecurityConfigurerAdapter {
    	
        private final AuthenticationProperties authProperties;
    	
        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
            	.withUser(authProperties.getClient().getId())
            	.password(passwordEncoder().encode(authProperties.getClient().getSecret()))
            	.authorities("ROLE_CLIENT_RADAR");
        }
    	
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                .headers()
                    .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, LoginController.LOGIN_ROUTE).authenticated()
                    .antMatchers(HttpMethod.POST, LoginController.FORGOT_ROUTE).authenticated()
                    .antMatchers(HttpMethod.POST, LoginController.VERIFY_EMAIL_ROUTE).authenticated()
                    .anyRequest().permitAll()
                    .and()
                .httpBasic().realmName("radar")
                    .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                    .and()
                .csrf().disable()
                .cors();
            // @formatter.on
        }

        @Override
        @Bean("basicAuthenticationManagerBean")
        public AuthenticationManager authenticationManagerBean() throws Exception {
          return super.authenticationManagerBean();
        }
        
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        
    }
    
}
