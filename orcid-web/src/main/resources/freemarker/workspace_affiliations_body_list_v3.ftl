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