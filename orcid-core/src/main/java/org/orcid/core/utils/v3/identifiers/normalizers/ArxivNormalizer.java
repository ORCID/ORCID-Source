package org.orcid.core.utils.v3.identifiers.normalizers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class ArxivNormalizer implements NormalizerWithURLTransform {

    private static final List<String> canHandle = Lists.newArrayList("arxiv");
    private static final Pattern pattern = Pattern.compile("(?:(?i)arXiv:)?(\\d{4}\\.\\d{4}v?\\d?|[\\w-\\.]+\\/\\d{7}v?\\d?)");
    
    @Override
    public List<String> canHandle() {
        return canHandle;
    }

    /** Uses rules at https://arxiv.org/help/arxiv_identifier_for_services
     * 
     */
    @Override
    public String normalise(String apiTypeName, String value) {
        if (!canHandle.contains(apiTypeName))
            return value;
        Matcher m = pattern.matcher(value);
        if (m.find()){
            String n = m.group(1);
            if (n != null){
                return "arXiv:"+n;
            }
        }
        return "";
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public String normaliseURL(String apiTypeName, String value) {
        if (!canHandle.contains(apiTypeName))
            return value;
        Matcher m = pattern.matcher(value);
        if (m.find()){
            String n = m.group(1);
            if (n != null){
                return n;
            }
        }
        return "";
    }

}
