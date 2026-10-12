package org.orcid.api.common.openapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.orcid.jaxb.model.v3.release.common.Amount;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;

import com.fasterxml.jackson.databind.type.TypeFactory;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Calls the converter with the real fields' types and annotations. Swagger's
 * own resolver cannot run on this module's test classpath (orcid-model brings
 * commons-lang3 3.2), so the context stands in for it; the generated files and
 * the contract tests cover the whole chain.
 */
public class OrcidModelConverterTest {

    private final OrcidModelConverter converter = new OrcidModelConverter();

    private final ModelConverterContext context = mock(ModelConverterContext.class);

    {
        // The resolver names a model class's schema after the class, as @Schema(name) does for these.
        when(context.resolve(any(AnnotatedType.class)))
                .thenAnswer(call -> new Schema<>().name(((Class<?>) ((AnnotatedType) call.getArgument(0)).getType()).getSimpleName()));
    }

    @SuppressWarnings("rawtypes")
    private Schema resolve(Type type, Field field) {
        AnnotatedType annotated = new AnnotatedType(type).ctxAnnotations(field.getAnnotations());
        return converter.resolve(annotated, context, Collections.<ModelConverter> emptyIterator());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void eachBulkElementIsAnObjectNamedAfterItsElement() throws Exception {
        Field bulk = WorkBulk.class.getDeclaredField("bulk");
        List<Schema> wrappers = resolve(bulk.getGenericType(), bulk).getItems().getOneOf();
        assertEquals(2, wrappers.size());
        assertEquals("#/components/schemas/Work", ((Schema) wrappers.get(0).getProperties().get("work")).get$ref());
        assertEquals(List.of("work"), wrappers.get(0).getRequired());
        assertEquals("#/components/schemas/OrcidError", ((Schema) wrappers.get(1).getProperties().get("error")).get$ref());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void anAffiliationGroupOnlyHoldsItsOwnSummaryType() throws Exception {
        // AffiliationGroup<EmploymentSummary>: the generator resolves the list with T bound.
        Field summaries = AffiliationGroup.class.getDeclaredField("summaries");
        Type bound = TypeFactory.defaultInstance().constructCollectionType(List.class, EmploymentSummary.class);
        Schema items = resolve(bound, summaries).getItems();
        assertNull(items.getOneOf());
        assertEquals(1, items.getProperties().size());
        assertEquals("#/components/schemas/EmploymentSummary", ((Schema) items.getProperties().get("employment-summary")).get$ref());
    }

    @Test
    public void aCurrencyIsItsCode() throws Exception {
        Field currency = Amount.class.getDeclaredField("currencyCode");
        assertEquals("string", resolve(currency.getGenericType(), currency).getType());
    }

    @Test
    public void everythingElseGoesDownTheChain() throws Exception {
        Field value = Amount.class.getDeclaredField("content");
        @SuppressWarnings("rawtypes")
        Schema next = new Schema<>();
        ModelConverter rest = (type, ctx, chain) -> next;
        AnnotatedType annotated = new AnnotatedType(value.getGenericType()).ctxAnnotations(value.getAnnotations());
        assertSame(next, converter.resolve(annotated, context, List.of(rest).iterator()));
    }

    @Test
    public void aListNoElementFitsGoesDownTheChain() throws Exception {
        // No @XmlElements entry is an Amount, so there is nothing to describe: the generator's own reading is kept.
        Field summaries = AffiliationGroup.class.getDeclaredField("summaries");
        Type unrelated = TypeFactory.defaultInstance().constructCollectionType(List.class, Amount.class);
        @SuppressWarnings("rawtypes")
        Schema next = new Schema<>();
        ModelConverter rest = (type, ctx, chain) -> next;
        AnnotatedType annotated = new AnnotatedType(unrelated).ctxAnnotations(summaries.getAnnotations());
        assertSame(next, converter.resolve(annotated, context, List.of(rest).iterator()));
    }
}
