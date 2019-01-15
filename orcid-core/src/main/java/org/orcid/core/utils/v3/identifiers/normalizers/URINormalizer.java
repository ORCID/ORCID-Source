package org.orcid.core.utils.v3.identifiers.normalizers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class URINormalizer implements Normalizer {

    private static final List<String> canHandle = Lists.newArrayList("uri");
    private static final Pattern pattern = Pattern.compile("^(?:(ht|f)tp(s?)\\:\\/\\/)?");
    private static final String HTTP_PROTOCOL = "http://";
    
    @Override
    public List<String> canHandle() {
        return canHandle;
    }

    @Override
    public String normalise(String apiTypeName, String value) {
        if (!canHandle.contains(apiTypeName)) {
            return value;
        }
        
        Matcher m = pattern.matcher(value);
        if (m.find()){
            if (m.group(1) != null) {
                return value;
            }
        }
        return HTTP_PROTOCOL + value;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
