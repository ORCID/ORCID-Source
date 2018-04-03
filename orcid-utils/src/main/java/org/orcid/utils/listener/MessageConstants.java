package org.orcid.utils.listener;

/** These are the keys for message queue messages.
 * Each message should have, as a minimum, a TYPE->TYPE_* pair. 
 *
 * 
 * @author tom
 *
 */
public enum MessageConstants {
    
    ORCID("o"),DATE("d"),METHOD("m"),
    TYPE("t"),
    TYPE_LAST_UPDATED("lu"),
    TYPE_RETRY("r");
    
    public static class Queues{
        public static final String UPDATED_ORCIDS = "updated_orcids";        
        public static final String TEST = "test";
        public static final String TEST_REPLY = "test_reply";
        public static final String REINDEX = "reindex";
        public static final String RETRY = "retry";
    }
    
    public final String value;
    
    MessageConstants(String s){
        value = s;
    }
}
