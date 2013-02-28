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
<div class="row">
    <div class="span12">
        <div class="pull-left">
            <#if isPreview??>
                <h3>ORCID Record for ${(profile.orcidBio.personalDetails.creditName.content)!}</h3>
                <div class="alert alert-block">
                    <h4 class="alert-heading">Preview!</h4>
                    <p>This is what visitors will see when they view your ORCID Record page based on your privacy settings. To modify those settings, change the 'public' options in the  <a href="<@spring.url '/account'/>">Manage ORCID Record</a> page.</p>
                </div>
            </#if>
        </div>
        <div class="pull-right">
        </div>
    </div>
</div>
<div class="row">
    <div class="span12 top-margin">
       <#include "workspace_bio.ftl"/>
    </div>
</div>
