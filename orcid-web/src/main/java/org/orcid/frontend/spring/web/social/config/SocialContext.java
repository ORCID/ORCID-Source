
package org.orcid.frontend.spring.web.social.config;

import java.util.Calendar;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * 
 * @author Shobhit Tyagi
 *
 */
public class SocialContext implements ConnectionSignUp, SignInAdapter {

	private static Random rand;

	private final UserCookieGenerator userCookieGenerator;

	private static final ThreadLocal<String> currentUser = new ThreadLocal<String>();

	private final UsersConnectionRepository connectionRepository;

	private final Facebook facebook;

	public SocialContext(UsersConnectionRepository connectionRepository, UserCookieGenerator userCookieGenerator,
			Facebook facebook) {
		this.connectionRepository = connectionRepository;
		this.userCookieGenerator = userCookieGenerator;
		this.facebook = facebook;

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

	public boolean isSignedIn(HttpServletRequest request, HttpServletResponse response) {

		boolean retVal = false;
		String userId = userCookieGenerator.readCookieValue(request);
		if (isValidId(userId)) {

			if (isConnectedFacebookUser(userId)) {
				retVal = true;
			} else {
				userCookieGenerator.removeCookie(response);
			}
		}

		currentUser.set(userId);
		return retVal;
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

	public String getUserId() {

		return currentUser.get();
	}

	public Facebook getFacebook() {
		return facebook;
	}

}
