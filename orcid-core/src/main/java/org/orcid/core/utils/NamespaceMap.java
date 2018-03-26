package org.orcid.core.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.NamespaceContext;

/**
 * 
 * @author Will Simpson
 *
 */
public class NamespaceMap implements NamespaceContext {

    private Map<String, String> map = new HashMap<>();

    @Override
    public String getNamespaceURI(String prefix) {
        return map.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        Optional<String> firstMatch = getByNamespace(namespaceURI).findFirst();
        return firstMatch.orElse(null);
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        return getByNamespace(namespaceURI).collect(Collectors.toList()).iterator();
    }

    public void putNamespace(String prefix, String namespaceURI) {
        map.put(prefix, namespaceURI);
    }

    private Stream<String> getByNamespace(String namespaceURI) {
        return map.entrySet().stream().filter(e -> e.getValue().equals(namespaceURI)).map(e -> e.getKey());
    }

}
