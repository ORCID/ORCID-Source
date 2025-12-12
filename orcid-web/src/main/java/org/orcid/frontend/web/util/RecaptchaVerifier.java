package org.orcid.frontend.web.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RecaptchaVerifier(String url, String secret) {
        this.url = url;
        this.secret = secret;
    }

    public boolean verify(String gRecaptchaResponse) {
        if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
            return false;
        }

        try {
            URL obj = new URL(this.url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            // add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            String postParams = "secret=" + URLEncoder.encode(this.secret, StandardCharsets.UTF_8.name()) + "&response="
                    + URLEncoder.encode(gRecaptchaResponse, StandardCharsets.UTF_8.name());

            // Send post request
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(postParams);
                wr.flush();
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                JsonNode responseJson = objectMapper.readTree(in);
                boolean success = responseJson.path("success").asBoolean(false);
                String hostname = responseJson.path("hostname").asText();
                String errorCodes = responseJson.path("error-codes").toString();
                String secretSuffix = secret != null && secret.length() > 4 ? secret.substring(secret.length() - 4) : "null";
                LOGGER.debug("reCAPTCHA verify result success={}, hostname={}, errorCodes={}, secretSuffix=****{}", success, hostname, secretSuffix, errorCodes);
                if (!success) {
                    LOGGER.warn("reCAPTCHA verification failed, errorCodes={}, hostname={}", errorCodes, hostname);
                }
                return success;
            }
        } catch (Exception e) {
            LOGGER.warn("Error verifying reCAPTCHA", e);
            return false;
        }
    }
}
