package org.orcid.core.utils.v3.identifiers.normalizers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class CurieNormalizer implements NormalizerWithURLTransform {

    private static final List<String> canHandle = Lists.newArrayList("rrid");
    private static final Pattern pattern = Pattern.compile("(?:(?i)RRID:)?(AB_\\d{6}|CVCL_[0-9A-Z]{4}|SCR_\\d{6}|IMSR_JAX\\:\\d{6}|Addgene_\\d{5}|SAMN\\d{8}|MMRRC_\\d{6}-UCD)");

    @Override
    public List<String> canHandle() {
        return canHandle;
    }

    @Override
    public String normalise(String apiTypeName, String value) {
        return curieIdentifier(apiTypeName, value);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public String normaliseURL(String apiTypeName, String value) {
        return curieIdentifier(apiTypeName, value);
    }
    
    private String curieIdentifier(String apiTypeName, String value) {
        if (!canHandle.contains(apiTypeName))
            return value;
        Matcher m = pattern.matcher(value);
        if (m.find()){
            String n = m.group(1);
            if (n != null){
                return "RRID:"+n;
            }
        }
        return "";
    }
}
