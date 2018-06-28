# How to add a new activity type

This is quite a complex beast.  Below is a list of things that need to be done at the time of writing (28/06/18), in the hope that it will help future developers.  It's not in any particular order:

The basics are adding models, db tables, dao, adapters, manager and API methods.  But you also have to:

- Add to activity summary (inc tests)
  - orcid-core/src/main/java/org/orcid/core/manager/v3/read_only/impl/ActivitiesSummaryManagerReadOnlyImpl.java
  - orcid-model/src/main/java/org/orcid/jaxb/model/v3/rc1/record/summary/ActivitiesSummary.java

- Handle deactivation:
 - org.orcid.core.manager.v3.impl.ProfileEntityManagerImpl.clearRecord

- Set paths (including summary list path):  
  - orcid-api-common/src/main/java/org/orcid/api/common/util/v3/ActivityUtils.java

- Filter by visibility (including summary lists):
  - orcid-core/src/main/java/org/orcid/core/manager/v3/impl/OrcidSecurityManagerImpl.java

- Set last modified (including summary list last modified):
  - orcid-core/src/main/java/org/orcid/core/version/impl/Api3_0_RC1LastModifiedDatesHelper.java

- Valide JSON:
  - orcid-api-common/src/main/java/org/orcid/api/common/exception/JSONInputValidator.java

- Add to jaxb context
  - orcid-api-common/src/main/java/org/orcid/api/common/jaxb/OrcidValidationJaxbContextResolver.java
  - orcid-model/src/main/java/org/orcid/jaxb/model/v3/rc1/record/package-info.java

- Input validation
  - orcid-core/src/main/java/org/orcid/core/manager/v3/validator/ActivityValidator.java

- Set sources:
  - orcid-core/src/main/java/org/orcid/core/utils/v3/SourceUtils.java

- Handle notification item type:
  - orcid-model/src/main/java/org/orcid/jaxb/model/v3/rc1/notification/permission/ItemType.java
  - orcid-model/src/main/java/org/orcid/jaxb/model/v3/rc1/notification/amended/AmendedSection.java

- Add to profileEntity
  - orcid-persistence/src/main/java/org/orcid/persistence/jpa/entities/ProfileEntity.java

- Add to persistence.xml
  - orcid-persistence/src/main/resources/META-INF/persistence.xml

- Ensure your unit tests delete correctly
  - orcid-test/src/main/java/org/orcid/test/DBUnitTest.java

- Create XSD's
- Create entity POJO
- Create model object
- Create the DAO
- Create dao and read only dao beans
- Create manager and read only manager
- Create orika mappings org.orcid.core.adapter.v3.impl.MapperFacadeFactory
- Create jpa -> jaxb adapter under org.orcid.core.adapter.v3
- Create Form object to return to UI
- Create controller
- Create the UI components, services, modules, etc 
- Include the new type in the org.orcid.frontend.web.controllers.PublicProfileController
- Create unit test data, follow the example of /orcid-test/src/main/resources/data/PeerReviewEntityData.xml: 5 activities per orcid: 1 public with client as source, 1 limited with client as source, 1 private with client as source, 1 limited with the user as source, 1 private with the user as source
- Create new unit tests for every new component
- Update all tests under /orcid-api-web/src/test/java/org/orcid.api/memberV3.server/delegator
- Update all unit test /orcid-pub-web/src/test/java/org/orcid/api/publicV3/server/PublicV3ApiServiceDelegatorTest.java
- Update unit test /orcid-pub-web/src/test/java/org/orcid/api/publicV3/server/security/PublicAPISecurityManagerV3Test.java

