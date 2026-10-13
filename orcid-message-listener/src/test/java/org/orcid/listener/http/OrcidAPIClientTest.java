package org.orcid.listener.http;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import jakarta.ws.rs.core.MediaType;

public class OrcidAPIClientTest {

    private HttpServer server;
    private int port;
    private OrcidAPIClient orcidAPIClient;

    @Before
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        port = server.getAddress().getPort();
        orcidAPIClient = new OrcidAPIClient(10);
    }

    @After
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void testGetActivityHeadersAndBody() throws Exception {
        String testXml = "<employment:employment xmlns:employment=\"http://www.orcid.org/ns/employment\" put-code=\"17\"/>";
        AtomicReference<String> acceptHeader = new AtomicReference<>();
        AtomicReference<String> userAgentHeader = new AtomicReference<>();

        server.createContext("/v3.0/0000-0002-7966-8243/employment/17", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                acceptHeader.set(exchange.getRequestHeaders().getFirst("Accept"));
                userAgentHeader.set(exchange.getRequestHeaders().getFirst("User-Agent"));

                byte[] responseBytes = testXml.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", MediaType.APPLICATION_XML);
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        });
        server.start();

        String url = "http://127.0.0.1:" + port + "/v3.0/0000-0002-7966-8243/employment/17";
        HttpResponse<byte[]> response = orcidAPIClient.getActivity(url);

        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertArrayEquals(testXml.getBytes(StandardCharsets.UTF_8), response.body());
        assertEquals(MediaType.APPLICATION_XML, acceptHeader.get());
        assertEquals("orcid/message-listener", userAgentHeader.get());
    }
}
