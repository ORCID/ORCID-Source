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
        <@orcid.checkFeatureStatus 'ANGULAR2_QA'> 
        <works-ng2></works-ng2>
        </@orcid.checkFeatureStatus>         
        <div id="workspace-publications" class="workspace-accordion-item workspace-accordion-active" ng-controller="WorkCtrl" orcid-loaded="{{worksSrvc.loading != true}}">
          <#include "includes/work/work_section_header_inc_v3.ftl"/>
          <!-- Work Import Wizard -->
          <div ng-if="workImportWizard" class="work-import-wizard" ng-cloak>
            <div class="ie7fix-inner">
              <div class="row"> 
                <div class="col-md-12 col-sm-12 col-xs-12">
                  <h1 class="lightbox-title wizard-header"><@orcid.msg 'workspace.link_works'/></h1>
                  <span ng-click="showWorkImportWizard()" class="close-wizard"><@orcid.msg 'workspace.LinkResearchActivities.hide_link_works'/></span>
                </div>
              </div>
              <div class="row">
                <div class="col-md-12 col-sm-12 col-xs-12">
                  <p class="wizard-content">
                    <@orcid.msg 'workspace.LinkResearchActivities.description'/> <a href="https://support.orcid.org/knowledgebase/articles/188278-import-works-website-user" target="more_information"><@orcid.msg 'workspace.LinkResearchActivities.description.more_info'/></a>
                  </p>
                </div>
              </div>
              <div class="row">
                <div id="workFilters">
                  <form class="form-inline">
                    <div class="col-md-5 col-sm-5 col-xs-12">
                      <div class="form-group">
                        <label for="work-type"><@orcid.msg 'workspace.link_works.filter.worktype'/></label>   
                        <select id="work-type" ng-options="wt as wt for wt in workType | orderBy: 'toString()'" ng-model="selectedWorkType"></select>                    
                      </div> 
                    </div>
                    <div class="col-md-7 col-sm-7 col-xs-12">
                      <div class="form-group geo-area-group">
                        <label for="geo-area"><@orcid.msg 'workspace.link_works.filter.geographicalarea'/></label>  
                        <select ng-options="ga as ga for ga in geoArea | orderBy: 'toString()'" ng-model="selectedGeoArea"></select>                      
                      </div>
                    </div>  
                  </form>
                  <hr />
                </div>
              </div>         
              <div class="row wizards">               
                <div class="col-md-12 col-sm-12 col-xs-12">
                  <div ng-repeat="wtw in workImportWizardsOriginal | orderBy: 'name' | filterImportWizards : selectedWorkType : selectedGeoArea">
                    <strong><a ng-click="openImportWizardUrlFilter('<@orcid.rootPath '/oauth/authorize'/>', wtw)">{{wtw.name}}</a></strong><br />                                                                                    
                    <div class="justify">                       
                      <p class="wizard-description" ng-class="{'ellipsis-on' : wizardDescExpanded[wtw.id] == false || wizardDescExpanded[wtw.id] == null}">
                        {{wtw.description}}
                        <a ng-click="toggleWizardDesc(wtw.id)" ng-if="wizardDescExpanded[wtw.id]"><span class="glyphicon glyphicon-chevron-right wizard-chevron"></span></a>
                      </p>                        
                      <a ng-click="toggleWizardDesc(wtw.id)" ng-if="wizardDescExpanded[wtw.id] == false || wizardDescExpanded[wtw.id] == null" class="toggle-wizard-desc"><span class="glyphicon glyphicon-chevron-down wizard-chevron"></span></a>
                    </div>
                    <hr/>
                  </div>
                </div>
              </div>
            </div>            
          </div>
          <!-- Bulk Edit -->          
          <div ng-if="bulkEditShow && workspaceSrvc.displayWorks" ng-cloak>           
            <div class="bulk-edit">
              <div class="row">
                <div class="col-md-7 col-sm-7 col-xs-12">
                  <h4><@orcid.msg 'workspace.bulkedit.title'/></h4><span class="hide-bulk" ng-click="toggleBulkEdit()"><@orcid.msg 'workspace.bulkedit.hide'/></span>
                  <ol>
                    <li><@orcid.msg 'workspace.bulkedit.selectWorks'/></li>
                    <li><@orcid.msg 'workspace.bulkedit.selectAction'/></li>
                  </ol>
                </div>
                <div class="col-md-5 col-sm-5 col-xs-12">
                  <ul class="bulk-edit-toolbar">

                    <li class="bulk-edit-toolbar-item work-multiple-selector"><!-- Select all -->
                      <label><@orcid.msg 'workspace.bulkedit.select'/></label>
                      <div id="custom-control-x">
                        <div class="custom-control-x" > 
                          <div class="dropdown-custom-menu" id="dropdown-custom-menu" ng-click="toggleSelectMenu();$event.stopPropagation()">                   
                            <span class="custom-checkbox-parent">
                              <div class="custom-checkbox" id="custom-checkbox" ng-click="swapbulkChangeAll();$event.stopPropagation();" ng-class="{'custom-checkbox-active':bulkChecked}"></div>
                            </span>                   
                            <div class="custom-control-arrow" ng-click="toggleSelectMenu(); $event.stopPropagation()"></div>                            
                          </div>
                          <div>
                            <ul class="dropdown-menu" role="menu" id="special-menu" ng-class="{'block': bulkDisplayToggle}">
                              <li><a ng-click="bulkChangeAll(true)"><@orcid.msg 'workspace.bulkedit.selected.all'/></a></li>
                              <li><a ng-click="bulkChangeAll(false)"><@orcid.msg 'workspace.bulkedit.selected.none'/></a></li>                                                
                            </ul>     
                          </div>
                        </div>
                      </div>
                    </li>
                    <li class="bulk-edit-toolbar-item"><!-- Privacy control -->
                      <label><@orcid.msg 'workspace.bulkedit.edit'/></label>
                      <div class="bulk-edit-privacy-control">
                        <@orcid.privacyToggle2 angularModel="groupPrivacy()" 
                        questionClick=""
                        clickedClassCheck=""
                        publicClick="setBulkGroupPrivacy('PUBLIC', $event)" 
                        limitedClick="setBulkGroupPrivacy('LIMITED', $event)" 
                        privateClick="setBulkGroupPrivacy('PRIVATE', $event)"/>
                      </div>                      
                    </li>                   
                  </ul>
                  <div class="bulk-edit-delete">
                    <div class="centered">
                      <a ng-click="deleteBulkConfirm()" class="ignore toolbar-button edit-item-button" ng-mouseenter="showTooltip('Bulk-Edit')" ng-mouseleave="hideTooltip('Bulk-Edit')">
                        <span class="edit-option-toolbar glyphicon glyphicon-trash"></span>
                      </a>
                      <div class="popover popover-tooltip top bulk-edit-popover" ng-if="showElement['Bulk-Edit']">
                        <div class="arrow"></div>
                        <div class="popover-content">
                          <span><@orcid.msg 'workspace.bulkedit.delete'/></span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>              
              </div>              
            </div>
          </div>

          <!-- BibTeX Export Layout -->         
          <div ng-if="showBibtexExport && workspaceSrvc.displayWorks" ng-cloak class="bibtex-box">
            <div class=box-border" ng-if="canReadFiles" ng-cloak>
              <h4><@orcid.msg 'workspace.bibtexExporter.export_bibtex'/></h4><span ng-click="toggleBibtexExport()" class="hide-importer"><@orcid.msg 'workspace.bibtexExporter.hide'/></span>
              <div class="row full-height-row">
                <div class="col-md-9 col-sm-9 col-xs-8">
                  <p>
                    <@orcid.msg 'workspace.bibtexExporter.intro_1'/><a href="https://support.orcid.org/knowledgebase/articles/1807552-exporting-works-into-a-bibtex-file" target="exporting_bibtex" style="word-break\: normal;"><@orcid.msg 'workspace.bibtexExporter.intro_2'/></a><@orcid.msg 'workspace.bibtexExporter.intro_3'/>
                  </p> 
                </div>
                <div class="col-md-3 col-sm-3 col-xs-4">
                  <span class="bibtext-options">                                        
                    <a class="bibtex-cancel" ng-click="toggleBibtexExport()"><@orcid.msg 'workspace.bibtexExporter.cancel'/></a>             
                    <span ng-hide="worksFromBibtex.length > 0" class="import-label" ng-click="fetchBibtexExport()"><@orcid.msg 'workspace.bibtexExporter.export'/></span>                   
                  </span>                   
                </div>
              </div>
            </div>
            <div class="bottomBuffer" ng-if="bibtexLoading && !bibtexExportError" ng-cloak>
              <span class="dotted-bar"></span>
              <ul class="inline-list">
                <li>
                  <@orcid.msg 'workspace.bibtexExporter.generating'/>
                </li>
                <li>
                  &nbsp;<span><i id="" class="glyphicon glyphicon-refresh spin x1 green"></i></span>    
                </li>
              </ul>
            </div>
            <div class="alert alert-block" ng-if="bibtexExportError">
              <strong><@orcid.msg 'workspace.bibtexExporter.error'/></strong>
            </div>
          </div>    

          <!-- Bibtex Importer Wizard -->
          <div ng-if="showBibtexImportWizard && workspaceSrvc.displayWorks" ng-cloak class="bibtex-box">
            <div class=box-border" ng-if="canReadFiles" ng-cloak>
              <h4><@orcid.msg 'workspace.bibtexImporter.link_bibtex'/></h4><span ng-click="openBibTextWizard()" class="hide-importer"><@orcid.msg 'workspace.bibtexImporter.hide_link_bibtex'/></span>
              <div class="row full-height-row">
                <div class="col-md-9 col-sm-9 col-xs-8">
                  <p>
                  <@orcid.msg 'workspace.bibtexImporter.instructions'/>  <a href="${knowledgeBaseUri}/articles/390530#2import" target="workspace.bibtexImporter.learnMore"><@orcid.msg 'workspace.bibtexImporter.learnMore'/></a>.
                  </p> 
                </div>
                <div class="col-md-3 col-sm-3 col-xs-4">
                  <span class="bibtext-options">                                        
                    <a class="bibtex-cancel" ng-click="openBibTextWizard()"><@orcid.msg 'workspace.bibtexImporter.cancel'/></a>            
                    <span ng-hide="worksFromBibtex.length > 0" class="import-label" ng-click="openFileDialog()"><@orcid.msg 'workspace.bibtexImporter.fileUpload'/></span>
                    <span ng-if="worksFromBibtex.length > 0" class="import-label" ng-click="saveAllFromBibtex()"><@orcid.msg 'workspace.bibtexImporter.save_all'/></span>                                              
                    <input id="inputBibtex" type="file" class="upload-button" ng-model="textFiles" accept="*" update-fn="loadBibtexJs()"  app-file-text-reader multiple />
                  </span>                   
                </div>
              </div>
            </div>            
            <div class="alert alert-block" ng-if="bibtexParsingError">
              <strong><@orcid.msg 'workspace.bibtexImporter.parsingError'/></strong>
            </div>
            <span class="dotted-bar" ng-if="worksFromBibtex.length > 0"></span>

            <!-- Bibtex Import Results List -->
            <div ng-repeat="work in worksFromBibtex" ng-cloak class="bottomBuffer">             
              <div class="row full-height-row">   
                <div class="col-md-9 col-sm-9 col-xs-7">
                  <h3 class="workspace-title" ng-class="work.title.value == null ? 'bibtex-content-missing' :  ''">
                    <span ng-if="work.title.value != null">{{work.title.value}}</span>
                    <span ng-if="work.title.value == null">&lt;<@orcid.msg 'workspace.bibtexImporter.work.title_missing' />&gt;</span>
                    <span class="journaltitle" ng-if="work.journalTitle.value" ng-bind="work.journalTitle.value"></span>
                  </h3>

                  <div class="info-detail">
                    <span ng-if="work.publicationDate.year" ng-bind="work.publicationDate.year"></span><span ng-if="work.publicationDate.month">-{{work.publicationDate.month}}</span><span ng-if="work.publicationDate.day">-</span><span ng-if="work.publicationDate.day" ng-bind="work.publicationDate.day"></span><span ng-if="work.publicationDate.year"> | </span>
                    <!-- Work Category --> 
                    <!--
                    <span class="capitalize" ng-if="work.workCategory.value.length > 0">{{work.workCategory.value}}</span>
                    <span class="bibtex-content-missing small-missing-info" ng-if="work.workCategory.value.length == 0">&lt;<@orcid.msg 'workspace.bibtexImporter.work.category_missing' />&gt;</span>
                    -->

                    <!-- Work Type -->
                    <span class="capitalize" ng-if="work.workType.value.length > 0">{{work.workType.value}}</span>
                    <span class="bibtex-content-missing small-missing-info" ng-if="work.workType.value.length == 0">&lt;<@orcid.msg 'workspace.bibtexImporter.work.type_missing' />&gt;</span>

                    <!-- Active Row Identifiers / URL / Validations / Versions -->
                    
                  </div>
                  <div class="row" ng-if="group.activePutCode == work.putCode.value">
                    <div class="col-md-12 col-sm-12 bottomBuffer">
                      <ul class="id-details">
                        <li class="url-work">
                          <ul class="id-details">
                            <li ng-repeat='ie in work.workExternalIdentifiers | orderBy:["-relationship.value", "externalIdentifierType.value"]' class="url-popover">
                         
                              <span ng-if="work.workExternalIdentifiers[0].externalIdentifierId.value.length > 0" bind-html-compile='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length:moreInfo[group.groupId]'></span>
                            </li>
                          </ul>                                   
                        </li>

                        <li ng-if="work.url.value" class="url-popover url-work">
                          <@orcid.msg 'common.url' />: <a href="{{work.url.value | urlProtocol}}" ng-mouseenter="showURLPopOver(work.putCode.value)" ng-mouseleave="hideURLPopOver(work.putCode.value)" ng-class="{'truncate-anchor' : moreInfo[group.groupId] == false || moreInfo[group.groupId] == undefined}" target="work.url.value">{{work.url.value}}</a>
                          <div class="popover-pos">                                   
                            <div class="popover-help-container">
                              <div class="popover bottom" ng-class="{'block' : displayURLPopOver[work.putCode.value] == true}">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                  <a href="{{work.url.value}}" target="work.url.value">{{work.url.value}}</a>
                                </div>                
                              </div>                              
                            </div>
                          </div>
                        </li>
                      </ul>
                    </div>
                   </div>
                </div>                          
                <div class="col-md-3 col-sm-3 col-xs-3 bibtex-options-menu">                            
                  <ul>
                    <li><a ng-click="rmWorkFromBibtex(work)" class="ignore glyphicon glyphicon-trash bibtex-button" title="Ignore"></a></li>
                    <li><a ng-if="work.errors.length == 0" ng-click="addWorkFromBibtex(work)" class="save glyphicon glyphicon-floppy-disk bibtex-button" title="Save"></a></li>
                    <li><a ng-if="work.errors.length > 0" ng-click="editWorkFromBibtex(work)" class="save glyphicon glyphicon-pencil bibtex-button" title="Edit"></a></li>
                    <li><span ng-if="work.errors.length > 0"><a ng-click="editWorkFromBibtex(work)"><i class="glyphicon glyphicon-exclamation-sign"></i><@orcid.msg 'workspace.bibtexImporter.work.warning' /></a></span></li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
          <div ng-if="workspaceSrvc.displayWorks" class="workspace-accordion-content">
            <#include "includes/work/add_work_modal_inc.ftl"/>
            <#include "includes/work/del_work_modal_inc.ftl"/>
            <#include "includes/work/body_work_inc_v3.ftl"/>            
          </div>
        </div>

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

<script type="text/ng-template" id="bulk-delete-modal">
  <div class="lightbox-container">
    <div class="bulk-delete-modal">     
      <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
          <h3><@orcid.msg 'groups.bulk_delete.confirm.header'/></h3>
          <div class="orcid-error">
            <p>
              <@orcid.msg 'groups.bulk_delete.confirm.line_1'/>
            </p>
            <p>
              <@orcid.msg 'groups.bulk_delete.confirm.line_2'/>
            </p>
            <p ng-class="{'red-error':bulkDeleteSubmit == true}">
              <@orcid.msg 'groups.bulk_delete.confirm.line_3'/> <input ng-class="{'red-border-error':bulkDeleteSubmit == true}" type="text" size="3" ng-init="delCountVerify=0" ng-model="delCountVerify"/>
            </p>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12"> 
          <div class="right">     
            <button class="btn btn-danger" ng-click="bulkDeleteFunction()"><@orcid.msg 'freemarker.btnDelete'/></button>&nbsp;&nbsp;
            <a ng-click="closeModal()">
              <@orcid.msg 'freemarker.btncancel'/>
            </a>  
          </div>        
        </div>
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

<@orcid.checkFeatureStatus 'ANGULAR2_QA'> 
<#include "/includes/ng2_templates/works-form-ng2-template.ftl">
<#include "/includes/ng2_templates/works-ng2-template.ftl">
<modalngcomponent elementHeight="645" elementId="modalWorksForm" elementWidth="700">
    <works-form-ng2></works-form-ng2>
</modalngcomponent><!-- Ng2 component -->
</@orcid.checkFeatureStatus> 

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
<modalngcomponent elementHeight="645" elementId="modalFundingForm" elementWidth="700">
  <funding-form-ng2></funding-form-ng2>
</modalngcomponent>

<!-- Ng1 directive -->
<modal-email-un-verified></modal-email-un-verified>

<#include "/includes/record/record_modals.ftl">

</@protected>  