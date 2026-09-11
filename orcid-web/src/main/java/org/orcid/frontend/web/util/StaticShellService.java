package org.orcid.frontend.web.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Fetches and caches the Angular shell (index.html) from the orcid-web-frontend
 * webapp deployed on the same Tomcat, so orcid-web can serve the public record
 * page itself and control its HTTP caching headers (Last-Modified / 304).
 */
@Component
public class StaticShellService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticShellService.class);

    // Must stay in sync with the `map $cookie_locale_v3 $langCode` table in the
    // nginx ui configs and the locale directories shipped in the orcid-web-frontend war
    private static final Set<String> FRONTEND_LOCALE_DIRS = Set.of("en", "ar", "ca", "cs", "es", "de", "fr", "it", "ja", "ko", "lr", "pl", "pt", "rl", "ru", "tr",
            "xx", "zh_CN", "zh_TW");

    private static final String DEFAULT_LOCALE_DIR = "en";

    @Value("${org.orcid.frontend.web.frontendShellBaseUri:http://localhost:8080/orcid-web-frontend}")
    private String frontendShellBaseUri;

    @Value("${org.orcid.frontend.web.frontendShellCacheTtlMinutes:5}")
    private int frontendShellCacheTtlMinutes;

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).followRedirects(HttpClient.Redirect.NEVER).build();

    private final ConcurrentHashMap<String, CachedShell> cache = new ConcurrentHashMap<>();

    public String getShellHtml(Locale locale) throws IOException {
        String localeDir = toFrontendLocaleDir(locale);
        CachedShell cached = cache.get(localeDir);
        if (cached != null && !cached.isExpired(frontendShellCacheTtlMinutes)) {
            return cached.html;
        }
        try {
            String html = fetchShell(localeDir);
            cache.put(localeDir, new CachedShell(html));
            return html;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            if (cached != null) {
                LOGGER.warn("Could not refresh Angular shell for locale {}, serving stale copy", localeDir, e);
                return cached.html;
            }
            throw new IOException("Could not fetch Angular shell for locale " + localeDir, e);
        }
    }

    static String toFrontendLocaleDir(Locale locale) {
        if (locale == null) {
            return DEFAULT_LOCALE_DIR;
        }
        String candidate = locale.toString();
        if (FRONTEND_LOCALE_DIRS.contains(candidate)) {
            return candidate;
        }
        // Fall back to the bare language when the war has no country-specific dir (e.g. es_CR -> es)
        String language = locale.getLanguage();
        if (FRONTEND_LOCALE_DIRS.contains(language)) {
            return language;
        }
        return DEFAULT_LOCALE_DIR;
    }

    private String fetchShell(String localeDir) throws IOException, InterruptedException {
        URI uri = URI.create(frontendShellBaseUri + "/" + localeDir + "/index.html");
        HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(10)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Unexpected status " + response.statusCode() + " fetching " + uri);
        }
        return response.body();
    }

    private static class CachedShell {
        private final String html;
        private final long fetchedAt;

        private CachedShell(String html) {
            this.html = html;
            this.fetchedAt = System.currentTimeMillis();
        }

        private boolean isExpired(int ttlMinutes) {
            return System.currentTimeMillis() - fetchedAt >= Duration.ofMinutes(ttlMinutes).toMillis();
        }
    }
}
