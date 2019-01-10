package org.orcid.frontend.spring.web.social.config;

import java.util.Calendar;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.frontend.spring.web.social.GoogleSignIn;
import org.orcid.frontend.spring.web.social.GoogleSignInImpl;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.google.api.Google;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * @author Shobhit Tyagi
 */
public class SocialContext implements ConnectionSignUp, SignInAdapter {

	private static Random rand;

	private final UserCookieGenerator userCookieGenerator;

	private static final ThreadLocal<String> currentUser = new ThreadLocal<String>();

	private final UsersConnectionRepository connectionRepository;

	private final Facebook facebook;

	private final GoogleSignIn google;
	
	public SocialContext(UsersConnectionRepository connectionRepository, UserCookieGenerator userCookieGenerator,
			Facebook facebook, GoogleSignIn google) {
		this.connectionRepository = connectionRepository;
		this.userCookieGenerator = userCookieGenerator;
		this.facebook = facebook;
		this.google = google;

		rand = new Random(Calendar.getInstance().getTimeInMillis());
	}

	@Override
	public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
		userCookieGenerator.addCookie(userId, request.getNativeResponse(HttpServletResponse.class));
		return null;
	}

	@Override
	public String execute(Connection<?> connection) {
		return Long.toString(rand.nextLong());
	}

	public SocialType isSignedIn(HttpServletRequest request, HttpServletResponse response) {

		SocialType connectionType = null;
		String userId = userCookieGenerator.readCookieValue(request);
		if (isValidId(userId)) {

			if (isConnectedFacebookUser(userId)) {
				connectionType = SocialType.FACEBOOK;
			} else if(isConnectedGoogleUser(userId)) {
				connectionType = SocialType.GOOGLE;
			} else {
				userCookieGenerator.removeCookie(response);
			}
		}

		currentUser.set(userId);
		return connectionType;
	}

	private boolean isValidId(String id) {
		return isNotNull(id) && (id.length() > 0);
	}

	private boolean isNotNull(Object obj) {
		return obj != null;
	}

	private boolean isConnectedFacebookUser(String userId) {

		ConnectionRepository connectionRepo = connectionRepository.createConnectionRepository(userId);
		Connection<Facebook> facebookConnection = connectionRepo.findPrimaryConnection(Facebook.class);
		return facebookConnection != null;
	}
	
	private boolean isConnectedGoogleUser(String userId) {

		ConnectionRepository connectionRepo = connectionRepository.createConnectionRepository(userId);
		Connection<Google> googleConnection = connectionRepo.findPrimaryConnection(Google.class);
		return googleConnection != null;
	}

	public String getUserId() {

		return currentUser.get();
	}

	public Facebook getFacebook() {
		return facebook;
	}
	
	public GoogleSignIn getGoogle() {
		return google;
	}
}
