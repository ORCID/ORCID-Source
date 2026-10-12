package org.orcid.api.common.openapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.Test;
import org.orcid.utils.OrcidStringUtils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.media.DateTimeSchema;
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
    public void aMediaTypeAnAnnotationDeclaresWinsOverTheProducedOne() {
        Content content = new Content();
        content.addMediaType("application/json", new MediaType().schema(new Schema<>().$ref("#/components/schemas/Record")));
        content.addMediaType("application/ld+json; qs=0.2", new MediaType().schema(new Schema<>().$ref("#/components/schemas/Record")));
        content.addMediaType("application/ld+json", new MediaType().schema(new Schema<>().type("object")));

        Content stripped = OrcidOpenApiFilter.withoutMediaTypeParameters(content);

        assertEquals(java.util.List.of("application/json", "application/ld+json"), new ArrayList<>(stripped.keySet()));
        assertEquals("object", stripped.get("application/ld+json").getSchema().getType());
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
    public void everyValueMayBeNullBecauseTheJsonProviderWritesNulls() {
        Schema<?> plain = property(new StringSchema(), "short-description");
        assertTrue(plain.getNullable());

        @SuppressWarnings("rawtypes")
        Schema reference = new Schema().$ref("#/components/schemas/Source");
        Schema<?> unchanged = property(reference, "source");
        assertEquals("#/components/schemas/Source", unchanged.get$ref());
        assertEquals(null, unchanged.getNullable());

        Schema<?> component = filter.filterSchema(new Schema<>().type("object"), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap())
                .get();
        assertTrue(component.getNullable());
    }

    @Test
    public void aNullableEnumListsNullAmongItsValues() {
        StringSchema relationship = new StringSchema();
        relationship.setEnum(java.util.List.of("self", "part-of"));
        assertEquals(java.util.Arrays.asList("self", "part-of", null), property(relationship, "external-id-relationship").getEnum());
        assertEquals(java.util.Arrays.asList("self", "part-of", null), property(relationship, "external-id-relationship").getEnum());
    }

    @Test
    public void datesAreMillisecondsInJson() {
        Schema<?> date = property(new DateTimeSchema(), "value");
        assertEquals("integer", date.getType());
        assertEquals("int64", date.getFormat());
        assertTrue(date.getNullable());
    }

    @Test
    public void describesTheIdAndPutCodePathParameters() {
        Parameter orcid = filter.filterParameter(new PathParameter().name("orcid").schema(new StringSchema()), null, null, Collections.emptyMap(),
                Collections.emptyMap(), Collections.emptyMap()).get();
        assertTrue(orcid.getDescription().contains("0000-0002-1825-0097"));
        Pattern documented = Pattern.compile(orcid.getSchema().getPattern());
        for (String id : new String[] { "0000-0002-1825-0097", "0000-0002-9079-593X" }) {
            assertTrue(id, OrcidStringUtils.isValidOrcid(id));
            assertTrue(id, documented.matcher(id).matches());
        }
        for (String id : new String[] { "0000-0002-9079-593x", "0000-0002-1825", "https://orcid.org/0000-0002-1825-0097" }) {
            assertFalse(id, OrcidStringUtils.isValidOrcid(id));
            assertFalse(id, documented.matcher(id).matches());
        }

        Parameter putCode = filter.filterParameter(new PathParameter().name("putCode").schema(new StringSchema()), null, null, Collections.emptyMap(),
                Collections.emptyMap(), Collections.emptyMap()).get();
        assertTrue(putCode.getDescription().startsWith("Put-code"));

        Parameter other = filter.filterParameter(new QueryParameter().name("orcid").schema(new StringSchema()), null, null, Collections.emptyMap(),
                Collections.emptyMap(), Collections.emptyMap()).get();
        assertEquals(null, other.getDescription());
        assertEquals(null, other.getSchema().getPattern());
    }

    private Schema<?> property(Schema<?> property, String name) {
        return filter.filterSchemaProperty(property, null, name, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()).get();
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
