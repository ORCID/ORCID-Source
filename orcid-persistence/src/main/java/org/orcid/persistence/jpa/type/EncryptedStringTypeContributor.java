package org.orcid.persistence.jpa.type;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BasicType;
import org.hibernate.type.BasicTypeReference;
import org.hibernate.type.StandardBasicTypes;

/**
 * Registers the legacy encryptedString logical type name in Hibernate 6.
 *
 * The old TypeDef or TypeDefs annotation API was removed, so this keeps
 * compatibility for mappings that still refer to encryptedString by name.
 */
public class EncryptedStringTypeContributor implements TypeContributor {

    public static final String ENCRYPTED_STRING_TYPE_NAME = "encryptedString";

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        BasicType<?> stringType = resolveStringType(typeContributions);
        typeContributions.getTypeConfiguration()
                .getBasicTypeRegistry()
                .register(stringType, ENCRYPTED_STRING_TYPE_NAME);
    }

    private BasicType<?> resolveStringType(TypeContributions typeContributions) {
        BasicType<?> stringType = typeContributions.getTypeConfiguration()
                .getBasicTypeRegistry()
                .getRegisteredType("string");
        if (stringType != null) {
            return stringType;
        }

        BasicTypeReference<String> stringReference = StandardBasicTypes.STRING;
        return typeContributions.getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(stringReference);
    }
}