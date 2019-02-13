<div class="work-list-container">
    <ul class="sources-edit-list">
        <!-- Edit sources -->
        <li *ngIf="editSources[group.groupId]" class="source-header" [ngClass]="{'source-active' : editSources[group.groupId] == true}">
            <div class="sources-header">
                <div class="row">
                    <div class="col-md-7 col-sm-7 col-xs-7">
                        <@orcid.msg 'groups.common.sources' /> <span class="hide-sources" (click)="hideSources(group)"><@orcid.msg 'groups.common.close_sources' /></span>
                    </div>                            
                    <div class="col-md-2 col-sm-2 col-xs-2">                                
                        <@orcid.msgCapFirst 'groups.common.preferred' />
                    </div>                            
                    <div class="col-md-3 col-sm-3 col-xs-3 right">
                        <div class="workspace-toolbar">
                            <ul class="workspace-private-toolbar">
                                 <li class="works-details">
                                    <a (click)="showDetailsMouseClick(group,$event);" (mouseenter)="showTooltip(group.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group.groupId+'-showHideDetails')">
                                        <span [ngClass]="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                        </span>
                                    </a>                                        
                                    <div class="popover popover-tooltip top show-hide-details-popover ng-hide" *ngIf="showElement[group.groupId+'-showHideDetails']">
                                         <div class="arrow"></div>
                                        <div class="popover-content">
                                            <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null" class=""><@orcid.msg 'common.details.show_details'/></span>
                                            <span *ngIf="moreInfo[group.groupId] == true" class="ng-hide"><@orcid.msg 'common.details.hide_details'/></span>
                                        </div>
                                    </div>                                        
                                </li>
                                <#if !(isPublicProfile??)>
                                    <li>
                                        <@orcid.privacyToggle2Ng2 angularModel="group.activeVisibility"
                                        elementId="group.activePutCode"
                                        questionClick="toggleClickPrivacyHelp(group.activePutCode)"
                                        clickedClassCheck="{'popover-help-container-show':privacyHelp[group.fundings[0].putCode.value]==true}" 
                                        publicClick="setGroupPrivacy(group, 'PUBLIC', $event)" 
                                        limitedClick="setGroupPrivacy(group, 'LIMITED', $event)" 
                                        privateClick="setGroupPrivacy(group, 'PRIVATE', $event)" />
                                    </li>
                                </#if>
                            </ul>
                        </div>
                     </div>
                </div>
            </div>
        </li>
        <!--  End edit sources-->
        <ng-container *ngFor="let funding of group.fundings;let i = index;">
            <li *ngIf="group.activePutCode == funding.putCode.value || editSources[group.groupId]">
                <!-- active row summary info -->
                <div class="row" *ngIf="group?.activePutCode == funding.putCode.value"> 
                    <div class="col-md-9 col-sm-9 col-xs-7">
                        <!--Title-->
                        <h3 class="workspace-title">                                
                           <span *ngIf="funding.fundingTitle?.title?.value">{{funding.fundingTitle?.title?.value}}</span>                               
                        </h3>
                        <div class="clear-fix left">
                            <!--Organization-->
                            <span *ngIf="funding.fundingName?.value">{{funding.fundingName?.value}}</span> (<span *ngIf="funding.city?.value">{{funding.city?.value}}</span><span *ngIf="funding.region.value">, {{funding.region?.value}}</span>
                            <span *ngIf="funding.countryForDisplay">, {{funding.countryForDisplay}}</span>)
                        </div>  
                        <div class="info-detail">
                            <!-- Funding date -->
                            <span class="funding-date" *ngIf="funding.startDate?.year">
                                <span *ngIf="funding.startDate.year">{{funding.startDate?.year}}</span><span *ngIf="funding.startDate?.month">-{{funding.startDate?.month}}</span><span *ngIf="funding.startDate?.day">-{{funding.startDate?.day}}</span> 
                                <span>&nbsp;<@orcid.msg 'workspace_affiliations.dateSeparator'/>&nbsp;</span><span [hidden]="funding.endDate && funding.endDate?.year"><@orcid.msg 'workspace_affiliations.present'/></span>
                            </span>
                            <span *ngIf="funding.endDate">
                                <span *ngIf="funding.endDate?.year">{{funding.endDate?.year}}</span><span *ngIf="funding.endDate?.month">-{{funding.endDate?.month}}</span><span *ngIf="funding.endDate?.day">-{{funding.endDate?.day}}</span>
                            </span>                            
                            <!-- Funding type -->
                            <span *ngIf="(funding.startDate?.year || funding.endDate?.year) && funding.fundingType?.value">|</span> <span class="capitalize">{{funding.fundingTypeForDisplay}}</span>
                        </div>                            
                    </div>
                    <div class="col-md-3 col-sm-3 col-xs-5 workspace-toolbar">
                        <ul class="workspace-private-toolbar" *ngIf="!editSources[group.groupId]">
                            <!--Show details toggle-->
                            <li class="works-details">
                                <a (click)="showDetailsMouseClick(group,$event);" (mouseenter)="showTooltip(group.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group.groupId+'-showHideDetails')">
                                    <span [ngClass]="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                    </span>
                                </a>                                        
                                <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group.groupId+'-showHideDetails'] == true">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>   
                                        <span *ngIf="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details'/></span>
                                    </div>
                                </div>                                        
                            </li>
                            <!--Visibility selector-->
                            <#if !(isPublicProfile??)>
                                <li>
                                    <@orcid.privacyToggle2Ng2  angularModel="group.activeVisibility"
                                    elementId="group.activePutCode"
                                    questionClick="toggleClickPrivacyHelp(group.activePutCode)"
                                    clickedClassCheck="{'popover-help-container-show':privacyHelp[funding.putCode.value]==true}" 
                                    publicClick="setGroupPrivacy(group, 'PUBLIC', $event)" 
                                    limitedClick="setGroupPrivacy(group, 'LIMITED', $event)" 
                                    privateClick="setGroupPrivacy(group, 'PRIVATE', $event)" />
                                </li>
                            </#if>  
                        </ul>
                        <!--Inconsistent visibility warning--> 
                        <#if !(isPublicProfile??)>
                            <div *ngIf="!fundingService.consistentVis(group) && !editSources[group.groupId]" class="vis-issue">
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
                <div class="row" *ngIf="group?.activePutCode == funding?.putCode.value">
                    <div class="col-md-12 col-sm-12 bottomBuffer">
                        <ul class="id-details">
                            <li class="url-work"> 
                                <ul class="id-details"> 
                                    <li *ngFor='let extID of funding?.externalIdentifiers;let i = index;trackBy:trackByIndex | orderBy:["-relationship.value", "externalIdentifierType.value"]' class="url-popover">
                                        <span *ngIf="funding?.externalIdentifiers[0]?.externalIdentifierId?.value?.length > 0">
                                            <ext-id-popover-ng2 [extID]="extID" [putCode]="funding?.putCode.value+i" activityType="funding"></ext-id-popover-ng2>
                                        </span>
                                    </li>
                                </ul>
                            </li>
                            <li *ngIf="funding?.url?.value" class="url-popover">
                                <@orcid.msg 'common.url' />: <a href="{{funding?.url?.value | urlProtocol}}" [ngClass]="{'truncate-anchor' : moreInfo[group.groupId] == false || moreInfo[group.groupId] == undefined}" target="funding.url.value">{{funding?.url?.value}}</a>
                            </li>
                        </ul>
                    </div>
                </div>
                <!-- more info -->
                <div class="more-info" *ngIf="moreInfo[group.groupId] && group.activePutCode == funding.putCode.value">
                    <div id="ajax-loader" *ngIf="fundingService.details[funding.putCode.value] == undefined">
                        <span id="ajax-loader"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x4 green"></i></span>
                    </div>
                    <div class="content" *ngIf="fundingService.details[funding.putCode.value] != undefined">  
                        <span class="dotted-bar"></span>    
                        <div class="row">        
                            <div class="org-ids" *ngIf="funding?.disambiguatedFundingSourceId?.value">
                                <div class="col-md-12">   
                                    <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                                    <org-identifier-popover-ng2 [value]="funding?.disambiguatedFundingSourceId?.value" [putCode]="funding?.putCode?.value" [type]="funding?.disambiguationSource?.value"></org-identifier-popover-ng2>
                                </div>
                            </div><!--org-ids-->
                            <!-- Funding subtype -->
                            <div class="col-md-6" *ngIf="fundingService.details[funding.putCode.value]?.organizationDefinedFundingSubType?.subtype?.value" >
                                <div class="bottomBuffer">                    
                                    <strong><@orcid.msg 'manual_funding_form_contents.organization_defined_type.label'/></strong>
                                    <div>{{fundingService.details[funding.putCode.value]?.organizationDefinedFundingSubType?.subtype?.value}}</div>
                                </div>        
                            </div> 
                            <!-- Funding translated title -->
                            <div class="col-md-6" *ngIf="fundingService.details[funding.putCode.value]?.fundingTitle?.translatedTitle?.content" >
                                <div class="bottomBuffer">                
                                    <strong><@orcid.msg
                                        'manual_funding_form_contents.label_translated_title'/></strong> <span><i>({{fundingService.details[funding.putCode.value]?.fundingTitle?.translatedTitle?.languageName}})</i></span>
                                            <div>{{fundingService.details[funding.putCode.value]?.fundingTitle?.translatedTitle?.content}}</div>

                                </div>        
                            </div>
                            <!-- Funding Amount -->
                            <div class="col-md-6" *ngIf="fundingService.details[funding.putCode.value]?.amount?.value" >
                                <div class="bottomBuffer">                
                                    <strong><@orcid.msg 'manual_funding_form_contents.label_amount'/></strong>
                                    <div>{{fundingService.details[funding.putCode.value]?.currencyCode?.value}} {{fundingService.details[funding.putCode.value]?.amount?.value}}</div>                
                                </div>
                            </div>
                            
                            <!-- Contribuitors -->
                            <div class="col-md-6" *ngIf="fundingService.details[funding.putCode.value]?.contributors?.length > 0" >
                                <div class="bottomBuffer">
                                    <strong><@orcid.msg 'manual_funding_form_contents.label_contributors'/></strong>
                                    <div *ngFor="let contributor of fundingService.details[funding.putCode.value]?.contributors">
                                        {{contributor?.creditName?.value}} <span>{{contributor | contributorFilter}}</span>
                                    </div>        
                                </div>
                            </div>
                            <!-- Description -->
                            <div class="col-md-6" *ngIf="fundingService.details[funding.putCode.value]?.description?.value" >
                                <div class="bottomBuffer">                
                                    <strong><@orcid.msg 'manual_funding_form_contents.label_description'/></strong>
                                    <div>{{fundingService.details[funding.putCode.value]?.description?.value}}</div>                
                                </div>
                            </div>
                        </div>
                        <!--Added/last modified dates-->                
                        <div class="row bottomBuffer">
                            <div class="col-md-6">
                                <div class="bottomBuffer">
                                    <strong><@orcid.msg 'groups.common.added'/></strong><br> 
                                    <span>{{fundingService.details[funding.putCode.value].createdDate | ajaxFormDateToISO8601}}</span>
                                </div>    
                            </div>
                            <div class="col-md-6">
                                <div class="bottomBuffer">
                                    <strong><@orcid.msg 'groups.common.last_modified'/></strong><br> 
                                    <span>{{fundingService.details[funding.putCode.value].lastModified | ajaxFormDateToISO8601}}</span>
                                </div>    
                            </div>      
                        </div><!--Added/last modified dates--> 
                    </div>
                </div>
                <!-- active row source display -->
                <div class="row source-line" *ngIf="group.activePutCode == funding.putCode.value">
                    <div class="col-md-7 col-sm-7 col-xs-12" *ngIf="editSources[group.groupId]">
                        {{(funding.sourceName == null || funding.sourceName == '') ? funding.source : funding.sourceName}}
                             <#--  OBO  -->
                            <ng-container *ngIf="(funding.assertionOriginClientId && funding.assertionOriginClientId !== funding.sourceClientId) ||
                            (funding.source.assertionOriginOrcid && funding.source.assertionOriginOrcid !== funding.source.sourceOrcid)">
                            <i>${springMacroRequestContext.getMessage("public_profile.onBehalfOf")}</i> {{funding.assertionOriginName || funding.assertionOriginOrcid}}
                            </ng-container>
                    </div>                          
                    <div class="col-md-3 col-sm-3 col-xs-6" *ngIf="editSources[group.groupId]">

                        <span class="glyphicon glyphicon-star" *ngIf="funding?.putCode.value == group?.defaultPutCode"></span><span *ngIf="funding?.putCode.value == group?.defaultPutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                        <#if !(isPublicProfile??)>
                            <div *ngIf="editSources[group.groupId]">
                                <a (click)="makeDefault(group, funding?.putCode.value);" *ngIf="funding?.putCode.value != group?.defaultPutCode" class="">
                                    <span class="glyphicon glyphicon-star-empty"></span> <@orcid.msg 'groups.common.make_preferred' />
                                </a>
                            </div>
                        </#if>
                    </div>
                    <div class="col-md-2 col-sm-2  col-xs-6 trash-source" *ngIf="editSources[group.groupId]">
                        <#if !(isPublicProfile??)>
                            <ul class="sources-actions">
                                <li>
                                    <@orcid.editWorkIconNg2
                                        activity="funding"
                                        click="openEditFunding(funding, group)"
                                        toolTipSuffix="editToolTipSource"
                                        toolTipClass="popover popover-tooltip top edit-activeSource-popover"
                                     />
                                </li>
                                <li>
                                    <a (click)="deleteFunding(funding)"  (mouseenter)="showTooltip(group.groupId+'-deleteActiveSource')" (mouseleave)="hideTooltip(group.groupId+'-deleteActiveSource')">
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
                <div *ngIf="group?.activePutCode != funding?.putCode.value" class="row source-line">
                    <div class="col-md-7 col-sm-7 col-xs-12">
                            <a (click)="swapSources(group, funding.putCode.value)">                                
                            {{(funding?.sourceName == null || funding?.sourceName == '') ? funding?.source : funding?.sourceName}}
                             <#--  OBO  -->
                            <ng-container *ngIf="(funding.assertionOriginClientId && funding.assertionOriginClientId !== funding.sourceClientId) ||
                            (funding.source.assertionOriginOrcid && funding.source.assertionOriginOrcid !== funding.source.sourceOrcid)">
                            <i>${springMacroRequestContext.getMessage("public_profile.onBehalfOf")}</i> {{funding.assertionOriginName || funding.assertionOriginOrcid}}
                            </ng-container>
                        </a>
                    </div>                        
                    <div class="col-md-3 col-sm-3 col-xs-6">
                         <#if !(isPublicProfile??)>
                            <span class="glyphicon glyphicon-star" *ngIf="funding?.putCode.value == group?.defaultPutCode"></span><span *ngIf="funding?.putCode.value == group?.defaultPutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                            <a (click)="makeDefault(group, funding?.putCode.value);" *ngIf="funding?.putCode.value != group.defaultPutCode">
                               <span class="glyphicon glyphicon-star-empty"></span> <@orcid.msg 'groups.common.make_preferred' />
                            </a>
                        </#if>
                    </div>


                    <div class="col-md-2 col-sm-2 col-xs-6 trash-source">
                        <#if !(isPublicProfile??)>
                            <ul class="sources-actions">
                                <li> 
                                    <@orcid.editWorkIconNg2
                                        activity="funding"
                                        click="openEditFunding(funding, group)"
                                        toolTipSuffix="editToolTipSourceActions"
                                        toolTipClass="popover popover-tooltip top edit-inactiveSource-popover"
                                     />
                                </li>
                                <li>
                                    <a (click)="deleteFunding(funding)" (mouseenter)="showTooltip(funding?.putCode.value+'-deleteInactiveSource')" (mouseleave)="hideTooltip(funding?.putCode.value+'-deleteInactiveSource')">
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
                <div class="row source-line" *ngIf="editSources[group.groupId] != true">
                    <div class="col-md-7 col-sm-7 col-xs-12">
                          <b><@orcid.msg 'groups.common.source'/>:</b> {{(funding?.sourceName == null || funding.sourceName == '') ? funding.source : funding.sourceName}}
                             <#--  OBO  -->
                            <ng-container *ngIf="(funding.assertionOriginClientId && funding.assertionOriginClientId !== funding.sourceClientId) ||
                            (funding.source.assertionOriginOrcid && funding.source.assertionOriginOrcid !== funding.source.sourceOrcid)">
                            <i>${springMacroRequestContext.getMessage("public_profile.onBehalfOf")}</i> {{funding.assertionOriginName || funding.assertionOriginOrcid}}
                            </ng-container>
                    </div>                          
                    <div class="col-md-3 col-sm-3 col-xs-6" *ngIf="group?.activePutCode == funding?.putCode.value">
                        <span class="glyphicon glyphicon-star"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span>&nbsp;<span *ngIf="!(group.fundings.length == 1)">(</span><a (click)="showSources(group, $event)" *ngIf="!(group.fundings.length == 1)" (mouseenter)="showTooltip(group.groupId+'-sources')" (mouseleave)="hideTooltip(group.groupId+'-sources')"><@orcid.msg 'groups.common.of'/> {{group.fundings.length}}</a><span *ngIf="!(group.fundings.length == 1)">)</span>
                        
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
                                    <@orcid.editWorkIconNg2
                                        activity="funding"
                                        click="openEditFunding(funding, group)"
                                        toolTipSuffix="editToolTip"
                                        toolTipClass="popover popover-tooltip top edit-source-popover"
                                     />
                                </li>
                                <li *ngIf="!(group.fundings.length == 1 || editSources[group.groupId] == true)">

                                    <a (click)="showSources(group, $event)" (mouseenter)="showTooltip(group.groupId+'-deleteGroup')" (mouseleave)="hideTooltip(group.groupId+'-deleteGroup')">
                                         <span class="glyphicon glyphicon-trash"></span>
                                    </a>
                                    <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group.groupId+'-deleteGroup']">
                                         <div class="arrow"></div>
                                        <div class="popover-content">
                                             <@orcid.msg 'groups.common.delete_this_source' />                                
                                        </div>
                                    </div>  
                                </li>
                                <li *ngIf="group.fundings.length == 1">
                                   <a id="delete-funding_{{funding.putCode.value}}" (click)="deleteFunding(funding)" (mouseenter)="showTooltip(group.groupId+'-deleteSource')" (mouseleave)="hideTooltip(group.groupId+'-deleteSource')">
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
                </div><!--Bottom row-->
            </li><!-- End for funding line -->
        </ng-container>
    </ul>
</div>