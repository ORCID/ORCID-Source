<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#include "includes/funding/del_funding_inc.ftl"/>

<#include "includes/funding/add_funding_inc.ftl"/>

<div ng-controller="FundingCtrl">
    <!-- Funding -->
    <div id="workspace-fundings" class="workspace-accordion-item workspace-accordion-active" >
        <#include "includes/funding/funding_section_header_inc_v3.ftl" />
        
        <div ng-show="workspaceSrvc.displayFunding" class="workspace-accordion-content">
            <#include "includes/funding/body_funding_inc_v3.ftl" />
        </div>
        </div><!-- this div seems out of place -->
    </div>
</div>