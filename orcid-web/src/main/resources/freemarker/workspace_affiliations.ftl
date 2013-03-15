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
<#if (profile.orcidBio.affiliations)?? && (profile.orcidBio.affiliations?size != 0)>
    <ul class="workspace-affiliations workspace-body-list">
        <#list profile.orcidBio.affiliations as affiliation>
            <li>
                <#-- TODO: move to macros/orcid.ftl -->
                <@orcid.itemDetails affiliation.affiliationName "" "h4" />
                <@orcid.itemDetails affiliation.roleTitle "Role" />
                <@orcid.itemDetails affiliation.departmentName "Department" />
                <@orcid.itemDetails (affiliation.address.country.content)! "Country" />

                <div>
                    <@orcid.itemDetails (affiliation.startDate.value)!"" "Start" "span" />
                    <@orcid.itemDetails (affiliation.endDate.value)!"" "End" "span" />
                </div>
        
                <#if affiliation.visibility! != 'public' >
                    <#assign visibilityClass="protected"/>
                <#else>
                    <#assign visibilityClass=""/>
                </#if>
                <@orcid.privacy "hello" affiliation.visibility! />
            </li>
        </#list>
    </ul>
<#else>
    <div class="alert alert-info">
        <strong>${springMacroRequestContext.getMessage("workspace_affiliations.havenotaddaffiliation")}</strong>
    </div>
</#if>