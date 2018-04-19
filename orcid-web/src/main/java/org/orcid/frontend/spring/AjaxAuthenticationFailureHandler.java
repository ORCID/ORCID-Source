package org.orcid.frontend.spring;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.security.DeprecatedProfileException;
import org.orcid.core.security.InvalidUserTypeException;
import org.orcid.core.security.UnclaimedProfileExistsException;
import org.orcid.frontend.web.exception.Bad2FARecoveryCodeException;
import org.orcid.frontend.web.exception.Bad2FAVerificationCodeException;
import org.orcid.frontend.web.exception.VerificationCodeFor2FARequiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/*
 * Trying to make spring login for
 * http://stackoverflow.com/questions/10811623/spring-security-programatically-logging-in
 * 
 * @author Robert Peters (rcpeters)
 */
public class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.println("{");
        writer.println("\"success\": false");
        if (exception.getCause() instanceof UnclaimedProfileExistsException) {
            writer.println(",");
            writer.println("\"unclaimed\": true");
        } else if(exception.getCause() instanceof DeprecatedProfileException){
            writer.println(",");
            writer.println("\"deprecated\": true");
            DeprecatedProfileException exc = (DeprecatedProfileException)exception.getCause();
            if(exc != null && exc.getPrimary() != null){
                writer.println(",");
                writer.println("\"primary\":\"" + exc.getPrimary() + "\"");
            }
        } else if(exception.getCause() instanceof DisabledException){
            writer.println(",");
            writer.println("\"disabled\": true");
        } else if (exception instanceof VerificationCodeFor2FARequiredException) {
            writer.println(",");
            writer.println("\"verificationCodeRequired\": true");
        } else if (exception instanceof Bad2FAVerificationCodeException) {
            writer.println(",");
            writer.println("\"badVerificationCode\": true");
            writer.println(",");
            writer.println("\"verificationCodeRequired\": true");
        } else if (exception instanceof Bad2FARecoveryCodeException) {
            writer.println(",");
            writer.println("\"badRecoveryCode\": true");
        } else if (exception.getCause() instanceof InvalidUserTypeException) {
            writer.println(",");
            writer.println("\"invalidUserType\": true");
        }
        writer.println("}");
    }
}
