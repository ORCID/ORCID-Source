package org.orcid.utils.listener;

import java.util.Date;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
* TODO: Once the jersey migration is over, this could be moved back to the orcid-utils package so it could be reused form the orcid-persistence package
* */
@Deprecated
public class LastModifiedMessage extends BaseMessage {

    /**
     * Create a map from the component parts
     * 
     * @param orcid
     * @param date
     */
    public LastModifiedMessage(String orcid, Date date) {
        super(ImmutableMap.of(MessageConstants.TYPE.value, MessageConstants.TYPE_LAST_UPDATED.value, 
                MessageConstants.ORCID.value, orcid, MessageConstants.DATE.value,
                (date == null) ? "" : String.valueOf(date.getTime())));
    }

    /**
     * Convert a map that was sent back into an object
     * 
     * @param m
     */
    public LastModifiedMessage(Map<String, String> m) {
        super(ImmutableMap.copyOf(m));
    }

    public Date getLastUpdated() {
        String time = map.get(MessageConstants.DATE.value);
        if (time == null)
            return null;
        return new Date(Long.valueOf(time));
    }
}
