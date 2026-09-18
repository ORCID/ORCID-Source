package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Guards the value-copier configuration of the ehcache files.
 *
 * An identity copier hands every caller the same instance, so a cache may only use one when
 * its values are never mutated after they are read. Two consumers of caches that inherit
 * defaultTemplate do mutate what they get back:
 *
 *   - ManageProfileController calls removeIf(..) on the list from
 *     GivenPermissionToManagerReadOnly.findByGiver (cache "delegates-by-giver"),
 *   - RecordCorrectionsController reverses the list inside the cached RecordCorrectionsPage
 *     and swaps four of its fields (cache "invalid-record-data-change-page-asc").
 *
 * so the shared template has to keep copying.
 */
public class EhCacheCopierConfigTest {

    private static final String IDENTITY = "org.ehcache.impl.copy.IdentityCopier";
    private static final String SERIALIZING = "org.ehcache.impl.copy.SerializingCopier";

    private static final List<String> FILES = Arrays.asList(
            "ehcache_default.xml", "ehcache_orcid-api-web.xml", "ehcache_orcid-pub-web.xml",
            "ehcache_orcid-web.xml", "ehcache_orcid-scheduler-web.xml", "ehcache_orcid-internal-api.xml");

    /** Values are mutated in place by their consumers; these must get a defensive copy. */
    private static final List<String> MUST_COPY = Arrays.asList(
            "delegates-by-giver", "invalid-record-data-change-page-asc");

    private Document parse(String file) throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)) {
            assertNotNull("missing " + file, in);
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            return f.newDocumentBuilder().parse(in);
        }
    }

    private String templateCopier(Document d) {
        NodeList templates = d.getElementsByTagNameNS("*", "cache-template");
        for (int i = 0; i < templates.getLength(); i++) {
            Element t = (Element) templates.item(i);
            if ("defaultTemplate".equals(t.getAttribute("name"))) {
                NodeList vt = t.getElementsByTagNameNS("*", "value-type");
                return vt.getLength() == 0 ? null : ((Element) vt.item(0)).getAttribute("copier");
            }
        }
        return null;
    }

    /** alias -> its own copier, or null when it inherits the template's. */
    private Map<String, String> cacheCopiers(Document d) {
        Map<String, String> out = new LinkedHashMap<>();
        NodeList caches = d.getElementsByTagNameNS("*", "cache");
        for (int i = 0; i < caches.getLength(); i++) {
            Element c = (Element) caches.item(i);
            NodeList vt = c.getElementsByTagNameNS("*", "value-type");
            out.put(c.getAttribute("alias"), vt.getLength() == 0 ? null : ((Element) vt.item(0)).getAttribute("copier"));
        }
        return out;
    }

    @Test
    public void defaultTemplateCopiesValues() throws Exception {
        for (String file : FILES) {
            assertEquals(file + ": defaultTemplate must copy, because caches that inherit it are mutated by their consumers",
                    SERIALIZING, templateCopier(parse(file)));
        }
    }

    @Test
    public void cachesWhoseValuesAreMutatedDoNotAliasTheCachedInstance() throws Exception {
        for (String file : FILES) {
            Map<String, String> copiers = cacheCopiers(parse(file));
            for (String alias : MUST_COPY) {
                assertTrue(file + ": expected cache " + alias, copiers.containsKey(alias));
                String copier = copiers.get(alias);
                // either inherits the (serializing) template, or names a non-identity copier itself
                assertTrue(file + ": " + alias + " must not use an identity copier, its value is mutated in place",
                        copier == null || !IDENTITY.equals(copier));
            }
        }
    }

    @Test
    public void everyCacheResolvesToAKnownCopier() throws Exception {
        for (String file : FILES) {
            Document d = parse(file);
            String template = templateCopier(d);
            List<String> unknown = new ArrayList<>();
            for (Map.Entry<String, String> e : cacheCopiers(d).entrySet()) {
                String effective = e.getValue() == null ? template : e.getValue();
                if (!IDENTITY.equals(effective) && !SERIALIZING.equals(effective)) {
                    unknown.add(e.getKey() + "=" + effective);
                }
            }
            assertEquals(file + ": caches with an unrecognised copier " + unknown, 0, unknown.size());
        }
    }
}
