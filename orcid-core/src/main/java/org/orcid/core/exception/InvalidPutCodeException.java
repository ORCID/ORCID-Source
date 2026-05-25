package org.orcid.core.exception;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.common.Source;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class InvalidPutCodeException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public InvalidPutCodeException(Map<String, String> params) {
        super(params);
    }

    public static InvalidPutCodeException forSource(String sourceName) {
        Map<String, String> params = new HashMap<String, String>();
        if (StringUtils.isNoneBlank(sourceName)) {
            params.put("clientName", sourceName);
        }
        return new InvalidPutCodeException(params);
    }

}
