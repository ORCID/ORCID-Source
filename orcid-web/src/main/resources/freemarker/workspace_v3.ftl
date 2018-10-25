<@protected nav="record">
<#escape x as x?html>
<#include "/includes/ng2_templates/my-orcid-alerts-ng2-template.ftl">
<my-orcid-alerts-ng2 checkEmailValidated=${(Session.CHECK_EMAIL_VALIDATED?exists?c)!} inDelegationMode=${(inDelegationMode?c)!}></my-orcid-alerts-ng2>
<div class="row workspace-top public-profile">
  <!--Left col-->
  <div class="col-md-3 lhs left-aside">
    <div class="workspace-profile">
      <!-- ID Banner-->
      <#include "includes/id_banner.ftl"/>
      <!--Public record widget-->
      <#include "/includes/ng2_templates/widget-ng2-template.ftl">
      <widget-ng2></widget-ng2>
      <!--Print record-->
      <#include "/includes/ng2_templates/print-record-ng2-template.ftl">
      <print-record-ng2></print-record-ng2>
      <div class="qrcode-container">
          <a href="<@orcid.rootPath "/qr-code" />" target="<@orcid.msg 'workspace.qrcode.link.text'/>"><span class="glyphicons qrcode orcid-qr"></span><@orcid.msg 'workspace.qrcode.link.text'/>
          <div class="popover-help-container"></a>
              <i class="glyphicon glyphicon-question-sign"></i>
              <div id="qrcode-help" class="popover bottom">
                  <div class="arrow"></div>
                  <div class="popover-content">
                      <p><@orcid.msg 'workspace.qrcode.help'/> 
                          <a href="<@orcid.msg 'common.kb_uri_default'/>360006897654" target="qrcode.help"><@orcid.msg 'common.learn_more'/></a>
                      </p>
                  </div>
              </div>
          </div>
      </div>
      <!-- Person -->
      <#include "/includes/ng2_templates/person-ng2-template.ftl">
      <person-ng2></person-ng2> 
      <!-- Emails  -->
      <#include "/includes/ng2_templates/emails-ng2-template.ftl">
      <emails-ng2></emails-ng2>    
    </div>
  </div>
  <!--Right col-->
  <div class="col-md-9 right-aside">
    <div class="workspace-right">        
      <!-- Locked error message -->
      <#if (locked)?? && locked>
      <div class="workspace-inner workspace-header">
        <div class="alert alert-error readme" ng-cloak>
          <strong><@orcid.msg 'workspace.locked.header'/></strong>
          <p><@orcid.msg 'workspace.locked.message_1'/><a href="http://orcid.org/help/contact-us" target="Orcid_support"><@orcid.msg 'workspace.locked.message_2'/></a><@orcid.msg 'workspace.locked.message_3'/></p>
        </div>
      </div>                
      </#if>
      <div class="workspace-accordion" id="workspace-accordion">
        <!-- Notification alert -->                       
        <#include "/includes/ng2_templates/notification-alerts-ng2-template.ftl">
        <notification-alerts-ng2></notification-alerts-ng2>           
        <!-- Biography -->        
        <div id="workspace-personal" class="workspace-accordion-item workspace-accordion-active">  
          <div class="workspace-accordion-content">
            <#include "/includes/ng2_templates/biography-ng2-template.ftl">
            <biography-ng2></biography-ng2>
          </div>
        </div>    
        <!-- Affiliations / Education / Employment -->
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
        <!-- Funding -->
        <#include "/includes/ng2_templates/funding-ng2-template.ftl">
        <funding-ng2></funding-ng2>
        <!-- Research resources -->
        <@orcid.checkFeatureStatus 'RESEARCH_RESOURCE'>
          <!--Research resources-->
          <#include "/includes/ng2_templates/research-resource-ng2-template.ftl">
          <research-resource-ng2 publicView="false"></research-resource-ng2>
        </@orcid.checkFeatureStatus>
        <!-- Works -->
        <#include "/includes/ng2_templates/works-ng2-template.ftl">
        <works-ng2></works-ng2>
        <!--Peer review-->
        <#include "/includes/ng2_templates/peer-review-ng2-template.ftl">
        <peer-review-ng2 publicView="false"></peer-review-ng2>
      </div>
    </div>
  </div>    
</div>
</#escape>

<#include "/includes/ng2_templates/email-verification-sent-messsage-ng2-template.ftl">
<modalngcomponent elementHeight="248" elementId="emailSentConfirmation" elementWidth="500">
    <email-verification-sent-messsage-ng2></email-verification-sent-messsage-ng2>
</modalngcomponent><!-- Ng2 component --> 

<#include "/includes/ng2_templates/works-merge-choose-preferred-version-ng2-template.ftl">
<modalngcomponent elementHeight="280" elementId="modalWorksMergeChoosePreferredVersion" elementWidth="600">
    <works-merge-choose-preferred-version-ng2></works-merge-choose-preferred-version-ng2>
</modalngcomponent><!-- Ng2 component -->

<#include "/includes/ng2_templates/works-merge-suggestions-ng2-template.ftl">
<modalngcomponent elementHeight="320" elementId="modalWorksMergeSuggestions" elementWidth="600">
    <works-merge-suggestions-ng2></works-merge-suggestions-ng2>
</modalngcomponent><!-- Ng2 component -->

<#include "/includes/ng2_templates/works-delete-ng2-template.ftl">
<modalngcomponent elementHeight="160" elementId="modalWorksDelete" elementWidth="300">
    <works-delete-ng2></works-delete-ng2> 
</modalngcomponent><!-- Ng2 component -->


<modalngcomponent elementHeight="160" elementId="modalAffiliationDelete" elementWidth="300">
    <affiliation-delete-ng2></affiliation-delete-ng2>
</modalngcomponent><!-- Ng2 component -->

<modalngcomponent elementHeight="645" elementId="modalAffiliationForm" elementWidth="700">
    <affiliation-form-ng2></affiliation-form-ng2>
</modalngcomponent><!-- Ng2 component -->

<#include "/includes/ng2_templates/emails-form-ng2-template.ftl">
<modalngcomponent elementHeight="650" elementId="modalEmails" elementWidth="700">
    <emails-form-ng2 popUp="true"></emails-form-ng2>
</modalngcomponent><!-- Ng2 component -->

<#include "/includes/ng2_templates/email-unverified-warning-ng2-template.ftl">
<modalngcomponent elementHeight="280" elementId="modalemailunverified" elementWidth="500">
    <email-unverified-warning-ng2></email-unverified-warning-ng2>
</modalngcomponent><!-- Ng2 component --> 

<#include "/includes/ng2_templates/funding-delete-ng2-template.ftl">
<modalngcomponent elementHeight="160" elementId="modalFundingDelete" elementWidth="300">
    <funding-delete-ng2></funding-delete-ng2>
</modalngcomponent><!-- Ng2 component -->

<#include "/includes/ng2_templates/funding-form-ng2-template.ftl">
<modalngcomponent elementHeight="700" elementId="modalFundingForm" elementWidth="800">
  <funding-form-ng2></funding-form-ng2>
</modalngcomponent>

<#include "/includes/ng2_templates/peer-review-delete-ng2-template.ftl">
<modalngcomponent elementHeight="160" elementId="modalPeerReviewDelete" elementWidth="300">
    <peer-review-delete-ng2></peer-review-delete-ng2>
</modalngcomponent><!-- Ng2 component -->

<#include "/includes/ng2_templates/research-resource-delete-ng2-template.ftl">
<modalngcomponent elementHeight="160" elementId="modalResearchResourceDelete" elementWidth="300">
    <research-resource-delete-ng2></research-resource-delete-ng2>
</modalngcomponent><!-- Ng2 component -->

<#include "/includes/ng2_templates/works-bulk-delete-ng2-template.ftl">
<modalngcomponent elementHeight="280" elementId="modalWorksBulkDelete" elementWidth="600">
    <works-bulk-delete-ng2></works-bulk-delete-ng2>
</modalngcomponent><!-- Ng2 component -->

<#include "/includes/ng2_templates/works-delete-ng2-template.ftl">
<modalngcomponent elementHeight="160" elementId="modalWorksDelete" elementWidth="300">
    <works-delete-ng2></works-delete-ng2>
</modalngcomponent><!-- Ng2 component -->

<#include "/includes/ng2_templates/works-form-ng2-template.ftl">
<modalngcomponent elementHeight="645" elementId="modalWorksForm" elementWidth="820">
    <works-form-ng2></works-form-ng2>
</modalngcomponent><!-- Ng2 component -->

<!--Org ID popover template used in v3 affiliations and research resources-->
<#include "/includes/ng2_templates/org-identifier-popover-ng2-template.ftl">

</@protected>  