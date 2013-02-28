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
<#if (profile.orcidActivities.orcidGrants)??>
    <ul class="workspace-grants workspace-body-list">
        <#list profile.orcidActivities.orcidGrants.orcidGrant as grant>
            <#if grant.visibility! != 'public' >
                <#assign visibilityClass="protected"/>
            <#else>
                <#assign visibilityClass=""/>
            </#if>
            <@orcid.grantDetails grant/>
        </#list>
    </ul>
<#else>
    <div class="alert alert-info">
        <strong><#if (publicProfile)?? && publicProfile == true>No grants added yet<#else>You haven't added any grants</#if></strong>
    </div>
</#if>