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
<#include "includes/affiliate/del_affiliate_inc.ftl"/>

<#include "includes/affiliate/add_affiliate_inc.ftl"/>

<@orcid.checkFeatureStatus 'DISPLAY_NEW_AFFILIATION_TYPES'> 
    <affiliation-ng2 publicView="false"></affiliation-ng2>
</@orcid.checkFeatureStatus>

<@orcid.checkFeatureStatus 'DISPLAY_NEW_AFFILIATION_TYPES' false> 
    <div ng-controller="AffiliationCtrl">
        <!-- Education -->
        <div id="workspace-education" class="workspace-accordion-item workspace-accordion-active" >
            <#include "includes/affiliate/edu_section_header_inc.ftl" />
            <div ng-if="workspaceSrvc.displayEducation" class="workspace-accordion-content">
                <#include "includes/affiliate/edu_body_inc.ftl" />
            </div>
        </div>
        <!-- Employment -->
        <div id="workspace-employment" class="workspace-accordion-item workspace-accordion-active" >
            <#include "includes/affiliate/emp_section_header_inc.ftl" />
            <div ng-if="workspaceSrvc.displayEmployment" class="workspace-accordion-content">
                <#include "includes/affiliate/emp_body_inc.ftl" />
            </div>
        </div>
    </div>
</@orcid.checkFeatureStatus>