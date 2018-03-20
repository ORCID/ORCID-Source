package org.orcid.core.oauth.service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class NamespacedRandomCodeGenerator {

    private static final Cache<String, String> codeCache = CacheBuilder.newBuilder().maximumSize(100000).expireAfterWrite(10, TimeUnit.MINUTES).build();
    private RandomValueStringGenerator generator = new RandomValueStringGenerator(5);
    private static final String base62 = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    private int start;
    private int end;
    
    /** Each node needs a local cache of authentication codes. 
     * When creating a new code the cache is checked to ensure it's not already been issued. 
     * If it has, regenerate until unique code is used.
     * 
     * Each node requires its own code namespace in order to prevent clashes across nodes. 
     * This will be the first character of the authorisation code. 
     * Note this must be a range of prefixes, not a single char. 
     * Using a single char would reduce the amount of randomness drastically.
     * 
     * Split the base62 namespace into 3 chunks, last chuck may be bigger than the others.
     * Use a namespaced random char + 5 random chars to create code.
     */
    public NamespacedRandomCodeGenerator(int node, int numberOfNodes){
        int range = 62 / numberOfNodes;
        start = (node - 1) * range;
        end = start + range - 1;
        if (node == numberOfNodes)
            end = 61;        
    }
    
    /** 
     * @return a 6 char base62 code specific to this node.
     */
    public synchronized String nextRandomCode(){
        String suffix = generator.generate();
        String code;
        do {
            int position = ThreadLocalRandom.current().nextInt(end - start + 1);//excludes last number
            code = base62.charAt(position + start) + suffix;
        } while (codeCache.getIfPresent(code) != null);
        codeCache.put(code, code); 
        return code;
    };
}
