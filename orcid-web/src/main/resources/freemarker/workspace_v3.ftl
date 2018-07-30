<@protected nav="record">
<#escape x as x?html>
<#if justRegistered?? && justRegistered>
<div class="alert alert-success">
    <strong>
      <#include "/includes/ng2_templates/thanks-for-registering-ng2-template.ftl">
      <thanks-for-registering-ng2></thanks-for-registering-ng2>
    </strong>
</div>
</#if>

<#if emailVerified?? && emailVerified>
<div class="alert alert-success">
    <strong>
        <@spring.message "orcid.frontend.web.email_verified"/>
        <#if primaryEmailUnverified?? && primaryEmailUnverified>
        <#include "/includes/ng2_templates/thanks-for-verifying-ng2-template.ftl">
        <thanks-for-verifying-ng2></thanks-for-verifying-ng2>
        </#if>
    </strong>
</div>
</#if>

<#if invalidVerifyUrl?? && invalidVerifyUrl>
<div class="alert alert-success">
    <strong><@spring.message "orcid.frontend.web.invalid_verify_link"/></strong>
</div>
</#if>


<#if invalidOrcid?? && invalidOrcid>
<div class="alert alert-success">
    <strong><@spring.message "orcid.frontend.web.invalid_switch_orcid"/></strong>
</div>
</#if>

<div class="row workspace-top public-profile">

    <#-- hidden divs that trigger angular -->
    <#if RequestParameters['recordClaimed']??>
    <@orcid.checkFeatureStatus 'ANGULAR2_QA'>
    <#include "/includes/ng2_templates/claim-thanks-ng2-template.ftl">
    <claim-thanks-ng2></claim-thanks-ng2>
    </@orcid.checkFeatureStatus>
    <@orcid.checkFeatureStatus featureName='ANGULAR1_LEGACY' enabled=false>  
    <div ng-controller="ClaimThanks" style="display: hidden;"></div>
    </@orcid.checkFeatureStatus> 
    <#elseif !Session.CHECK_EMAIL_VALIDATED?exists && !inDelegationMode>
    <@orcid.checkFeatureStatus 'ANGULAR2_QA'>
    <verify-email-ng2></verify-email-ng2>
    </@orcid.checkFeatureStatus>
    <@orcid.checkFeatureStatus featureName='ANGULAR1_LEGACY' enabled=false>  
    <div ng-controller="VerifyEmailCtrl" style="display: hidden;" orcid-loading="{{loading}}"></div>
    </@orcid.checkFeatureStatus> 
    </#if>
    <!-- ID Banner and other account information -->
    <div class="col-md-3 lhs left-aside">
        <div class="workspace-profile">

            <#include "includes/id_banner.ftl"/>

            <#include "includes/orcid_public_record_widget.ftl"/>

            <#include "includes/print_record.ftl"/>

            <div class="qrcode-container">
                <a href="<@orcid.rootPath "/qr-code" />" target="<@orcid.msg 'workspace.qrcode.link.text'/>"><span class="glyphicons qrcode orcid-qr"></span><@orcid.msg 'workspace.qrcode.link.text'/>
                    <div class="popover-help-container">
                        <i class="glyphicon glyphicon-question-sign"></i>
                        <div id="qrcode-help" class="popover bottom">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <p><@orcid.msg 'workspace.qrcode.help'/><a href="https://support.orcid.org/knowledgebase/articles/116878"><@orcid.msg 'common.learn_more'/></a></p>
                            </div>
                        </div>
                    </div>
                </a>
            </div>

            <!-- Person -->
            <#include "/includes/ng2_templates/person-ng2-template.ftl">
            <person-ng2></person-ng2>

            <!-- Emails  -->
            <#include "/includes/ng2_templates/emails-ng2-template.ftl">
            <emails-ng2></emails-ng2>


      <!--  External Identifiers -->

      <@orcid.checkFeatureStatus 'ANGULAR2_QA'>
      <div ng-hide="!externalIdentifiersForm.externalIdentifiers.length">
        <external-identifiers-ng2></external-identifiers-ng2>
      </div>
      </@orcid.checkFeatureStatus>
      <@orcid.checkFeatureStatus featureName='ANGULAR1_LEGACY' enabled=false>   
      <div ng-controller="ExternalIdentifierCtrl" ng-hide="!externalIdentifiersForm.externalIdentifiers.length" ng-cloak  class="workspace-section">
        <div class="workspace-section-header">
          <div class="workspace-section-title">                 
            <div id="open-edit-external-identifiers" class="edit-websites edit-option" ng-click="openEditModal()">
              <div class="glyphicon glyphicon-pencil">
                <div class="popover popover-tooltip top">
                  <div class="arrow"></div>
                  <div class="popover-content">
                    <span><@orcid.msg 'manage_bio_settings.editExternalIdentifiers' /></span>
                  </div>                
                </div>
              </div>
            </div>
            <div class="workspace-section-label"><@orcid.msg 'public_profile.labelOtherIDs'/></div>
          </div>
        </div>
        <div class="workspace-section-content">
          <div ng-repeat="externalIdentifier in externalIdentifiersForm.externalIdentifiers">
            <span ng-hide="externalIdentifier.url">{{externalIdentifier.commonName}}: {{externalIdentifier.reference}}</span>
            <span ng-if="externalIdentifier.url"><a href="{{externalIdentifier.url}}" target="externalIdentifier.commonName">{{externalIdentifier.commonName}}: {{externalIdentifier.reference}}</a></span>
          </div>
        </div>
      </div>
      </@orcid.checkFeatureStatus>       
    </div>
  </div>

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

      <@orcid.checkFeatureStatus 'ANGULAR2_QA'> 
      <work-summary-ng2></work-summary-ng2>
      </@orcid.checkFeatureStatus>         
        <@orcid.checkFeatureStatus featureName='ANGULAR1_LEGACY' enabled=false>
      <div class="workspace-inner workspace-header" ng-controller="WorkspaceSummaryCtrl">
        <div class="grey-box" ng-if="showAddAlert()" ng-cloak>
          <strong><@orcid.msg 'workspace.addinformationaboutyou_1'/><a href="https://support.orcid.org/knowledgebase/articles/460004" target="get_started" style="word-break: normal;"><@orcid.msg 'workspace.addinformationaboutyou_2'/></a><@orcid.msg 'workspace.addinformationaboutyou_3'/></strong>
        </div>                
      </div>
      </@orcid.checkFeatureStatus>  
      <div class="workspace-accordion" id="workspace-accordion">
        <!-- Notification alert -->                       
        <#include "includes/notification_alert.ftl"/>             
        <!-- Personal Information -->
        <@orcid.checkFeatureStatus 'ANGULAR2_QA'> 
        <personal-info-ng2></personal-info-ng2>
        </@orcid.checkFeatureStatus>         
        <div id="workspace-personal" class="workspace-accordion-item workspace-accordion-active" ng-controller="PersonalInfoCtrl">              
          <div class="workspace-accordion-content" ng-if="displayInfo">
            <#include "workspace_personal_v3.ftl"/>
          </div>
        </div>    
        <!-- Affiliations / Education / Employment -->
        <#include "workspace_affiliations_body_list_v3.ftl"/>
        <!-- Fundings -->
        <#include "workspace_fundings_body_list_v3.ftl"/>
        
        <!-- Works -->
        <#include "/includes/ng2_templates/works-ng2-template.ftl">
        <works-ng2></works-ng2>

        <!--Peer review-->
        <div ng-controller="PeerReviewCtrl">
          <div ng-if="peerReviewSrvc.groups.length > 0" ng-cloak>
            <#include "workspace_peer_review_body_list.ftl"/>
          </div>
        </div>
      </div>
    </div>
  </div>    
</div>
</#escape>

<script type="text/ng-template" id="verify-email-modal">  
  <div class="lightbox-container"> 
    <div class="row">
      <div class="col-md-12 col-xs-12 col-sm-12" ng-if="verifiedModalEnabled">
        <!-- New -->
        <h4><@orcid.msg 'workspace.your_primary_email_new'/></h4>
        <p><@orcid.msg 'workspace.ensure_future_access1'/></p>
        <p><@orcid.msg 'workspace.ensure_future_access2'/> <strong>{{primaryEmail}}</strong></p>
        <p><@orcid.msg 'workspace.ensure_future_access3'/> <a target="workspace.ensure_future_access4" href="<@orcid.msg 'workspace.link.url.knowledgebase'/>"><@orcid.msg 'workspace.ensure_future_access4'/></a> <@orcid.msg 'workspace.ensure_future_access5'/> <a target="workspace.link.email.support" href="mailto:<@orcid.msg 'workspace.link.email.support'/>"><@orcid.msg 'workspace.link.email.support'/></a>.</p>
        <div class="topBuffer">
          <button class="btn btn-primary" id="modal-close" ng-click="verifyEmail()"><@orcid.msg 'workspace.send_verification_new'/></button>        
          <a class="cancel-option inner-row" ng-click="closeColorBox()"><@orcid.msg 'freemarker.btncancel'/></a>
        </div>
      </div>

      <div class="col-md-12 col-xs-12 col-sm-12" ng-if="!verifiedModalEnabled">
        <!-- Original -->
        <h4><@orcid.msg 'workspace.your_primary_email'/></h4>
        <p><@orcid.msg 'workspace.ensure_future_access'/></p>
        <button class="btn btn-primary" id="modal-close" ng-click="verifyEmail()"><@orcid.msg 'workspace.send_verification'/></button>        
        <a class="cancel-option inner-row" ng-click="closeColorBox()"><@orcid.msg 'freemarker.btncancel'/></a>
      </div>
    </div>
  </div>
</script>

<script type="text/ng-template" id="combine-work-template">
  <div class="lightbox-container">
    <div class="row combine-work">
      <div class="col-md-12 col-xs-12 col-sm-12">
        <h3>Selected work "{{combineWork.title.value}}"       
          <span ng-if="hasCombineableEIs(combineWork)">
            (<span ng-repeat='ie in combineWork.workExternalIdentifiers'>
              <span ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:combineWork.workExternalIdentifiers.length'></span>
            </span>)
          </span>       
        </h3>
        <p>Combine with (select one):</p>
        <ul class="list-group">
          <li class="list-group-item" ng-repeat="group in worksSrvc.groups | orderBy:sortState.predicate:sortState.reverse" ng-if="combineWork.putCode.value != group.getDefault().putCode.value && validCombineSel(combineWork,group.getDefault())">
            <strong>{{group.getDefault().title.value}}</strong>
            <a ng-click="combined(combineWork,group.getDefault())" class="btn btn-primary pull-right bottomBuffer">Combine</a>

          </li>           
        </ul>
      </div>
    </div>
    <div class="row">
      <div class="col-md-12 col-xs-12 col-sm-12">
        <button class="btn close-button pull-right" id="modal-close" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel'/></button>
      </div>
    </div>
  </div>
</script>

<script type="text/ng-template" id="verify-email-modal-sent">
  <div class="lightbox-container">
    <div class="row">
      <div class="col-md-12 col-sm-12 col-xs-12">
        <h4><@orcid.msg 'manage.email.verificationEmail'/> {{emailsPojo.emails[0].value}}</h4>
        <@orcid.msg 'workspace.check_your_email'/><br />
        <br />
        <button class="btn" ng-click="closeColorBox()"><@orcid.msg 'freemarker.btnclose'/></button>
      </div>
    </div>
  </div>
</script>

<script type="text/ng-template" id="claimed-record-thanks">
    <div class="lightbox-container">
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <strong><@spring.message "orcid.frontend.web.record_claimed"/></strong><br />
                <br />
                <button class="btn btn-primary" ng-click="close()"><@spring.message "freemarker.btnclose"/></button>
            </div>
        </div>
    </div>
</script>

<script type="text/ng-template" id="claimed-record-thanks-source-grand-read">
    <div class="lightbox-container">
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <strong><@spring.message "orcid.frontend.web.record_claimed"/></strong><br />
                <br />
                <strong ng-bind="sourceGrantReadWizard.displayName"></strong> <@spring.message "orcid.frontend.web.record_claimed.would_like"/><br />
                <br />
                <button class="btn btn-primary" ng-click="yes()"><@spring.message "orcid.frontend.web.record_claimed.yes_go_to" /></button>
                <button class="btn btn-primary" ng-click="close()"><@spring.message "orcid.frontend.web.record_claimed.no_thanks" /></button>
            </div>
        </div>
    </div>
</script>

<script type="text/ng-template" id="delete-external-id-modal">
    <div class="lightbox-container">
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <h3><@orcid.msg 'manage.deleteExternalIdentifier.pleaseConfirm'/> {{removeExternalModalText}} </h3>
                <button class="btn btn-danger" ng-click="removeExternalIdentifier()"><@orcid.msg 'freemarker.btnDelete'/></button> 
                <a ng-click="closeEditModal()"><@orcid.msg 'freemarker.btncancel'/></a>
            </div>
        </div>
    </div> 
</script>

<script type="text/ng-template" id="import-wizard-modal">
  <#if ((workImportWizards)??)>   
  <div id="third-parties">
    <div class="ie7fix-inner">
      <div class="row"> 
        <div class="col-md-12 col-sm-12 col-xs-12">         
          <a class="btn pull-right close-button" ng-click="closeModal()">X</a>
          <h1 class="lightbox-title" style="text-transform: uppercase;"><@orcid.msg 'workspace.link_works'/></h1>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
          <div class="justify">
            <p><@orcid.msg 'workspace.LinkResearchActivities.description'/></p>
          </div>                                
          <#list workImportWizards?sort_by("displayName") as thirdPartyDetails>
          <#assign redirect = (thirdPartyDetails.redirectUris.redirectUri[0].value) >
          <#assign predefScopes = (thirdPartyDetails.redirectUris.redirectUri[0].scopeAsSingleString) >
          <strong><a ng-click="openImportWizardUrl('<@orcid.rootPath '/oauth/authorize?client_id=${thirdPartyDetails.clientId}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>')">${thirdPartyDetails.displayName}</a></strong><br />
          <div class="justify">
            <p>
              ${(thirdPartyDetails.shortDescription)!}
            </p>
          </div>
          <#if (thirdPartyDetails_has_next)>
          <hr/>
          </#if>
          </#list>
        </div>
      </div>                 
      <div class="row footer">
        <div class="col-md-12 col-sm-12 col-xs-12">
          <p>
            <strong><@orcid.msg 'workspace.LinkResearchActivities.footer.title'/></strong>      
            <@orcid.msg 'workspace.LinkResearchActivities.footer.description1'/> <a href="<@orcid.msg 'workspace.LinkResearchActivities.footer.description.url'/>"><@orcid.msg 'workspace.LinkResearchActivities.footer.description.link'/></a> <@orcid.msg 'workspace.LinkResearchActivities.footer.description2'/>
          </p>
        </div>
      </div>
    </div>
  </div>
  </#if>
</script>

<script type="text/ng-template" id="import-funding-modal">
  <#if ((fundingImportWizards)??)>    
  <div id="third-parties">
    <div class="ie7fix-inner">
      <div class="row"> 
        <div class="col-md-12 col-sm-12 col-xs-12">         
          <a class="btn pull-right close-button" ng-click="closeModal()">X</a>
          <h1 class="lightbox-title" style="text-transform: uppercase;"><@orcid.msg 'workspace.link_funding'/></h1>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
          <div class="justify">
            <p><@orcid.msg 'workspace.LinkResearchActivities.description'/></p>
          </div>                                
          <#list fundingImportWizards?sort_by("name") as thirdPartyDetails>
          <#assign redirect = (thirdPartyDetails.redirectUri) >
          <#assign predefScopes = (thirdPartyDetails.scopes) >
          <strong><a ng-click="openImportWizardUrl('<@orcid.rootPath '/oauth/authorize?client_id=${thirdPartyDetails.id}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>')">${thirdPartyDetails.name}</a></strong><br />
          <div class="justify">
            <p>
              ${(thirdPartyDetails.description)!}
            </p>
          </div>
          <#if (thirdPartyDetails_has_next)>
          <hr/>
          </#if>
          </#list>
        </div>
      </div>                 
      <div class="row footer">
        <div class="col-md-12 col-sm-12 col-xs-12">
          <p>
            <strong><@orcid.msg 'workspace.LinkResearchActivities.footer.title'/></strong>      
            <@orcid.msg 'workspace.LinkResearchActivities.footer.description1'/> <a href="<@orcid.msg 'workspace.LinkResearchActivities.footer.description.url'/>"><@orcid.msg 'workspace.LinkResearchActivities.footer.description.link'/></a> <@orcid.msg 'workspace.LinkResearchActivities.footer.description2'/>
          </p>
        </div>
      </div>
    </div>
  </div>
  </#if>
</script>
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

<#include "/includes/ng2_templates/email-verification-sent-messsage-ng2-template.ftl">
<modalngcomponent elementHeight="248" elementId="emailSentConfirmation" elementWidth="500">
    <email-verification-sent-messsage-ng2></email-verification-sent-messsage-ng2>
</modalngcomponent><!-- Ng2 component --> 

<#include "/includes/ng2_templates/funding-form-ng2-template.ftl">
<modalngcomponent elementHeight="800" elementId="modalFundingForm" elementWidth="800">
  <funding-form-ng2></funding-form-ng2>
</modalngcomponent>

<!-- Ng1 directive -->
<modal-email-un-verified></modal-email-un-verified>

<#include "/includes/record/record_modals.ftl">

</@protected>  