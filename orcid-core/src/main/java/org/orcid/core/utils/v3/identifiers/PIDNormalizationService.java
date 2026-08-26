package org.orcid.core.utils.v3.identifiers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.normalizers.Normalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.NormalizerWithURLTransform;
import org.orcid.pojo.IdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

@Component("PIDNormalizationService")
public class PIDNormalizationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PIDNormalizationService.class);

    @Autowired
    private List<Normalizer> normalizers = new ArrayList<>();

    @Autowired
    @Lazy
    private IdentifierTypeManager idman;

    private Map<String, LinkedList<Normalizer>> map = new HashMap<>();
    private Map<String, IdentifierType> idTypeMap;

    private final Pattern pattern = Pattern.compile("^(http[s]?://www\\.|http[s]?://|www\\.)([^/]*)");
    private final UrlValidator urlValidator = new UrlValidator();

    @PostConstruct
    public void init() {
        Collections.sort(normalizers, AnnotationAwareOrderComparator.INSTANCE);
        for (Normalizer n : normalizers) {
            List<String> supported = n.canHandle();
            if (!supported.isEmpty()) {
                for (String type : supported) {
                    map.computeIfAbsent(type, k -> new LinkedList<>()).add(n);
                }
            }
        }
    }

    private synchronized Map<String, IdentifierType> getIdTypeMap() {
        if (idTypeMap == null && idman != null) {
            idTypeMap = idman.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH);
            if (idTypeMap != null) {
                for (String type : idTypeMap.keySet()) {
                    map.putIfAbsent(type, new LinkedList<>());
                }
                for (Normalizer n : normalizers) {
                    if (n.canHandle().isEmpty()) {
                        for (String type : idTypeMap.keySet()) {
                            map.computeIfAbsent(type, k -> new LinkedList<>()).add(n);
                        }
                    }
                }
                // Sort each list so catch-all normalizers execute in proper spring order
                for (List<Normalizer> list : map.values()) {
                    Collections.sort(list, AnnotationAwareOrderComparator.INSTANCE);
                }
            }
        }
        return idTypeMap;
    }

    public String normalise(String apiTypeName, String value) {
        getIdTypeMap();
        if (apiTypeName == null || value == null || !map.containsKey(apiTypeName))
            return value;
        String returnValue = value;
        List<Normalizer> normalizerList = map.get(apiTypeName);
        if (normalizerList != null) {
            for (Normalizer n : normalizerList) {
                returnValue = n.normalise(apiTypeName, returnValue);
            }
        }
        return returnValue;
    }

    public String generateNormalisedURL(String apiTypeName, String value){
        if (apiTypeName == null)
            return value;
        getIdTypeMap();
        String norm = value;
        List<Normalizer> normalizerList = map.get(apiTypeName);
        if (normalizerList != null) {
            for (Normalizer n : normalizerList) {
                if (n instanceof NormalizerWithURLTransform)
                    norm = ((NormalizerWithURLTransform)n).normaliseURL(apiTypeName, norm);
                else
                    norm = n.normalise(apiTypeName, norm);
            }
        }

        if (!norm.isEmpty() && idTypeMap != null && idTypeMap.containsKey(apiTypeName)){
            IdentifierType type = this.idTypeMap.get(apiTypeName);
            if (type != null) {
                String prefix = type.getResolutionPrefix();
                if (!StringUtils.isEmpty(prefix)) {
                    try {
                        String result = null;
                        if (norm.startsWith("http")) {
                            String compare = norm;
                            if (compare.toLowerCase().startsWith(prefix.toLowerCase())) {
                                result = norm;
                            } else {
                                Matcher matcher = pattern.matcher(compare);
                                if (matcher.find()) {
                                    if (prefix.equals(matcher.group(1) + matcher.group(2)) || prefix.contains(matcher.group(2))) {
                                        result = norm;
                                    } else if(urlValidator.isValid(norm)) {
                                        result = norm;
                                    } else {
                                        if (norm.contains("=")) {
                                            norm = norm.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)([^=]*)","");
                                            norm = norm.substring(1);
                                        } else {
                                            norm = norm.substring(norm.lastIndexOf("/") + 1);
                                        }
                                        result = prefix + norm;
                                    }
                                } else {
                                    if (urlValidator.isValid(norm)){
                                        result = norm;
                                    } else {
                                        result = prefix + norm;
                                    }
                                }
                            }
                        } else {
                            result = prefix + norm;
                        }
                        return URLDecoder.decode(result, "UTF-8");
                    } catch (UnsupportedEncodingException uee) {
                        throw new RuntimeException(uee);
                    } catch (IllegalArgumentException iae) {
                        throw iae;
                    }
                }
            }
        }

        return "";
    }
}