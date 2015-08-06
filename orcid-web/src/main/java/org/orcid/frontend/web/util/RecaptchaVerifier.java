package org.orcid.frontend.web.util;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
 
import javax.net.ssl.HttpsURLConnection;

/** Adapted from http://www.journaldev.com/7133/how-to-integrate-google-recaptcha-in-java-web-application
 * Not great code, but I think it will work.
 * 
 * @author tom
 *
 */
public class RecaptchaVerifier {
    
    public static final String url = "https://www.google.com/recaptcha/api/siteverify";
    public static final String secret = "6Ldj5woTAAAAAAzn6-CSk6_BzSiK5boEwQHhoP88";
    private final static String USER_AGENT = "Mozilla/5.0";
 
    public static boolean verify(String gRecaptchaResponse) {
        if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
            return false;
        }
         
        try{
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
 
        // add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
        String postParams = "secret=" + secret + "&response="
                + gRecaptchaResponse;
 
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();
 
        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
 
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString().contains("true");
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
