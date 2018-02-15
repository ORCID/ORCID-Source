package org.orcid.core.cache;

/**
 * 
 * Interface to use with GenericCache, for looking things up from the DB.
 * 
 * @author Will Simpson
 *
 * @param <K>
 *            The key to use when retrieving.
 * @param <V>
 *            The value that is retrieved.
 */
public interface Retriever<K, V> {

    V retrieve(K key);

}
