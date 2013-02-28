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
<div class="well ">

	<#if (profile.isDeactivated())>This Account is no longer active
	<#else>
    <div class="row-fluid">
        <div class="span6">
            <dl class="dl-horizontal">
                <dt>ORCID</dt>
                <dd>${(profile.orcid.value)!}</dd>
                <#if ((profile.orcidBio.personalDetails.givenNames.content)?has_content)>
                    <dt>Given names</dt>
                    <dd>${(profile.orcidBio.personalDetails.givenNames.content)!}</dd>
                </#if>
            <#if ((profile.orcidBio.personalDetails.familyName.content)?has_content)>
                <dt>Family name</dt>
                <dd>${(profile.orcidBio.personalDetails.familyName.content)!}</dd>
            </#if>
            <#if ((profile.orcidBio.personalDetails.creditName.content)?has_content)>
                <dt>Credit name</dt>
                <dd>${(profile.orcidBio.personalDetails.creditName.content)!}</dd>
            </#if>
            <#if (profile.orcidBio.personalDetails.otherNames)?? && (profile.orcidBio.personalDetails.otherNames.otherName)?size &gt; 0>
                <dt>Other names</dt>
                <#list profile.orcidBio.personalDetails.otherNames.otherName as otherName>
                    <dd>${otherName.content}</dd>
                </#list>
            </#if>
                <#if ((profile.orcidBio.contactDetails.email.value)??)>
                <dt>Email</dt>
                <dd><a href="mailto:${(profile.orcidBio.contactDetails.email.value)!}">${(profile.orcidBio.contactDetails.email.value)!}</a></dd>
                </#if>
                <dt>URL</dt>
                <dd><a href="<@orcid.orcidUrl profile.orcid.value/>"><@orcid.orcidUrl profile.orcid.value/></a></dd>
                <#if (profile.orcidBio.shortDescription.content)??>
                <dt>Description</dt>
                <dd>${profile.orcidBio.shortDescription.content}</dd>
                </#if>
                <#if (profile.orcidBio.personalDetails.researcherUrls.url)?? && (profile.orcidBio.personalDetails.researcherUrls.url)?size &gt; 0>
                <dt>My URLs</dt>
                <dd>
                    <#list profile.orcidBio.personalDetails.researcherUrls.url as url>
                        <a href="${url.value}">${url.value}</a><#if url_has_next><br /></#if>
                    </#list>
                </dd>
                </#if>
                 <#if ((profile.orcidBio.keywords.keyword)?? && profile.orcidBio.keywords.keyword?size &gt; 0 )>
                <dt>Keywords</dt>
                <dd>
                 <#list profile.orcidBio.keywords.keyword as keyword>
                        ${keyword.content}<#if keyword_has_next><br /></#if>
                    </#list>
                </dd>
                </#if>
                 <#if ((profile.orcidBio.subjects.subject)?? && profile.orcidBio.subjects.subject?size &gt; 0)>
                <dt>Subjects</dt>
                <dd>
                 <#list profile.orcidBio.subjects.subject as subject>
                        ${subject.content}<#if subject_has_next><br /></#if>
                    </#list>
                </dd>
                </#if>               
            </dl>
        </div>
        <div class="span6">
            <#if (profile.orcidBio.affiliations)?? && profile.orcidBio.affiliations?size &gt; 0>
                <h5>Affiliations</h5>
                <#list profile.orcidBio.affiliations as affiliation>
                    <span>${(affiliation.affiliationName)!}<#if affiliation.departmentName??>, ${affiliation.departmentName}</#if></span><span> (${(affiliation.affiliationType?replace('_', ' ')?lower_case?cap_first)!})</span><span><#if affiliation.startDate??> from ${affiliation.startDate.value.toGregorianCalendar().time?date}</#if><#if affiliation.endDate??> to ${affiliation.endDate.value.toGregorianCalendar().time?date}</#if><#if affiliation.roleTitle??>, as ${affiliation.roleTitle}</#if></span>
                    <#if affiliation_has_next><br/></#if>
                </#list>
            </#if>
        </div>
        <div class="span6 small-top-margin">
            <#if (profile.orcidBio.externalIdentifiers)?? && profile.orcidBio.externalIdentifiers.externalIdentifier?size &gt; 0>
                <h5>External Identifiers</h5>
                <#list profile.orcidBio.externalIdentifiers.externalIdentifier as externalIdentifier>
                    ${(externalIdentifier.externalIdCommonName.content)!'Source unknown'}:
                    <#if externalIdentifier.externalIdUrl??>
                        <a href="${externalIdentifier.externalIdUrl.value}">${externalIdentifier.externalIdReference.content}</a>
                    <#else>
                        ${externalIdentifier.externalIdReference.content}
                    </#if>
                    <#if externalIdentifier_has_next><br/></#if>
                </#list>
            </#if>
        </div>
    </div>
</div>
</#if>