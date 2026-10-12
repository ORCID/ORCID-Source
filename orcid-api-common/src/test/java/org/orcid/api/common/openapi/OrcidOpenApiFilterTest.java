package org.orcid.api.common.openapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import org.junit.Test;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

public class OrcidOpenApiFilterTest {

    private final OrcidOpenApiFilter filter = new OrcidOpenApiFilter();

    @Test
    public void removesNonApiOperations() {
        for (String id : OrcidOpenApiFilter.NON_API_OPERATIONS) {
            assertFalse(id, filter(new Operation().operationId(id)).isPresent());
        }
        assertTrue(filter(new Operation().operationId("viewWork")).isPresent());
    }

    @Test
    public void stripsTheQualityFactorFromMediaTypes() {
        Operation operation = new Operation().operationId("createWork")
                .requestBody(new RequestBody().content(content("application/vnd.orcid+xml; qs=5", "application/json; qs=4", "application/json")))
                .responses(new ApiResponses().addApiResponse("200", new ApiResponse().content(content("application/orcid+json; qs=2", "text/csv"))));

        Operation filtered = filter(operation).get();

        assertEquals(new ArrayList<>(filtered.getRequestBody().getContent().keySet()), java.util.List.of("application/vnd.orcid+xml", "application/json"));
        assertEquals(new ArrayList<>(filtered.getResponses().get("200").getContent().keySet()), java.util.List.of("application/orcid+json", "text/csv"));
    }

    @Test
    public void leavesOperationsWithoutContentAlone() {
        Operation filtered = filter(new Operation().operationId("deleteWork").responses(new ApiResponses().addApiResponse("204", new ApiResponse()))).get();
        assertEquals(null, filtered.getResponses().get("204").getContent());
    }

    @Test
    public void removesSchemasNoOperationUses() {
        assertTrue(filter.isRemovingUnreferencedDefinitions());
    }

    @Test
    public void pinsTheVersionTheIdentifiersResourceTakesAsAPathVariable() {
        Operation get = new Operation().operationId("viewIdentifierTypes").addParametersItem(new PathParameter().name("version"));
        OpenAPI openAPI = new OpenAPI().paths(new Paths().addPathItem("/v{version}/identifiers", new PathItem().get(get))
                .addPathItem("/v3.0/{orcid}/works", new PathItem().get(new Operation().operationId("viewWorks"))));

        OpenAPI filtered = filter.filterOpenAPI(openAPI, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()).get();

        assertEquals(java.util.Set.of("/v3.0/identifiers", "/v3.0/{orcid}/works"), filtered.getPaths().keySet());
        assertTrue(filtered.getPaths().get("/v3.0/identifiers").getGet().getParameters().isEmpty());
    }

    @Test
    public void everyPropertyMayBeNullBecauseTheJsonProviderWritesNulls() {
        Schema<?> plain = filter.filterSchemaProperty(new StringSchema(), null, "short-description", Collections.emptyMap(), Collections.emptyMap(),
                Collections.emptyMap()).get();
        assertTrue(plain.getNullable());

        @SuppressWarnings("rawtypes")
        Schema reference = new Schema().$ref("#/components/schemas/Source");
        Schema<?> wrapped = filter.filterSchemaProperty(reference, null, "source", Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap())
                .get();
        assertTrue(wrapped instanceof ComposedSchema);
        assertTrue(wrapped.getNullable());
        assertEquals("#/components/schemas/Source", ((ComposedSchema) wrapped).getAllOf().get(0).get$ref());
    }

    private Optional<Operation> filter(Operation operation) {
        return filter.filterOperation(operation, null, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
    }

    private static Content content(String... mediaTypes) {
        Content content = new Content();
        for (String mediaType : mediaTypes) {
            content.addMediaType(mediaType, new MediaType());
        }
        return content;
    }
}
