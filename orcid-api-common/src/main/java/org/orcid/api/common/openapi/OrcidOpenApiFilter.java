package org.orcid.api.common.openapi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.swagger.v3.core.filter.AbstractSpecFilter;
import io.swagger.v3.core.model.ApiDescription;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
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

    /** The API version these descriptions are for. */
    static final String API_VERSION = "3.0";

    /**
     * The identifiers resource serves every version from one class
     * ({@code /v{version: 2.0|2.1|3.0}/identifiers}), which the generator turns
     * into a path with an undeclared parameter. A v3.0 description pins it.
     */
    @Override
    public Optional<OpenAPI> filterOpenAPI(OpenAPI openAPI, Map<String, List<String>> params, Map<String, String> cookies,
            Map<String, List<String>> headers) {
        if (openAPI.getPaths() != null) {
            Paths pinned = new Paths();
            openAPI.getPaths().forEach((path, item) -> {
                if (path.contains("{version}")) {
                    withoutVersionParameter(item);
                    pinned.addPathItem(path.replace("{version}", API_VERSION), item);
                } else {
                    pinned.addPathItem(path, item);
                }
            });
            openAPI.setPaths(pinned);
        }
        return Optional.of(openAPI);
    }

    private static void withoutVersionParameter(PathItem item) {
        if (item.getParameters() != null) {
            item.getParameters().removeIf(OrcidOpenApiFilter::isVersionParameter);
        }
        for (Operation operation : item.readOperations()) {
            if (operation.getParameters() != null) {
                operation.getParameters().removeIf(OrcidOpenApiFilter::isVersionParameter);
            }
        }
    }

    private static boolean isVersionParameter(Parameter parameter) {
        return "path".equals(parameter.getIn()) && "version".equals(parameter.getName());
    }

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

    /**
     * The API's JSON provider writes absent values out as null
     * ({@code "source": null}), so any value may be null. OpenAPI 3.0 cannot say
     * that beside a {@code $ref}, so the referenced schemas themselves are marked
     * nullable, and every other property directly.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Optional<Schema> filterSchema(Schema schema, Map<String, List<String>> params, Map<String, String> cookies,
            Map<String, List<String>> headers) {
        schema.setNullable(true);
        return Optional.of(schema);
    }

    /**
     * Besides nullability: the JSON provider writes dates as milliseconds since
     * the epoch ({@code "value": 1791835454322}), not as the date-time strings
     * the XML carries, and JSON is what these descriptions follow.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Optional<Schema> filterSchemaProperty(Schema property, Schema schema, String propName, Map<String, List<String>> params,
            Map<String, String> cookies, Map<String, List<String>> headers) {
        if (property.get$ref() != null) {
            return Optional.of(property);
        }
        if ("string".equals(property.getType()) && "date-time".equals(property.getFormat())) {
            property.setType("integer");
            property.setFormat("int64");
            property.setDescription("Milliseconds since the epoch. XML carries the same instant as an xs:dateTime.");
        }
        property.setNullable(true);
        return Optional.of(property);
    }

    /**
     * Path parameters that nearly every operation shares, described once here
     * rather than on each of the resource methods. The iD pattern is the one
     * the Registry validates iDs with ({@code OrcidStringUtils.ORCID_STRING}).
     */
    @Override
    public Optional<Parameter> filterParameter(Parameter parameter, Operation operation, ApiDescription api, Map<String, List<String>> params,
            Map<String, String> cookies, Map<String, List<String>> headers) {
        if ("path".equals(parameter.getIn()) && "orcid".equals(parameter.getName())) {
            parameter.setDescription("ORCID iD of the record, for example `0000-0002-1825-0097`.");
            parameter.getSchema().setPattern("^(\\d{4}-){3}\\d{3}[\\dX]$");
        } else if ("path".equals(parameter.getIn()) && "putCode".equals(parameter.getName())) {
            parameter.setDescription("Put-code of the item: the identifier the API gave it when it was added.");
        }
        return Optional.of(parameter);
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
