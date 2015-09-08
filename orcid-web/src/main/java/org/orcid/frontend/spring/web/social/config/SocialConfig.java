/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.spring.web.social.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.connect.GoogleConnectionFactory;

/**
 * @author Shobhit Tyagi
 */
@Configuration
public class SocialConfig implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(SocialConfig.class);

	private static final String FB_KEY = "161378537526754";
	private static final String FB_SECRET = "9382f990c575be17af9afd7c8b856532";
	
	private static final String GG_KEY = "729610675962-s5053analpnp9mdf4iqf4hne8v8ud7ng.apps.googleusercontent.com";
	private static final String GG_SECRET="D8nykt6q0IBzvy02CZPYp9Ru";

	private SocialContext socialContext;

	private UsersConnectionRepository usersConnectionRepositiory;

	@Resource
	private DataSource simpleDataSource;

	@Bean
	public SocialContext socialContext() {

		return socialContext;
	}

	@Bean
	public ConnectionFactoryLocator connectionFactoryLocator() {
		logger.info("getting connectionFactoryLocator");
		ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
		registry.addConnectionFactory(new FacebookConnectionFactory(FB_KEY, FB_SECRET));
		registry.addConnectionFactory(new GoogleConnectionFactory(GG_KEY, GG_SECRET));
		return registry;
	}

	@Bean
	public UsersConnectionRepository usersConnectionRepository() {
		return usersConnectionRepositiory;
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public ConnectionRepository connectionRepository() {
		String userId = socialContext.getUserId();
		return usersConnectionRepository().createConnectionRepository(userId);
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public Facebook facebook() {
		String accessToken = connectionRepository().getPrimaryConnection(Facebook.class).createData().getAccessToken();
		return new FacebookTemplate(accessToken);
	}
	
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public Google google() {
		String accessToken = connectionRepository().getPrimaryConnection(Google.class).createData().getAccessToken();
		return new GoogleTemplate(accessToken);
	}
	
	@Bean
	public ProviderSignInController providerSignInController() {
		ProviderSignInController providerSigninController = new ProviderSignInController(connectionFactoryLocator(),
				usersConnectionRepository(), socialContext);
		providerSigninController.setPostSignInUrl("/social/access");
		return providerSigninController;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		JdbcUsersConnectionRepository usersConnectionRepositiory = new JdbcUsersConnectionRepository(simpleDataSource,
				connectionFactoryLocator(), Encryptors.noOpText());
		socialContext = new SocialContext(usersConnectionRepositiory, new UserCookieGenerator(), facebook(), google());
		usersConnectionRepositiory.setConnectionSignUp(socialContext);
		this.usersConnectionRepositiory = usersConnectionRepositiory;
	}
}