package org.orcid.utils.listener;

public enum MessageConstants {

    ORCID("o"),DATE("d"),METHOD("m"),
    TYPE("t"),
    TYPE_LAST_UPDATED("lu");
    
    public final String value;
    
    MessageConstants(String s){
        value = s;
    }
}
