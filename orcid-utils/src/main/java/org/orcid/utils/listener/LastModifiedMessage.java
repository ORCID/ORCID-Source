/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.utils.listener;

import java.util.Date;
import java.util.Map;

import org.orcid.utils.listener.MessageConstants;

import com.google.common.collect.ImmutableMap;

/** Represents an immutable last modified event
 * 
 * @author tom
 *
 */
public class LastModifiedMessage {
    
    /** immutable map ready for transport
     * 
     */
    public final Map<String,String> map;

    public LastModifiedMessage(String orcid, String method, Date date){
        String lastStr = "";
        if (date != null)
            lastStr = ""+date.getTime();

        map = ImmutableMap.of(
                MessageConstants.TYPE.value,MessageConstants.TYPE_LAST_UPDATED.value,
                MessageConstants.ORCID.value, orcid, 
                MessageConstants.DATE.value, ""+lastStr, 
                MessageConstants.METHOD.value, method);
    }
    
    public LastModifiedMessage(Map<String,String> m){
        if (!m.containsKey(MessageConstants.TYPE.value) || !MessageConstants.TYPE_LAST_UPDATED.value.equals(m.get(MessageConstants.TYPE.value)))
                throw new IllegalArgumentException("Wrong map type");
        map = ImmutableMap.copyOf(m);
    }
    
    public String getOrcid(){
        return map.get(MessageConstants.ORCID.value);
    }
    
    @SuppressWarnings("deprecation")
    public Date getLastUpdated(){
        String time = map.get(MessageConstants.DATE.value);
        if (time == null)
            return null;
        return new Date(Long.valueOf(time));
    }
    
    public String getMethod(){
        return map.get(MessageConstants.METHOD.value);
    }
    
    public Map<String,String> getMap(){
        return map;
    }
    
}
