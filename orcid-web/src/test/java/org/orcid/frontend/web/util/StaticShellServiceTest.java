package org.orcid.frontend.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.sun.net.httpserver.HttpServer;

public class StaticShellServiceTest {

    private HttpServer server;
    private StaticShellService service;
    private final AtomicReference<String> shellBody = new AtomicReference<>("shell v1");
    private final AtomicReference<Integer> shellStatus = new AtomicReference<>(200);

    @Before
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/orcid-web-frontend", exchange -> {
            byte[] body = shellBody.get().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(shellStatus.get(), body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.start();
        service = newService(server.getAddress().getPort(), 5);
    }

    @After
    public void tearDown() {
        server.stop(0);
    }

    private StaticShellService newService(int port, int ttlMinutes) {
        StaticShellService newService = new StaticShellService();
        ReflectionTestUtils.setField(newService, "frontendShellBaseUri", "http://127.0.0.1:" + port + "/orcid-web-frontend");
        ReflectionTestUtils.setField(newService, "frontendShellCacheTtlMinutes", ttlMinutes);
        return newService;
    }

    @Test
    public void toFrontendLocaleDirTest() {
        assertEquals("en", StaticShellService.toFrontendLocaleDir(null));
        assertEquals("en", StaticShellService.toFrontendLocaleDir(Locale.ENGLISH));
        assertEquals("fr", StaticShellService.toFrontendLocaleDir(Locale.FRENCH));
        assertEquals("zh_CN", StaticShellService.toFrontendLocaleDir(Locale.SIMPLIFIED_CHINESE));
        assertEquals("zh_TW", StaticShellService.toFrontendLocaleDir(Locale.TRADITIONAL_CHINESE));
        // Country-specific locale falls back to the bare language dir
        assertEquals("es", StaticShellService.toFrontendLocaleDir(new Locale("es", "CR")));
        // Pseudo-locales shipped by the frontend war
        assertEquals("lr", StaticShellService.toFrontendLocaleDir(new Locale("lr")));
        assertEquals("xx", StaticShellService.toFrontendLocaleDir(new Locale("xx")));
        // Unknown locale falls back to en
        assertEquals("en", StaticShellService.toFrontendLocaleDir(new Locale("tlh")));
    }

    @Test
    public void cachesShellWithinTtlTest() throws IOException {
        assertEquals("shell v1", service.getShellHtml(Locale.ENGLISH));
        shellBody.set("shell v2");
        // Within the TTL the cached copy is served
        assertEquals("shell v1", service.getShellHtml(Locale.ENGLISH));
    }

    @Test
    public void refreshesShellWhenTtlExpiredTest() throws IOException {
        StaticShellService zeroTtlService = newService(server.getAddress().getPort(), 0);
        assertEquals("shell v1", zeroTtlService.getShellHtml(Locale.ENGLISH));
        shellBody.set("shell v2");
        assertEquals("shell v2", zeroTtlService.getShellHtml(Locale.ENGLISH));
    }

    @Test
    public void servesStaleShellWhenRefreshFailsTest() throws IOException {
        StaticShellService zeroTtlService = newService(server.getAddress().getPort(), 0);
        assertEquals("shell v1", zeroTtlService.getShellHtml(Locale.ENGLISH));
        shellStatus.set(500);
        assertEquals("shell v1", zeroTtlService.getShellHtml(Locale.ENGLISH));
    }

    @Test
    public void throwsWhenNoShellAvailableTest() {
        shellStatus.set(404);
        assertThrows(IOException.class, () -> service.getShellHtml(Locale.ENGLISH));
    }
}
