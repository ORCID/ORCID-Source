<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#-- @ftlvariable name="profile" type="org.orcid.jaxb.model.message.OrcidProfile" -->
<@public >
<#escape x as x?html>
<div class="row workspace-top public-profile">
    <div class="span3">
        <div class="workspace-left workspace-profile">
            <h2 class="full-name">
                <#if (profile.orcidBio.personalDetails.creditName.content)??>
                    ${(profile.orcidBio.personalDetails.creditName.content)!}
                <#else>
                    ${(profile.orcidBio.personalDetails.givenNames.content)!} ${(profile.orcidBio.personalDetails.familyName.content)!}
                </#if>
            </h2>
            <p><small id="orcid-id" class="orcid-id">${baseUri}/${(profile.orcid.value)!}</small></p>
            <#if (profile.orcidBio.personalDetails.otherNames)?? && (profile.orcidBio.personalDetails.otherNames.otherName?size != 0)>
                <p><strong>${springMacroRequestContext.getMessage("public_profile.labelAlsoknownas")}</strong><br />
                  <#list profile.orcidBio.personalDetails.otherNames.otherName as otherName>
                    ${otherName.content}<#if otherName_has_next><br /></#if>
                  </#list>
            </#if>
            <#if (profile.orcidBio.contactDetails.address.country.content)??>
                <p><strong>${springMacroRequestContext.getMessage("public_profile.labelCountry")}</strong>
                ${(profile.orcidBio.contactDetails.address.country.content)!}
                </p>
            </#if>
            <#if (profile.orcidBio.keywords)?? && (profile.orcidBio.keywords.keyword?size != 0)>
                <p><strong>${springMacroRequestContext.getMessage("public_profile.labelKeywords")}</strong> 
                    <#list profile.orcidBio.keywords.keyword as keyword>
                        ${keyword.content}<#if keyword_has_next>,</#if>
                    </#list></p>
            </#if>
            <#if (profile.orcidBio.researcherUrls)?? && (profile.orcidBio.researcherUrls.researcherUrl?size != 0)>
                <p><strong>${springMacroRequestContext.getMessage("public_profile.labelWebsites")}</strong><br/>
                    <#list profile.orcidBio.researcherUrls.researcherUrl as url>
                        <a href="${url.url.value}"><#if (url.urlName.content)! != "">${url.urlName.content}<#else>${url.url.value}</#if></a><#if url_has_next><br/></#if>
                    </#list></p>
            </#if>
            <#if (profile.orcidBio.externalIdentifiers)?? && (profile.orcidBio.externalIdentifiers.externalIdentifier?size != 0)>
                <p><strong>${springMacroRequestContext.getMessage("public_profile.labelOtherIDs")}</strong> <br/>
                    <#list profile.orcidBio.externalIdentifiers.externalIdentifier as external>
                        <#if (external.externalIdUrl.value)??>
                            <a href="${external.externalIdUrl.value}">${(external.externalIdCommonName.content)!}: ${(external.externalIdReference.content)!}</a><#if external_has_next><br/></#if>
                        <#else>
                            ${(external.externalIdCommonName.content)!}: ${(external.externalIdReference.content)!}<#if external_has_next><br/></#if>
                        </#if>    
                    </#list>
                </p>
            </#if>
        </div>
    </div>
    <div class="span9">
        <div class="workspace-right">
            <div class="workspace-inner workspace-public">
                <#if (profile.orcidBio.biography.content)??>
                    <h3 class="workspace-header-public no-border">${springMacroRequestContext.getMessage("public_profile.h3PersonalInformation")}</h3>
                    <p><b>${springMacroRequestContext.getMessage("public_profile.labelBiography")}</b><br />${(profile.orcidBio.biography.content)!}</p>
                </#if>
                <#assign publicProfile = true />
                <#include "workspace_preview_activities.ftl"/>
            </div>
        </div>
    </div>
</div>
</#escape>
</@public>