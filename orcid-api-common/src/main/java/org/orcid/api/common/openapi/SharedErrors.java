package org.orcid.api.common.openapi;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

/**
 * The error responses operations share, added by {@link OrcidOpenApiFilter} so
 * they are written down once. Each was observed against a running Registry by
 * the contract tests (ORCID/orcid-api-contract-tests, tests/test_errors.py); the
 * error codes are OrcidCoreExceptionMapper's. A response an operation declares
 * itself is kept.
 */
final class SharedErrors {

    /** Error media types for an operation whose own responses name none, such as a delete. */
    static final List<String> ORCID_MEDIA_TYPES = List.of("application/vnd.orcid+xml", "application/orcid+xml", "application/xml",
            "application/vnd.orcid+json", "application/orcid+json", "application/json");

    static final String ORCID_ERROR = "#/components/schemas/OrcidError";

    /** The body the token check writes; declared in the base files. */
    static final String OAUTH_ERROR = "#/components/schemas/OAuthError";

    private SharedErrors() {
    }

    static void addTo(Operation operation, String path, String httpMethod) {
        if (operation.getResponses() == null) {
            operation.setResponses(new ApiResponses());
        }
        ApiResponses responses = operation.getResponses();
        boolean member = operation.getSecurity() != null && !operation.getSecurity().isEmpty();
        boolean record = path.startsWith("/v3.0/{orcid}");
        boolean get = "GET".equalsIgnoreCase(httpMethod);
        boolean post = "POST".equalsIgnoreCase(httpMethod);
        boolean body = operation.getRequestBody() != null;
        boolean putCode = hasPathParameter(operation, "putCode");
        List<String> types = errorMediaTypes(responses);

        List<String> invalid = new ArrayList<>();
        if (body) {
            invalid.add("the body does not match the schema (error 9047) or holds an invalid value");
        }
        if (body && putCode) {
            invalid.add("the put-code in the body is not the one in the path (9019)");
        }
        if (member && putCode) {
            invalid.add("the put-code is not a number (9034)");
        }
        if (!invalid.isEmpty()) {
            String text = "Bad request: " + String.join("; ", invalid) + ".";
            ApiResponse badRequest = orcidError(types, body ? text + " Malformed JSON is answered with the parser's message, as plain text." : text);
            if (body) {
                badRequest.getContent().addMediaType("text/plain", new MediaType().schema(new StringSchema()));
            }
            addIfAbsent(responses, "400", badRequest);
        }

        if (member) {
            addIfAbsent(responses, "401", withOAuthError(orcidError(types,
                    "The token is invalid, expired or revoked (an OAuth error, always JSON), or it belongs to a public client or to another record (error 9017).")));
            addIfAbsent(responses, "403", withOAuthError(orcidError(types,
                    "No token was sent (an OAuth error, always JSON), or the token lacks the scope this needs (error 9038).")));
        } else {
            addIfAbsent(responses, "401", oauthError("The token is invalid, expired or revoked. The token is optional, but a bad one is refused."));
            if (putCode && get) {
                addIfAbsent(responses, "403", orcidError(types, "The item is not public (error 9039)."));
            }
        }

        if (record || path.startsWith("/v3.0/client/")) {
            addIfAbsent(responses, "404", orcidError(types, "No record has this iD, or no item has this put-code (error 9016)."
                    + (!member && putCode ? " Also when the put-code is not a number." : "")));
        }
        if (record) {
            String conflict = "The record is locked (error 9018) or deactivated (9044)";
            conflict += get ? "." : post && body ? ", or deprecated, or the item duplicates one already on the record (9021)." : ", or deprecated.";
            addIfAbsent(responses, "409", orcidError(types, conflict));
            if (get) {
                ApiResponse moved = orcidError(types, "The record is deprecated. Location is this URL with the primary record's iD.");
                moved.addHeaderObject("Location", new Header().description("The same resource on the primary record")
                        .schema(new StringSchema().format("uri")));
                addIfAbsent(responses, "301", moved);
            }
        }
    }

    private static boolean hasPathParameter(Operation operation, String name) {
        if (operation.getParameters() != null) {
            for (Parameter parameter : operation.getParameters()) {
                if ("path".equals(parameter.getIn()) && name.equals(parameter.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<String> errorMediaTypes(ApiResponses responses) {
        List<String> types = new ArrayList<>();
        for (ApiResponse response : responses.values()) {
            if (response.getContent() != null) {
                for (String type : response.getContent().keySet()) {
                    if (ORCID_MEDIA_TYPES.contains(type) && !types.contains(type)) {
                        types.add(type);
                    }
                }
            }
        }
        return types.isEmpty() ? ORCID_MEDIA_TYPES : types;
    }

    private static ApiResponse orcidError(List<String> types, String description) {
        Content content = new Content();
        for (String type : types) {
            content.addMediaType(type, new MediaType().schema(new Schema<>().$ref(ORCID_ERROR)));
        }
        return new ApiResponse().description(description).content(content);
    }

    private static ApiResponse oauthError(String description) {
        return new ApiResponse().description(description)
                .content(new Content().addMediaType("application/json", new MediaType().schema(new Schema<>().$ref(OAUTH_ERROR))));
    }

    /** The token check answers in JSON whatever was asked for, beside the ORCID error the API writes. */
    private static ApiResponse withOAuthError(ApiResponse response) {
        Schema<?> either = new ComposedSchema().anyOf(List.of(new Schema<>().$ref(ORCID_ERROR), new Schema<>().$ref(OAUTH_ERROR)));
        response.getContent().addMediaType("application/json", new MediaType().schema(either));
        return response;
    }

    private static void addIfAbsent(ApiResponses responses, String code, ApiResponse response) {
        if (!responses.containsKey(code)) {
            responses.addApiResponse(code, response);
        }
    }
}
