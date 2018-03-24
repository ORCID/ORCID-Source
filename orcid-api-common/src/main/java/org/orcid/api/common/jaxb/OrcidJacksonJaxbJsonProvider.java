package org.orcid.api.common.jaxb;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.orcid.api.common.exception.JSONInputValidator;
import org.orcid.core.exception.InvalidJSONException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * @author Will Simpson
 */
@Provider
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class OrcidJacksonJaxbJsonProvider extends JacksonJaxbJsonProvider {
    
    private JSONInputValidator jsonInputValidator = new JSONInputValidator();

    public OrcidJacksonJaxbJsonProvider() {
        super();
        configureAll();
    }

    public OrcidJacksonJaxbJsonProvider(Annotations... annotationsToUse) {
        super(annotationsToUse);
        configureAll();
    }

    public OrcidJacksonJaxbJsonProvider(ObjectMapper mapper, Annotations[] annotationsToUse) {
        super(mapper, annotationsToUse);
        configureAll();
    }
    
    private void configureAll() {
        configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    /** This adds a validation step when converting JSON into ORCID models.
     * 
     */
    @Override
    public Object readFrom(Class<Object> arg0, Type arg1, Annotation[] arg2, MediaType arg3, MultivaluedMap<String, String> arg4, InputStream arg5) throws IOException {
        Object o = null;
        try{
            o = super.readFrom(arg0, arg1, arg2, arg3, arg4, arg5);
        }catch(JsonMappingException e){
            Map<String, String> params = new HashMap<>();
            params.put("error", e.getMessage());
            throw new InvalidJSONException(params);
        }
        if (jsonInputValidator.canValidate(o.getClass())){
            jsonInputValidator.validateJSONInput(o);
        }
        return o;
    }
    
}
