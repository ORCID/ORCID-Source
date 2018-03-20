package org.orcid.core.utils.v3.identifiers.normalizers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.html.HtmlEscapers;

@Component
public class DOINormalizer implements Normalizer {

    private static final List<String> canHandle = Lists.newArrayList("doi");
    private static final Pattern pattern = Pattern.compile("(10\\.[0-9a-zA-Z]+\\/(?:(?![\"&\\'])\\S)+)\\b");
    
    @Override
    public List<String> canHandle() {
        return canHandle;
    }

    @Override
    public String normalise(String apiTypeName, String value) {
        if (!canHandle.contains(apiTypeName))
            return value;
        
        //could be html escaped, and more than once!
        if (value.contains("&") && value.contains(";")){            
            int length = 0;
            do {
                length = value.length();
                value = StringEscapeUtils.unescapeXml(value);               
            }while (value.length() < length);                
        }
        
        Matcher m = pattern.matcher(value);
        if (m.find()){
            String n = m.group(1);
            if (n != null){
                return n;
            }
        }
        return "";
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
