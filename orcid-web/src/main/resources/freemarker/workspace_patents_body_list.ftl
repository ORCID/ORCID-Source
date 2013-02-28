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
<#if (profile.orcidActivities.orcidPatents)??>
    <ul class="workspace-body-list worskspace-patents">
        <#list profile.orcidActivities.orcidPatents.orcidPatent as patent>
            <#if patent.visibility! != 'public' >
                <#assign visibilityClass="protected"/>
            <#else>
                <#assign visibilityClass=""/>
            </#if>
            <tr class="${visibilityClass!}">
                <@orcid.patentDetails patent/>
            </tr>
        </#list>
    </ul>
<#else>
    <div class="alert alert-info">
        <strong><#if (publicProfile)?? && publicProfile == true>No patents added yet<#else>You haven't added any patents</#if></strong>
    </div>
</#if>