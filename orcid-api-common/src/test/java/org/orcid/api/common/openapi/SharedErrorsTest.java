package org.orcid.api.common.openapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;

public class SharedErrorsTest {

    private static Operation operation(boolean member, String... pathParameters) {
        Content json = new Content().addMediaType("application/vnd.orcid+json", new MediaType()).addMediaType("application/json", new MediaType());
        Operation operation = new Operation().responses(new ApiResponses().addApiResponse("200", new ApiResponse().content(json)));
        for (String name : pathParameters) {
            operation.addParametersItem(new PathParameter().name(name));
        }
        if (member) {
            operation.addSecurityItem(new SecurityRequirement().addList("orcid_auth", "/read-limited"));
        }
        return operation;
    }

    private static String ref(ApiResponse response, String type) {
        return response.getContent().get(type).getSchema().get$ref();
    }

    @Test
    public void aMemberItemReadCanAnswerEveryRecordAndTokenError() {
        Operation read = operation(true, "orcid", "putCode");
        SharedErrors.addTo(read, "/v3.0/{orcid}/work/{putCode}", "GET");

        assertEquals(Set.of("200", "301", "400", "401", "403", "404", "409"), read.getResponses().keySet());
        assertTrue(read.getResponses().get("400").getDescription().contains("9034"));
        assertEquals(SharedErrors.ORCID_ERROR, ref(read.getResponses().get("401"), "application/vnd.orcid+json"));
        List<Schema> either = read.getResponses().get("401").getContent().get("application/json").getSchema().getAnyOf();
        assertEquals(SharedErrors.OAUTH_ERROR, either.get(1).get$ref());
        assertNotNull(read.getResponses().get("301").getHeaders().get("Location"));
        assertFalse(read.getResponses().get("409").getDescription().contains("deprecated"));
    }

    @Test
    public void aMemberAddCanBeInvalidOrADuplicate() {
        Operation add = operation(true, "orcid").requestBody(new RequestBody());
        SharedErrors.addTo(add, "/v3.0/{orcid}/work", "POST");

        ApiResponse badRequest = add.getResponses().get("400");
        assertTrue(badRequest.getDescription().contains("9047"));
        assertEquals("string", badRequest.getContent().get("text/plain").getSchema().getType());
        assertTrue(add.getResponses().get("409").getDescription().contains("9021"));
        assertFalse(add.getResponses().containsKey("301"));
    }

    @Test
    public void aPublicReadOnlyRefusesABadToken() {
        Operation read = operation(false, "orcid", "putCode");
        SharedErrors.addTo(read, "/v3.0/{orcid}/work/{putCode}", "GET");

        assertEquals(SharedErrors.OAUTH_ERROR, ref(read.getResponses().get("401"), "application/json"));
        assertEquals(1, read.getResponses().get("401").getContent().size());
        assertTrue(read.getResponses().get("403").getDescription().contains("9039"));
        assertTrue(read.getResponses().get("404").getDescription().contains("not a number"));
        assertFalse(read.getResponses().containsKey("400"));
    }

    @Test
    public void onlyRecordOperationsCanFindTheRecordLockedOrMissing() {
        Operation webhook = operation(true, "orcid", "webhook_uri");
        SharedErrors.addTo(webhook, "/{orcid}/webhook/{webhook_uri}", "PUT");
        assertFalse(webhook.getResponses().containsKey("404"));
        assertFalse(webhook.getResponses().containsKey("409"));

        Operation search = operation(false);
        SharedErrors.addTo(search, "/v3.0/search", "GET");
        assertEquals(Set.of("200", "401"), search.getResponses().keySet());
    }

    @Test
    public void aResponseTheOperationDeclaresIsKept() {
        Operation biography = operation(false, "orcid");
        ApiResponse declared = new ApiResponse().description("The record has no biography (error 9041)");
        biography.getResponses().addApiResponse("404", declared);
        SharedErrors.addTo(biography, "/v3.0/{orcid}/biography", "GET");
        assertEquals(declared, biography.getResponses().get("404"));
    }

    @Test
    public void aDeleteWithNoContentGetsErrorsInEveryOrcidMediaType() {
        Operation delete = new Operation().responses(new ApiResponses().addApiResponse("204", new ApiResponse()))
                .addParametersItem(new PathParameter().name("orcid")).addParametersItem(new PathParameter().name("putCode"))
                .addSecurityItem(new SecurityRequirement().addList("orcid_auth", "/activities/update"));
        SharedErrors.addTo(delete, "/v3.0/{orcid}/work/{putCode}", "DELETE");
        assertEquals(SharedErrors.ORCID_MEDIA_TYPES.size(), delete.getResponses().get("404").getContent().size());
        assertTrue(delete.getResponses().get("409").getDescription().contains("deprecated"));
    }

    @Test
    public void aMemberItemNamesPrivateItemsOnReadsAndOtherClientsItemsOnWrites() {
        Operation read = operation(true, "orcid", "putCode");
        SharedErrors.addTo(read, "/v3.0/{orcid}/work/{putCode}", "GET");
        String readForbidden = read.getResponses().get("403").getDescription();
        assertTrue(readForbidden.contains("public client") && readForbidden.contains("9038") && readForbidden.contains("9013"));
        assertTrue(read.getResponses().get("401").getDescription().contains("9017"));

        Operation delete = operation(true, "orcid", "putCode");
        SharedErrors.addTo(delete, "/v3.0/{orcid}/work/{putCode}", "DELETE");
        assertTrue(delete.getResponses().get("403").getDescription().contains("9010"));
        assertFalse(delete.getResponses().get("403").getDescription().contains("9013"));
    }

    @Test
    public void bulkWorksReportDuplicatesInTheirOwnBody() {
        Operation bulk = operation(true, "orcid").operationId("createWorks").requestBody(new RequestBody());
        SharedErrors.addTo(bulk, "/v3.0/{orcid}/works", "POST");
        assertFalse(bulk.getResponses().get("409").getDescription().contains("9021"));
        assertTrue(bulk.getResponses().get("409").getDescription().contains("9018"));
    }

    @Test
    public void somePublicListsAnswerEmptyForAnUnknownId() {
        Operation distinctions = operation(false, "orcid").operationId("viewDistinctions");
        SharedErrors.addTo(distinctions, "/v3.0/{orcid}/distinctions", "GET");
        assertFalse(distinctions.getResponses().containsKey("404"));

        Operation employments = operation(false, "orcid").operationId("viewEmployments");
        SharedErrors.addTo(employments, "/v3.0/{orcid}/employments", "GET");
        assertTrue(employments.getResponses().containsKey("404"));

        Operation memberDistinctions = operation(true, "orcid").operationId("viewDistinctions");
        SharedErrors.addTo(memberDistinctions, "/v3.0/{orcid}/distinctions", "GET");
        assertTrue(memberDistinctions.getResponses().containsKey("404"));
    }

    @Test
    public void thePublicBiographyCanBeNotPublic() {
        Operation biography = operation(false, "orcid").operationId("viewBiography");
        SharedErrors.addTo(biography, "/v3.0/{orcid}/biography", "GET");
        assertTrue(biography.getResponses().get("403").getDescription().contains("9039"));
    }

    @Test
    public void notificationsHaveTheirOwnCodes() {
        Operation read = operation(true, "orcid", "id");
        SharedErrors.addTo(read, "/v3.0/{orcid}/notification-permission/{id}", "GET");
        assertTrue(read.getResponses().get("404").getDescription().contains("9029"));
        assertTrue(read.getResponses().get("403").getDescription().contains("9004"));
        assertFalse(read.getResponses().get("409").getDescription().contains("9044"));

        Operation archive = operation(true, "orcid", "id");
        SharedErrors.addTo(archive, "/v3.0/{orcid}/notification-permission/{id}", "DELETE");
        assertTrue(archive.getResponses().get("409").getDescription().contains("9044"));
    }

    @Test
    public void anUnknownGroupIdRecordIsABadRequest() {
        Operation update = operation(true, "putCode").requestBody(new RequestBody());
        SharedErrors.addTo(update, "/v3.0/group-id-record/{putCode}", "PUT");
        assertTrue(update.getResponses().get("400").getDescription().contains("9026"));
        assertFalse(update.getResponses().containsKey("404"));
    }
}
