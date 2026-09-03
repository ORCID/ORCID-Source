package org.orcid.core.adapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverService;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.pojo.Local;

/**
 * Builds the REAL MapStruct {@code JpaJaxb*AdapterImpl} mappers over mocked leaf dependencies, with
 * no Spring context.
 *
 * <p>
 * This replaces {@code MockedMapperFacadeFactory}, which built Orika {@code MapperFacade}s. Orika was
 * removed from every pom by the MapStruct migration on main, along with the two
 * {@code MapperFacadeFactory} classes it wired. It is also the replacement for
 * {@code org.orcid.core.adapter.MockSourceNameCache}, whose {@code extends BaseTest} was the real
 * database dependency of the adapter tests rather than any annotation on them.
 * </p>
 *
 * <p>
 * <b>Why the mapper is real.</b> The behaviour under test is the mapping configuration itself, so a
 * mocked mapper would turn every adapter assertion into an assertion about a mock. Only the leaves are
 * faked - caches, DAOs, the message bundle and the identifier normaliser - and each has its own tests.
 * {@link SourceEntityUtils} and {@link OrcidUrlManager} stay real because they are what produce the
 * source path, source uri and assertion origin the tests assert on.
 * </p>
 *
 * <p>
 * <b>How the wiring works.</b> {@code Mappers.getMapper(X.class)} returns the generated subclass, the
 * same pattern main uses in {@code SourceMapperV2Test} and
 * {@code JpaJaxbInvalidRecordDataChangeAdapterImplTest}. Because these mappers declare
 * {@code componentModel = "spring"}, both their {@code @Autowired} fields and the fields MapStruct
 * generates for {@code uses = {...}} arrive null, so {@link #get(Class)} injects them reflectively by
 * type: a known leaf if the type is one, otherwise a nested mapper built the same way. Injecting by
 * type rather than by name means a new {@code uses} entry on main needs no change here.
 * </p>
 */
public class MockedMapStructAdapters {

    /** Same value as the old MockSourceNameCache constant. */
    public static final String CLIENT_SOURCE_ID = "APP-0000000000000001";

    public static final String BASE_URL = "https://testserver.orcid.org";

    /** Real POJO - getBaseHost() parses baseUrl, and the source uri assertions depend on it. */
    public final OrcidUrlManager orcidUrlManager = new OrcidUrlManager();

    public final SourceNameCacheManager sourceNameCacheManager = mock(SourceNameCacheManager.class);

    public final ClientDetailsEntityCacheManager clientDetailsEntityCacheManager = mock(ClientDetailsEntityCacheManager.class);

    public final RecordNameManagerReadOnly recordNameManagerReadOnlyV3 = mock(RecordNameManagerReadOnly.class);

    /** v2 only: decides client-vs-orcid source for non APP- prefixed ids. */
    public final ClientDetailsManagerReadOnly clientDetailsManagerReadOnly = mock(ClientDetailsManagerReadOnly.class);

    /**
     * Mocked. No adapter test in this family asserts ExternalID.getNormalized() /
     * getNormalizedUrl(); the JSON external identifier mappers null guard the result. The real
     * normalisation chain is exercised by JSONWorkExternalIdentifiersMapperV3Test and by
     * PIDNormalizationServiceTest.
     */
    public final PIDNormalizationService norm = mock(PIDNormalizationService.class);

    /** Mocked: the real one resolves DOIs over the network. */
    public final PIDResolverService resolverService = mock(PIDResolverService.class);

    /**
     * Not a Mockito mock: LocaleManager.resolveMessage is a varargs method called with both zero and
     * two parameters, and the mappers call .replace(...) on the result, so it must never be null.
     */
    public final LocaleManager localeManager = new EchoLocaleManager();

    public final WorkDao workDao = mock(WorkDao.class);

    public final IdentityProviderManager identityProviderManager = mock(IdentityProviderManager.class);

    public final EncryptionManager encryptionManager = mock(EncryptionManager.class);

    /** Real: it is what produces retrieveSourcePath(), retriveSourceUri() and getAssertionOriginOrcid(). */
    public final SourceEntityUtils sourceEntityUtils = new SourceEntityUtils();

    /** Injection registry, consulted in insertion order; the first assignable entry wins. */
    private final Map<Class<?>, Object> leaves = new LinkedHashMap<Class<?>, Object>();

    /** One instance per mapper type, so a mapper shared by two adapters is wired once. */
    private final Map<Class<?>, Object> mappers = new HashMap<Class<?>, Object>();

    public MockedMapStructAdapters() {
        orcidUrlManager.setBaseUrl(BASE_URL);
        when(sourceNameCacheManager.retrieve(CLIENT_SOURCE_ID)).thenReturn("Client name");

        setField(sourceEntityUtils, "orcidUrlManager", orcidUrlManager);
        setField(sourceEntityUtils, "sourceNameCacheManager", sourceNameCacheManager);
        setField(sourceEntityUtils, "clientDetailsEntityCacheManager", clientDetailsEntityCacheManager);
        setField(sourceEntityUtils, "recordNameManagerReadOnlyV3", recordNameManagerReadOnlyV3);

        leaves.put(OrcidUrlManager.class, orcidUrlManager);
        leaves.put(SourceNameCacheManager.class, sourceNameCacheManager);
        leaves.put(ClientDetailsEntityCacheManager.class, clientDetailsEntityCacheManager);
        leaves.put(RecordNameManagerReadOnly.class, recordNameManagerReadOnlyV3);
        leaves.put(ClientDetailsManagerReadOnly.class, clientDetailsManagerReadOnly);
        leaves.put(PIDNormalizationService.class, norm);
        leaves.put(PIDResolverService.class, resolverService);
        leaves.put(LocaleManager.class, localeManager);
        leaves.put(WorkDao.class, workDao);
        leaves.put(IdentityProviderManager.class, identityProviderManager);
        leaves.put(EncryptionManager.class, encryptionManager);
        leaves.put(SourceEntityUtils.class, sourceEntityUtils);
    }

    /**
     * @param mapperClass the abstract {@code @Mapper} class, e.g.
     *        {@code org.orcid.core.adapter.mapstruct.v3.impl.JpaJaxbWorkAdapterImpl.class}
     * @return the generated implementation, with every collaborator wired.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> mapperClass) {
        Object existing = mappers.get(mapperClass);
        if (existing != null) {
            return (T) existing;
        }
        T instance = Mappers.getMapper(mapperClass);
        // Registered before wiring so that a cycle between two mappers terminates.
        mappers.put(mapperClass, instance);
        wire(instance);
        return instance;
    }

    private void wire(Object instance) {
        for (Class<?> type = instance.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                Object value = valueFor(field.getType());
                if (value == null) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    field.set(instance, value);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not inject " + field.getName() + " on " + type.getName(), e);
                }
            }
        }
        callPostConstruct(instance);
    }

    /** A known leaf wins over building a mapper, so an explicitly mocked collaborator is never rebuilt. */
    private Object valueFor(Class<?> fieldType) {
        for (Map.Entry<Class<?>, Object> leaf : leaves.entrySet()) {
            if (fieldType.isAssignableFrom(leaf.getKey())) {
                return leaf.getValue();
            }
        }
        if (fieldType.isAnnotationPresent(Mapper.class)) {
            return get(fieldType);
        }
        return null;
    }

    /** Spring calls these on startup; nothing else does, and at least one mapper resolves messages there. */
    private void callPostConstruct(Object instance) {
        for (Class<?> type = instance.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            for (Method method : type.getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class) && method.getParameterCount() == 0) {
                    method.setAccessible(true);
                    try {
                        method.invoke(instance);
                    } catch (Exception e) {
                        throw new IllegalStateException("@PostConstruct failed on " + type.getName(), e);
                    }
                }
            }
        }
    }

    private static void setField(Object target, String name, Object value) {
        for (Class<?> type = target.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                // try the superclass
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Could not set " + name + " on " + type.getName(), e);
            }
        }
        throw new IllegalStateException("No field " + name + " on " + target.getClass().getName());
    }

    /** Returns the message code itself: never null, and never accidentally meaningful. */
    private static class EchoLocaleManager implements LocaleManager {

        @Override
        public Locale getLocale() {
            return Locale.ENGLISH;
        }

        @Override
        public Locale getLocaleFromOrcidProfile(OrcidProfile orcidProfile) {
            return Locale.ENGLISH;
        }

        @Override
        public String resolveMessage(String messageCode, Object... messageParams) {
            return messageCode;
        }

        @Override
        public String resolveMessage(String messageCode, Locale locale, Object... messageParams) {
            return messageCode;
        }

        @Override
        public Local getJavascriptMessages(Locale locale) {
            return null;
        }

        @Override
        public Map<String, String> getCountries(Locale locale) {
            return new HashMap<String, String>();
        }
    }
}
