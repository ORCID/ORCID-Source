package org.orcid.frontend.spring.web.social.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${org.orcid.social.fb.key}")
	private String fb_key;
	@Value("${org.orcid.social.fb.secret}")
	private String fb_secret;
	@Value("${org.orcid.social.gg.key}")
	private String gg_key;
	@Value("${org.orcid.social.gg.secret}")
	private String gg_secret;

	private SocialContext socialContext;

	private UsersConnectionRepository usersConnectionRepositiory;

	@Resource
	private DataSource simpleDataSource;
	
	@Value("${org.orcid.core.baseUri}")
	private String appUrl;

	@Bean
	public SocialContext socialContext() {

		return socialContext;
	}

	@Bean
	public ConnectionFactoryLocator connectionFactoryLocator() {
		logger.info("getting connectionFactoryLocator");
		ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
		registry.addConnectionFactory(new FacebookConnectionFactory(fb_key, fb_secret));
		registry.addConnectionFactory(new GoogleConnectionFactory(gg_key, gg_secret));
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
		providerSigninController.setPostSignInUrl(appUrl + "/social/access");
		providerSigninController.setApplicationUrl(appUrl);
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