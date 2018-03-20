package org.orcid.core.cli;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;

/**
 * 
 * @author Will Simpson
 * 
 */
public class GetFromSalesForce {

    private static Logger LOGGER = LoggerFactory.getLogger(GetFromSalesForce.class);

    @Option(name = "-q", usage = "SOQL query (not url encoded)")
    private String query;
    @Option(name = "-f", usage = "File containting SOQL query (not url encoded)")
    private File queryFile;
    @Option(name = "-h", usage = "The hostname of the SalesForce API", required = true)
    private String hostName;
    @Option(name = "-a", usage = "The access token to use", required = true)
    private String accessToken;

    private Client client;

    public static void main(String[] args) throws IOException {
        GetFromSalesForce thisObject = new GetFromSalesForce();
        CmdLineParser parser = new CmdLineParser(thisObject);
        try {
            parser.parseArgument(args);
            thisObject.validateArgs(parser);
            thisObject.init();
            thisObject.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            System.exit(2);
        }
        System.exit(0);
    }

    private void validateArgs(CmdLineParser parser) {
        if (StringUtils.isBlank(query) && queryFile == null) {
            throw new RuntimeException("At least one of -q or -f must be secified");
        }
    }

    public void execute() throws IOException {
        WebResource resource = createQueryResource(query);
        ClientResponse response = doGetRequest(resource, accessToken);
        System.out.println(response.getEntity(String.class));
    }

    private void init() {
        client = Client.create();
        client.addFilter(new LoggingFilter());
        if (StringUtils.isBlank(query)) {
            try {
                query = IOUtils.toString(new FileReader(queryFile));
            } catch (IOException e) {
                throw new RuntimeException("Error reading query file", e);
            }
        }
    }

    private WebResource createQueryResource(String query) {
        return client.resource(hostName).path("services/data/v20.0/query").queryParam("q", query);
    }

    private ClientResponse doGetRequest(WebResource resource, String accessToken) {
        return resource.header("Authorization", "Bearer " + accessToken).header("X-PrettyPrint", "1").accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
    }

}
