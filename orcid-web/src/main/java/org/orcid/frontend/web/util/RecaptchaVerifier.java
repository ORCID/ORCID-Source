package org.orcid.frontend.web.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapted from
 * http://www.journaldev.com/7133/how-to-integrate-google-recaptcha-
 * in-java-web-application Not great code, but I think it will work.
 *
 * @author tom
 *
 */
public class RecaptchaVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaVerifier.class);

    private final static String USER_AGENT = "Mozilla/5.0";
    private final String url;
    private final String secret;

    public RecaptchaVerifier(String url, String secret) {
        this.url = url;
        this.secret = secret;
    }

    public boolean verify(String gRecaptchaResponse) {
        if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
            LOGGER.warn("Recaptcha verification failed: response is null or empty");
            return false;
        }

        try {
            BufferedReader in = getBufferedReader(gRecaptchaResponse);
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if(response.toString().contains("true")){
                return true;
            } else {
                LOGGER.warn("Recaptcha verification failed: Response: {}", response.toString());
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Recaptcha verification error", e);
            return false;
        }
    }

    private BufferedReader getBufferedReader(String gRecaptchaResponse) throws IOException {
        URL obj = new URL(this.url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        // add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String postParams = "secret=" + this.secret + "&response=" + gRecaptchaResponse;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        return new BufferedReader(new InputStreamReader(con.getInputStream()));
    }
}
