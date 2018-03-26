package org.orcid.api.member;

import org.orcid.core.exception.PutCodeFormatException;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class MemberApiServiceImplHelper {

    protected Long getPutCode(String putCode) {
        Long putCodeNum = null;
        try {
            putCodeNum = Long.valueOf(putCode);
        } catch (Exception e) {
            throw new PutCodeFormatException();
        }
        return putCodeNum;
    }
}
