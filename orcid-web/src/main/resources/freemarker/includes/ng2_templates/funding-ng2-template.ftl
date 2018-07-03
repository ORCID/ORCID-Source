<script type="text/ng-template" id="funding-ng2-template">
    <div>
        <!-- Funding -->
        <div id="workspace-fundings" class="workspace-accordion-item workspace-accordion-active" >
            <!--
            include "includes/funding/funding_section_header_inc_v3.ftl" 
            -->
            <div class="workspace-accordion-header clearfix">
                <div class="row">
                    
                    <div class="col-md-4 col-sm-4 col-xs-12">
                        <a href="" (click)="workspaceSrvc.toggleFunding($event)" class="toggle-text" *ngIf="groups?.length > 0">
                            <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayFunding==false}"></i>
                            <@orcid.msg 'workspace.Funding'/> (<span>{{groups.length}}</span>)
                        </a>
                        <#if !(isPublicProfile??)> 
                        <div class="popover-help-container">
                            <i class="glyphicon glyphicon-question-sign"></i>
                            <div id="funding-help" class="popover bottom">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <p><@orcid.msg 'manage_funding_settings.helpPopoverFunding'/> <a href="${knowledgeBaseUri}/articles/326033" target="manage_funding_settings.helpPopoverFunding"><@orcid.msg 'common.learn_more'/></a></p>
                                </div>
                            </div>
                        </div> 
                        </#if> 
                    </div>
                    <div class="col-md-8 col-sm-8 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayFunding">
                        <!-- Sort -->
                        <#include "../workspace/workspace_act_sort_menu.ftl"/>
                        <#if !(isPublicProfile??)>
                        <ul class="workspace-bar-menu">
                            <!-- Link Manually -->
                            <li class="hidden-xs">
                                <div class="menu-container" id="add-fundidiv">
                                    <ul class="toggle-menu">
                                        <li [ngClass]="{'green-bg' : fundingImportWizard == true}">       
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msgCapFirst 'manual_funding_form_contents.add_grant' />    
                                            <ul class="menu-options funding">
                                                <!-- Search & Link -->
                                                <#if fundingImportWizards?has_content>
                                                <li>                                                
                                                    <a class="action-option manage-button" (click)="showFundingImportWizard()">
                                                        <span class="glyphicon glyphicon-cloud-upload"></span>
                                                        <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                                    </a>
                                                </li>
                                                </#if>                      
                                                <!-- Add Manually -->
                                                <li>
                                                    <a id="add-funding" class="action-option manage-button" (click)="addFundingModal()">
                                                        <span class="glyphicon glyphicon-plus"></span>
                                                        <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                    </a>
                                                </li>
                                            </ul>
                                        </li>                               
                                    </ul>
                                </div>
                            </li>
                            <!-- Mobile Version Workaround -->
                            <!-- Search & Link -->
                            <#if fundingImportWizards?has_content>
                            <li class="hidden-md hidden-sm visible-xs-inline">
                                <a class="action-option manage-button action-funding-mobile" (click)="showFundingImportWizard()">
                                    <span class="glyphicon glyphicon-cloud-upload"></span>
                                    <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                </a>
                            </li>
                            </#if>                      
                            <!-- Add Manually -->
                            <li class="hidden-md hidden-sm visible-xs-inline">
                                <a class="action-option manage-button action-funding-mobile" (click)="addFundingModal()">
                                    <span class="glyphicon glyphicon-plus"></span>
                                    <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                </a>
                            </li>
                        </ul>
                        </#if>
                    </div>
                </div>
            </div>
            <!-- end of include -->

            <div *ngIf="fundingImportWizard" class="funding-import-wizard">
                <#if ((fundingImportWizards)??)>
                <div class="ie7fix-inner">
                    <div class="row">   
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h1 class="lightbox-title wizard-header"><@orcid.msg 'workspace.link_funding'/></h1>
                            <span (click)="showFundingImportWizard()" class="close-wizard"><@orcid.msg 'workspace.LinkResearchActivities.hide_link_fundings'/></span>
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
                            <strong><a (click)="openImportWizardUrl('<@orcid.rootPath '/oauth/authorize?client_id=${thirdPartyDetails.id}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>')">${thirdPartyDetails.name}</a></strong>
                            <br />
                            <div class="justify">
                                <p class="wizard-description" [ngClass]="{'ellipsis-on' : wizardDescExpanded[${thirdPartyDetails.id}] == false || wizardDescExpanded[${thirdPartyDetails.id}] == null}">
                                    ${(thirdPartyDetails.description)!}
                                    <a (click)="toggleWizardDesc(${thirdPartyDetails.id})" *ngIf="wizardDescExpanded[${thirdPartyDetails.id}] == true"><span class="glyphicon glyphicon-chevron-down wizard-chevron"></span></a>
                                </p>                                                
                                <a (click)="toggleWizardDesc(${thirdPartyDetails.id})" *ngIf="wizardDescExpanded[${thirdPartyDetails.id}] == false || wizardDescExpanded[${thirdPartyDetails.id}] == null" class="toggle-wizard-desc"><span class="glyphicon glyphicon-chevron-right wizard-chevron"></span></a>
                            </div>
                            <#if (thirdPartyDetails_has_next)>
                            <hr />
                            </#if>
                            </#list>
                        </div>
                    </div>  
                </div>
                </#if>
            </div>
            <div *ngIf="workspaceSrvc.displayFunding" class="workspace-accordion-content">
                <!-- 
                include "includes/funding/body_funding_inc_v3.ftl"
                -->
                <ul *ngIf="fundingSrvc?.groups?.length" class="workspace-fundings workspace-body-list bottom-margin-medium">
                    <li class="bottom-margin-small workspace-border-box card ng-scope" *ngFor="let group of fundingSrvc.groups | orderBy:sortState.predicate:sortState.reverse">
                        <div class="work-list-container">
                            <ul class="sources-edit-list">
                                <!-- Header -->
                                <li *ngIf="editSources[group.groupId] == true" class="source-header" [ngClass]="{'source-active' : editSources[group.groupId] == true}" [ngModel]="group.activities">
                                    <div class="sources-header">
                                        <div class="row">
                                            <div class="col-md-7 col-sm-7 col-xs-7">
                                                <@orcid.msg 'groups.common.sources' /> <span class="hide-sources" (click)="hideSources(group)"><@orcid.msg 'groups.common.close_sources' /></span>
                                            </div>                            
                                            <div class="col-md-2 col-sm-2 col-xs-2">                                
                                                <@orcid.msgCapFirst 'groups.common.preferred' />
                                            </div>                            
                                            <div class="col-md-3 col-sm-3 col-xs-3 right">
                                                <#if !(isPublicProfile??)>
                                                <div class="workspace-toolbar">
                                                    <ul class="workspace-private-toolbar">
                                                         <li class="works-details" *ngIf="editSources[group.groupId]">
                                                            <a (click)="showDetailsMouseClick(group.groupId,$event);" (mouseenter)="showTooltip(group.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group.groupId+'-showHideDetails')">
                                                                <span [ngClass]="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                                                </span>
                                                            </a>                   
                                                            <div class="popover popover-tooltip top show-hide-details-popover ng-hide" *ngIf="showElement[group.groupId+'-showHideDetails']">
                                                                <div class="arrow"></div>
                                                                <div class="popover-content">
                                                                    <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null" class=""><@orcid.msg 'common.details.show_details'/></span>                                    
                                                                    <span *ngIf="moreInfo[group.groupId] == true" class="ng-hide">Hide Details</span>
                                                                </div>
                                                            </div>                      
                                                        </li>
                
                                                        <li>
                                                            <@orcid.privacyToggle2  angularModel="group.getActive().visibility.visibility"
                                                                questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
                                                                clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}"
                                                                publicClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PUBLIC', $event)"
                                                                limitedClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'LIMITED', $event)"
                                                                privateClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PRIVATE', $event)"
                                                                />
                                                        </li>
                                                    </ul>
                                                </div>
                                                </#if>
                                            </div>
                                        </div>
                                    </div>
                                </li><!--  End of header -->

                                <div *ngIf="group.activePutCode == funding.putCode.value || editSources[group.groupId]">
                                    <li 
                                    *ngFor="let funding of group.activities" 
                                     
                                    ><!-- *** funding-put-code="{{funding.putCode.value}}" -->
                                        <!-- active row summary info -->
                                        <div class="row" *ngIf="group.activePutCode == funding.putCode.value">
                                            <div class="col-md-9 col-sm-9 col-xs-7">
                                                <h3 class="workspace-title">                                
                                                   <span *ngIf="group.getActive().fundingTitle.title.value">{{group.getActive().fundingTitle.title.value}}</span>                               
                                                </h3>
                                                <div class="clear-fix left">
                                                    <span *ngIf="group.getActive().fundingName.value"><span>{{group.getActive().fundingName.value}}</span></span> (<span *ngIf="group.getActive().city.value"><span>{{group.getActive().city.value}}</span></span><span *ngIf="group.getActive().region.value">, <span >{{group.getActive().region.value}}</span></span><span *ngIf="group.getActive().countryForDisplay">, <span>{{group.getActive().countryForDisplay}}</span></span>)
                                                </div>  
                                                <div class="info-detail">
                                                    <!-- Funding date -->
                                                    <span class="funding-date" *ngIf="group.getActive().startDate && !group.getActive().endDate">
                                                        <span *ngIf="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span *ngIf="group.getActive().startDate.month">-</span><span *ngIf="group.getActive().startDate.month">{{group.getActive().startDate.month}}</span>
                                                        <#-- Do not move it to two lines -->
                                                        <@orcid.msg 'workspace_fundings.dateSeparator'/> <@orcid.msg 'workspace_fundings.present'/>
                                                        <#-- ########################### -->
                                                    </span>
                                                    <span class="funding-date" *ngIf="group.getActive().startDate && group.getActive().endDate">
                                                        <span *ngIf="group.getActive().startDate.year">{{group.getActive().startDate.year}}</span><span *ngIf="group.getActive().startDate.month">-</span><span *ngIf="group.getActive().startDate.month">{{group.getActive().startDate.month}}</span>
                                                        <@orcid.msg 'workspace_fundings.dateSeparator'/>
                                                        <span *ngIf="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span *ngIf="group.getActive().endDate.month">-</span><span *ngIf="group.getActive().endDate.month">{{group.getActive().endDate.month}}</span>
                                                    </span>
                                                    <span class="funding-date" *ngIf="!group.getActive().startDate && group.getActive().endDate">
                                                         <span *ngIf="group.getActive().endDate.year">{{group.getActive().endDate.year}}</span><span *ngIf="group.getActive().endDate.month">-</span><span *ngIf="group.getActive().endDate.month">{{group.getActive().endDate.month}}</span>
                                                    </span>                                
                                                    <!-- Funding type -->
                                                    <span *ngIf="(group.getActive().startDate || group.getActive().endDate) && group.getActive().fundingType.value">|</span> <span class="capitalize">{{group.getActive().fundingTypeForDisplay}}</span>
                                                </div>                            
                                            </div>

                                            <div class="col-md-3 col-sm-3 col-xs-5 workspace-toolbar">
                                                <ul class="workspace-private-toolbar" *ngIf="!(editSources[group.groupId] == true)">
                                                    <#if !(isPublicProfile??)>
                                                    <!-- Bulk edit tool / for further implementation
                                                    <li *ngIf="bulkEditShow == true" class="hidden-xs bulk-checkbox-item">                                
                                                            <input type="checkbox" [ngModel]="bulkEditMap[funding.putCode.value]" class="bulk-edit-input ng-pristine ng-valid pull-right">                                                            
                                                    </li>
                                                    -->
                                                    </#if>
                                                    <!-- Show/Hide Details -->
                                                    <li class="works-details" *ngIf="editSources[group.groupId] == false">                                        
                                                        <a (click)="showDetailsMouseClick(group.groupId,$event);" (mouseenter)="showTooltip(group.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group.groupId+'-showHideDetails')">
                                                            <span [ngClass]="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                                            </span>
                                                        </a>                                        
                                                        <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group.groupId+'-showHideDetails'] == true">
                                                            <div class="arrow"></div>
                                                            <div class="popover-content">
                                                                <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>                                    
                                                                <span *ngIf="moreInfo[group.groupId]">Hide Details</span>
                                                            </div>
                                                        </div>                                        
                                                    </li>
                                                    <#if !(isPublicProfile??)>
                                                        <li>
                                                            <@orcid.privacyToggle2  angularModel="group.getActive().visibility.visibility"
                                                                questionClick="toggleClickPrivacyHelp(group.getActive().putCode.value)"
                                                                clickedClassCheck="{'popover-help-container-show':privacyHelp[group.getActive().putCode.value]==true}"
                                                                publicClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PUBLIC', $event)"
                                                                limitedClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'LIMITED', $event)"
                                                                privateClick="fundingSrvc.setGroupPrivacy(group.getActive().putCode.value, 'PRIVATE', $event)" />
                                                        </li>
                                                    </#if>  
                                                </ul>
                                                <#if !(isPublicProfile??)>
                                                    <div *ngIf="!group.consistentVis() && !editSources[group.groupId]" class="vis-issue">
                                                         <div class="popover-help-container">
                                                            <span class="glyphicons circle_exclamation_mark" (mouseleave)="hideTooltip('vis-issue')" (mouseenter)="showTooltip('vis-issue')"></span>
                                                            <div class="popover vis-popover bottom" *ngIf="showElement['vis-issue']">
                                                                <div class="arrow"></div>
                                                                <div class="popover-content">
                                                                    <@orcid.msg 'groups.common.data_inconsistency' />
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </#if>
                                            </div>
                                        </div>

                                        <!-- Active Row Identifiers / URL / Validations / Versions -->
                                        <div class="row" *ngIf="group.activePutCode == funding.putCode.value">
                                            <div class="col-md-12 col-sm-12 bottomBuffer">
                                                <ul class="id-details clearfix">
                                                    <li><!--
                                                        <span *ngFor='let ei of group.getActive().externalIdentifiers | orderBy:["-relationship.value", "type.value"]' class="clearfix">
                                                             ***
                                                            <span bind-html-compile='ei | externalIdentifierHtml:$first:$last:group.getActive().externalIdentifiers.length:group.getActive().fundingType:moreInfo[group.groupId]' class="url-popover"> 
                                                            </span>
                                                        </span>
                                                            -->
                                                    </li>
                                                    <li *ngIf="group.getActive().url.value" class="url-popover">
                                                        <@orcid.msg 'manual_funding_form_contents.label_url'/>: <a href="{{group.getActive().url.value | urlProtocol}}" [ngClass]="{'truncate-anchor' : moreInfo[group.groupId] == false || moreInfo[group.groupId] == null}" [innerHtml]="group.getActive().url.value" target="funding.putCode.value" (mouseenter)='showURLPopOver(funding.putCode.value + "-alternate")' (mouseleave)='hideURLPopOver(funding.putCode.value + "-alternate")'></a>
                                                        <div class="popover-pos">
                                                            <div class="popover-help-container">
                                                               <div class="popover bottom" [ngClass]="{'block' : displayURLPopOver[funding.putCode.value + '-alternate'] == true}">
                                                                    <div class="arrow"></div>
                                                                    <div class="popover-content">
                                                                        <a href="{{group.getActive().url.value}}" target="url.value" >{{group.getActive().url.value}}</a>
                                                                    </div>                
                                                                </div>                              
                                                            </div>
                                                        </div>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>

                                        <!-- more info -->
                                        <!--
                                        include "funding_more_info_inc_v3.ftl
                                        -->
                                        <div class="more-info" *ngIf="moreInfo[group.groupId] && group.activePutCode == funding.putCode.value">
                                            <span class="dotted-bar"></span>    
                                            <div class="row">        
                                                <!-- Funding subtype -->
                                                <div class="col-md-6" *ngIf="group.getActive().organizationDefinedFundingSubType.subtype.value" >
                                                    <div class="bottomBuffer">                    
                                                        <strong><@orcid.msg 'manual_funding_form_contents.organization_defined_type.label'/></strong>
                                                        <div [innerHtml]="group.getActive().organizationDefinedFundingSubType.subtype.value"></div>
                                                    </div>        
                                                </div> 
                                                
                                                <!-- Funding translated title -->
                                                <div class="col-md-6" *ngIf="group.getActive().fundingTitle.translatedTitle.content" >
                                                    <div class="bottomBuffer">                
                                                        <strong><@orcid.msg
                                                            'manual_funding_form_contents.label_translated_title'/></strong>
                                                        <div [innerHtml]="renderTranslatedTitleInfo(funding)"></div>                    
                                                    </div>        
                                                </div>
                                                
                                                <!-- Funding Amount -->
                                                <div class="col-md-6" *ngIf="group.getActive().amount.value" >
                                                    <div class="bottomBuffer">                
                                                        <strong><@orcid.msg 'manual_funding_form_contents.label_amount'/></strong>
                                                        <div>{{group.getActive().currencyCode.value}} {{group.getActive().amount.value}}</div>                
                                                    </div>
                                                </div>
                                                
                                                <!-- Contribuitors -->
                                                <div class="col-md-6" *ngIf="group?.getActive()?.contributors?.length > 0" >
                                                    <div class="bottomBuffer">
                                                        <strong><@orcid.msg 'manual_funding_form_contents.label_contributors'/></strong>
                                                        <div *ngFor="let contributor of group.getActive().contributors">
                                                            {{contributor.creditName.value}} <span
                                                                >{{contributor}}<!--  | contributorFilter ***--></span>
                                                        </div>        
                                                    </div>
                                                </div>
                                                
                                                <!-- Description -->
                                                <div class="col-md-6" *ngIf="group.getActive().description.value" >
                                                    <div class="bottomBuffer">                
                                                        <strong><@orcid.msg 'manual_funding_form_contents.label_description'/></strong>
                                                        <div [innerHtml]="group.getActive().description.value"></div>                
                                                    </div>
                                                </div>
                                                
                                                <!-- Created Date -->
                                                <div class="col-md-6">
                                                    <strong><@orcid.msg 'groups.common.created'/></strong>
                                                    <div [innerHtml]="group.getActive().createdDate | ajaxFormDateToISO8601"></div>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- end of include -->

                                        <!-- active row source display -->
                                        <div class="row source-line" *ngIf="group.activePutCode == funding.putCode.value">
                                            <div class="col-md-7 col-sm-7 col-xs-12" *ngIf="editSources[group.groupId] == true">                              
                                                {{(group.getActive().sourceName == null || group.getActive().sourceName == '') ? group.getActive().source : group.getActive().sourceName}}
                                            </div>                          
                                            <div class="col-md-3 col-sm-3 col-xs-6" *ngIf="editSources[group.groupId] == true">

                                                <span class="glyphicon glyphicon-check" *ngIf="funding.putCode.value == group.defaultPutCode"></span><span *ngIf="funding.putCode.value == group.defaultPutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                                                <#if !(isPublicProfile??)>
                                                    <div *ngIf="editSources[group.groupId]">
                                                        <a (click)="fundingSrvc.makeDefault(group, funding.putCode.value);" *ngIf="funding.putCode.value != group.defaultPutCode" class="">
                                                            <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                                        </a>
                                                    </div>
                                                </#if>
                                            </div>
                                            <div class="col-md-2 col-sm-2  col-xs-6 trash-source" *ngIf="editSources[group.groupId]">

                                                <#if !(isPublicProfile??)>
                                                    <ul class="sources-actions">
                                                        <li>
                                                            <@orcid.editActivityIcon
                                                        activity="funding"
                                                        click="openEditFunding(funding.putCode.value)"
                                                        toolTipSuffix="editFundingToolSourceActions"
                                                        toolTipClass="popover popover-tooltip top edit-activeSource-popover"
                                                        />
                                                        </li>
                                                        <li>
                                                            <a (click)="deleteFundingConfirm(group.getActive().putCode.value, false)"  (mouseenter)="showTooltip(group.groupId+'-deleteActiveSource')" (mouseleave)="hideTooltip(group.groupId+'-deleteActiveSource')">
                                                                <span class="glyphicon glyphicon-trash"></span>
                                                            </a>

                                                            <div class="popover popover-tooltip top delete-activeSource-popover" *ngIf="showElement[group.groupId+'-deleteActiveSource']">
                                                                <div class="arrow"></div>
                                                                <div class="popover-content">
                                                                    <@orcid.msg 'groups.common.delete_this_source' />
                                                                </div>
                                                            </div>

                                                        </li>
                                                    </ul>
                                                </#if>
                                            </div>
                                        </div>

                                        <!-- not active row && edit sources -->
                                        <div *ngIf="group.activePutCode != funding.putCode.value" class="row source-line">
                                            <div class="col-md-7 col-sm-7 col-xs-12">
                                                    <a (click)="group.activePutCode = funding.putCode.value;">                                
                                                    {{(funding.sourceName == null || funding.sourceName == '') ? funding.source : funding.sourceName}}
                                                </a>
                                            </div>                        
                                            <div class="col-md-3 col-sm-3 col-xs-6">
                                                 <#if !(isPublicProfile??)>
                                                    <span class="glyphicon glyphicon-check" *ngIf="funding.putCode.value == group.defaultPutCode"></span><span *ngIf="funding.putCode.value == group.defaultPutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                                                    <a (click)="fundingSrvc.makeDefault(group, funding.putCode.value);" *ngIf="funding.putCode.value != group.defaultPutCode">
                                                       <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                                    </a>
                                                </#if>
                                            </div>


                                            <div class="col-md-2 col-sm-2 col-xs-6 trash-source">
                                                <#if !(isPublicProfile??)>
                                                    <ul class="sources-actions">
                                                        <li> 
                                                            <@orcid.editActivityIcon
                                                                activity="funding"
                                                                click="openEditFunding(funding.putCode.value)"
                                                                toolTipSuffix="editFundingToolSourceActions"
                                                                toolTipClass="popover popover-tooltip top edit-inactiveSource-popover"
                                                            />
                                                        </li>
                                                        <li>
                                                            <a (click)="deleteFundingConfirm(group.getActive().putCode.value, false)" (mouseenter)="showTooltip(funding.putCode.value+'-deleteInactiveSource')" (mouseleave)="hideTooltip(funding.putCode.value+'-deleteInactiveSource')">
                                                                <span class="glyphicon glyphicon-trash"></span>
                                                            </a>
                                                            <div class="popover popover-tooltip top delete-inactiveSource-popover" *ngIf="showElement[funding.putCode.value+'-deleteInactiveSource']">
                                                                <div class="arrow"></div>
                                                                <div class="popover-content">
                                                                     <@orcid.msg 'groups.common.delete_this_source' />
                                                                </div>
                                                            </div>
                                                        </li>
                                                    </ul>
                                                </#if>
                                            </div>
                                        </div>

                                        <!-- Bottom row -->

                                        <div class="row source-line" *ngIf="editSources[group.groupId] == false">
                                            <div class="col-md-7 col-sm-7 col-xs-12">
                                                  <@orcid.msg 'groups.common.source'/>: {{(funding.sourceName == null || funding.sourceName == '') ? funding.source : funding.sourceName}}
                                            </div>                          
                                            <div class="col-md-3 col-sm-3 col-xs-6" *ngIf="group.activePutCode == funding.putCode.value">
                                                <span class="glyphicon glyphicon-check"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span> <span *ngIf="group.activitiesCount != 1">(</span><a (click)="showSources(group)" *ngIf="group.activitiesCount != 1" (mouseenter)="showTooltip(group.groupId+'-sources')" (mouseleave)="hideTooltip(group.groupId+'-sources')"><@orcid.msg 'groups.common.of'/> {{group.activitiesCount}}</a><span *ngIf="group.activitiesCount != 1">)</span>
                                                
                                                <div class="popover popover-tooltip top sources-popover" *ngIf="showElement[group.groupId+'-sources']">
                                                    <div class="arrow"></div>
                                                    <div class="popover-content">
                                                        <@orcid.msg 'groups.common.sources.show_other_sources' />                                
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-md-2 col-sm-2 col-xs-6">
                                                <ul class="sources-options" >
                                                    <#if !(isPublicProfile??)>
                                                        <li>
                                                             <@orcid.editActivityIcon
                                                                activity="funding"
                                                                click="openEditFunding(funding.putCode.value)"
                                                                toolTipSuffix="editFundingToolTipSources"
                                                                toolTipClass="popover popover-tooltip top edit-source-popover"
                                                             />
                                                        </li>
                                                        <li *ngIf="!(group.activitiesCount == 1 || editSources[group.groupId] == true)">
                        
                                                            <a (click)="showSources(group)" (mouseenter)="showTooltip(group.groupId+'-deleteGroup')" (mouseleave)="hideTooltip(group.groupId+'-deleteGroup')">
                                                                 <span class="glyphicon glyphicon-trash"></span>
                                                            </a>
                                                            <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group.groupId+'-deleteGroup']">
                                                                 <div class="arrow"></div>
                                                                <div class="popover-content">
                                                                     <@orcid.msg 'groups.common.delete_this_source' />                                
                                                                </div>
                                                            </div>  
                                                        </li>
                                                        <li *ngIf="group.activitiesCount == 1">
                                                           <a id="delete-funding_{{group.getActive().putCode.value}}" (click)="deleteFundingConfirm(group.getActive().putCode.value, false)" (mouseenter)="showTooltip(group.groupId+'-deleteSource')" (mouseleave)="hideTooltip(group.groupId+'-deleteSource')">
                                                              <span class="glyphicon glyphicon-trash"></span>
                                                           </a>                                 
                                                          <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group.groupId+'-deleteSource']">
                                                            <div class="arrow"></div>
                                                               <div class="popover-content">
                                                                   <@orcid.msg 'groups.common.delete_this_source' />                                
                                                               </div>
                                                            </div>
                                                        </li>
                                                    </#if>
                                                </ul>
                                            </div>
                                        </div>
                                    </li><!-- End line -->
                                </div>
                            </ul>
                        </div>
                    </li>
                </ul>

                <div *ngIf="fundingSrvc?.loading" class="text-center">
                    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                </div>
                <div *ngIf="fundingSrvc?.loading == false && fundingSrvc?.groups?.length == 0" class="" >
                    <strong>
                        <#if (publicProfile)?? && publicProfile == true><@orcid.msg 'workspace_fundings_body_list.nograntaddedyet' /><#else><@orcid.msg 'workspace_fundings.havenotaddaffiliation' />
                            <#if fundingImportWizards?has_content>
                                <a (click)="showTemplateInModal('import-funding-modal')"> <@orcid.msg 'workspace_fundings_body_list.addsomenow'/></a>
                            <#else>
                                <span><@orcid.msg 'workspace_fundings_body_list.addsomenow'/></span>
                            </#if>
                        </#if>
                    </strong>
                </div>
                <!-- End of body_funding_inc_v3.ftl include -->
            </div>
        </div>
    </div>
</script>