/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.authentication.util.impl;

import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import es.gob.radarcovid.authentication.api.UserDto;
import es.gob.radarcovid.authentication.etc.AuthenticationProperties;
import es.gob.radarcovid.authentication.util.EmailService;
import es.gob.radarcovid.common.exception.AuthenticationServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final static String VERIFY_USER_TEMPLATE = "verify_user.html";
	private final static String FORGOT_PASSWORD_TEMPLATE = "forgot_password.html";
	private final static String RESET_PASSWORD_TEMPLATE = "reset_password.html";

	private final JavaMailSender emailSender;
	private final SpringTemplateEngine thymeleafTemplateEngine;
	private final AuthenticationProperties authProperties;

	@Override
	public void sendMessage(String subject, String htmlText, String... to) {
		if (authProperties.getMail().isEnabled()) {
			log.debug("Sending mail with subject {} to {}", subject, Arrays.asList(to));
			try {
				MimeMessage message = emailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message);
				helper.setFrom(authProperties.getMail().getFrom());
				helper.setTo(to);
				helper.setSubject(subject);
				helper.setText(htmlText, true);
				emailSender.send(message);
			} catch (MessagingException e) {
				throw new AuthenticationServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
	}

	@Override
	public void sendVerifyUserMessage(UserDto userDto, String jwtToken, String... to) {
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(authProperties.getMail().getChangePassUrl())
				.queryParam("jwt", jwtToken).build();
		Context ctx = new Context();
		ctx.setVariable("user", userDto);
		ctx.setVariable("url", uriComponents.toString());
		String htmlBody = thymeleafTemplateEngine.process(VERIFY_USER_TEMPLATE, ctx);
		sendMessage(authProperties.getMail().getSubjects().getVerifyUser(), htmlBody, to);
	}

	@Override
	public void sendForgotMessage(UserDto userDto, String jwtToken, String... to) {
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(authProperties.getMail().getChangePassUrl())
				.queryParam("jwt", jwtToken).build();
		Context ctx = new Context();
		ctx.setVariable("user", userDto);
		ctx.setVariable("url", uriComponents.toString());
		String htmlBody = thymeleafTemplateEngine.process(FORGOT_PASSWORD_TEMPLATE, ctx);
		sendMessage(authProperties.getMail().getSubjects().getForgot(), htmlBody, to);
	}

	@Override
	public void sendResetMessage(UserDto userDto, String... to) {
		Context ctx = new Context();
		ctx.setVariable("user", userDto);
		String htmlBody = thymeleafTemplateEngine.process(RESET_PASSWORD_TEMPLATE, ctx);
		sendMessage(authProperties.getMail().getSubjects().getReset(), htmlBody, to);
	}

}
