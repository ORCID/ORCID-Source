package org.orcid.api.common.openapi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.RefUtils;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

/**
 * Used only by the OpenAPI generator (swagger-maven-plugin-jakarta), never at
 * runtime. Describes two kinds of property the way the JSON provider writes
 * them, where the generator's own reading of the class differs:
 * <ul>
 * <li>A list annotated with {@code @XmlElements}, such as the summaries of an
 * affiliation group or the elements of a bulk, holds one object per element,
 * named after the element: {@code [{"employment-summary": {...}}]}.</li>
 * <li>A {@link Currency} is written as its ISO 4217 code ({@code CurrencyAdapter}),
 * not as an object.</li>
 * </ul>
 */
public class OrcidModelConverter implements ModelConverter {

    @SuppressWarnings("rawtypes")
    @Override
    public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        JavaType javaType = type.getType() == null ? null : TypeFactory.defaultInstance().constructType(type.getType());
        if (javaType != null && Currency.class.equals(javaType.getRawClass())) {
            return new StringSchema().description("ISO 4217 currency code, for example `USD`.");
        }
        XmlElements elements = annotation(type.getCtxAnnotations(), XmlElements.class);
        if (javaType != null && elements != null && javaType.isCollectionLikeType()) {
            return wrappedElements(elements, javaType.getContentType().getRawClass(), context);
        }
        return chain.hasNext() ? chain.next().resolve(type, context, chain) : null;
    }

    @SuppressWarnings("rawtypes")
    private static Schema wrappedElements(XmlElements elements, Class<?> elementBound, ModelConverterContext context) {
        List<Schema> wrappers = new ArrayList<>();
        for (XmlElement element : elements.value()) {
            if (!elementBound.isAssignableFrom(element.type())) {
                continue;
            }
            Schema model = context.resolve(new AnnotatedType(element.type()).resolveAsRef(true));
            Schema target = model != null && model.getName() != null ? new Schema<>().$ref(RefUtils.constructRef(model.getName())) : model;
            wrappers.add(new ObjectSchema().addProperty(element.name(), target).addRequiredItem(element.name()));
        }
        Schema items = wrappers.size() == 1 ? wrappers.get(0) : new ComposedSchema().oneOf(wrappers);
        return new ArraySchema().items(items);
    }

    private static <A extends Annotation> A annotation(Annotation[] annotations, Class<A> annotationType) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotationType.isInstance(annotation)) {
                    return annotationType.cast(annotation);
                }
            }
        }
        return null;
    }
}
