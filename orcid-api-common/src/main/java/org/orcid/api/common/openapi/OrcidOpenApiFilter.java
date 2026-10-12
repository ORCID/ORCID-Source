package org.orcid.api.common.openapi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.swagger.v3.core.filter.AbstractSpecFilter;
import io.swagger.v3.core.model.ApiDescription;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;

/**
 * Used only by the OpenAPI generator (swagger-maven-plugin-jakarta), never at
 * runtime. Removes what the JAX-RS resources declare but the API description
 * should not show.
 */
public class OrcidOpenApiFilter extends AbstractSpecFilter {

    /**
     * Resource methods that serve the Swagger UI page, status checks or a test
     * error rather than the API itself.
     */
    static final Set<String> NON_API_OPERATIONS = Set.of("viewSwagger", "viewSwaggerO2c", "viewStatusSimple", "viewStatusJson", "viewError");

    @Override
    public Optional<Operation> filterOperation(Operation operation, ApiDescription api, Map<String, List<String>> params, Map<String, String> cookies,
            Map<String, List<String>> headers) {
        if (NON_API_OPERATIONS.contains(operation.getOperationId())) {
            return Optional.empty();
        }
        if (operation.getRequestBody() != null) {
            operation.getRequestBody().setContent(withoutMediaTypeParameters(operation.getRequestBody().getContent()));
        }
        if (operation.getResponses() != null) {
            for (ApiResponse response : operation.getResponses().values()) {
                response.setContent(withoutMediaTypeParameters(response.getContent()));
            }
        }
        return Optional.of(operation);
    }

    @Override
    public boolean isRemovingUnreferencedDefinitions() {
        return true;
    }

    /**
     * The media type constants carry a quality factor for content negotiation
     * ({@code application/vnd.orcid+xml; qs=5}). A client never sends it, so it
     * does not belong in the description. Where two keys become one, the first
     * wins; they describe the same body.
     */
    static Content withoutMediaTypeParameters(Content content) {
        if (content == null) {
            return null;
        }
        Map<String, MediaType> stripped = new LinkedHashMap<>();
        for (Map.Entry<String, MediaType> entry : content.entrySet()) {
            String key = entry.getKey();
            int semicolon = key.indexOf(';');
            String bare = (semicolon < 0 ? key : key.substring(0, semicolon)).trim();
            stripped.putIfAbsent(bare, entry.getValue());
        }
        Content result = new Content();
        result.putAll(stripped);
        return result;
    }
}
