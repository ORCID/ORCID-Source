package org.orcid.frontend.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

public class TurnstileVerifierTest {

    private HttpServer server;
    private String url;
    private final AtomicReference<String> requestBody = new AtomicReference<>();
    private final AtomicReference<String> contentType = new AtomicReference<>();
    private final AtomicReference<String> method = new AtomicReference<>();
    private final AtomicReference<String> body = new AtomicReference<>("{\"success\":true}");
    private final AtomicInteger status = new AtomicInteger(200);
    private final AtomicInteger requests = new AtomicInteger();

    @Before
    public void setUp() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/verify", exchange -> {
            requests.incrementAndGet();
            method.set(exchange.getRequestMethod());
            contentType.set(exchange.getRequestHeaders().getFirst("Content-Type"));
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] bytes = body.get().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status.get(), bytes.length);
            try (var output = exchange.getResponseBody()) {
                output.write(bytes);
            }
        });
        server.start();
        url = "http://127.0.0.1:" + server.getAddress().getPort() + "/verify";
    }

    @After
    public void tearDown() {
        server.stop(0);
    }

    @Test
    public void postsEncodedFormAndAcceptsSuccess() {
        assertTrue(new TurnstileVerifier(url, "secret+& é", 5000, 5000).verify("token=+ &"));
        assertEquals("POST", method.get());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", contentType.get());
        assertEquals("secret=secret%2B%26+%C3%A9&response=token%3D%2B+%26", requestBody.get());
    }

    @Test
    public void rejectsMissingCredentialsWithoutRequest() {
        assertFalse(new TurnstileVerifier(url, "", 5000, 5000).verify("token"));
        assertFalse(new TurnstileVerifier(url, null, 5000, 5000).verify("token"));
        assertFalse(new TurnstileVerifier(url, "secret", 5000, 5000).verify(null));
        assertFalse(new TurnstileVerifier(url, "secret", 5000, 5000).verify("  "));
        assertEquals(0, requests.get());
    }

    @Test
    public void rejectsInvalidResponses() {
        TurnstileVerifier verifier = new TurnstileVerifier(url, "secret", 5000, 5000);
        for (String invalid : new String[] {"{\"success\":false}", "{\"success\":\"true\"}", "{\"other\":true}",
                "not json", "", "{\"success\":true} {}", "{\"success\":false,\"success\":true}"}) {
            body.set(invalid);
            assertFalse(invalid, verifier.verify("token"));
        }
        body.set("{\"success\":true}");
        status.set(500);
        assertFalse(verifier.verify("token"));
        status.set(302);
        assertFalse(verifier.verify("token"));
    }

    @Test
    public void rejectsConnectionErrors() {
        server.stop(0);
        assertFalse(new TurnstileVerifier(url, "secret", 5000, 5000).verify("token"));
        assertFalse(new TurnstileVerifier("invalid-url", "secret", 5000, 5000).verify("token"));
    }

    @Test
    public void rejectsSlowResponsesUsingConfiguredReadTimeout() {
        server.createContext("/slow", exchange -> {
            try {
                Thread.sleep(1000);
                exchange.sendResponseHeaders(200, 0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                exchange.close();
            }
        });
        String slowUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/slow";
        assertFalse(new TurnstileVerifier(slowUrl, "secret", 5000, 50).verify("token"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInfiniteTimeout() {
        new TurnstileVerifier(url, "secret", 0, 5000);
    }
}