<div class="work-list-container">
    <ul class="sources-edit-list">
        <!-- Edit sources -->
        <li *ngIf="editSources[group.groupId] == true" class="source-header" [ngClass]="{'source-active' : editSources[group.groupId] == true}">
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
                                            <span *ngIf="moreInfo[group.groupId] == true" class="ng-hide">Hide Details</span>
                                        </div>
                                    </div>                                        
                                </li>
                                <#if !(isPublicProfile??)>
                                    <li>
                                        <@orcid.privacyToggle2Ng2  angularModel="group?.activities[group?.activePutCode]?.visibility.visibility"
                                        elementId="group.activePutCode"
                                        questionClick="toggleClickPrivacyHelp(group.activePutCode)"
                                        clickedClassCheck="{'popover-help-container-show':privacyHelp[group?.activities[group?.activePutCode]?.putCode.value]==true}" 
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
        <li *ngFor="let funding of group.activitiesArray">
            <!-- active row summary info -->
            <div class="row" *ngIf="group?.activePutCode == group?.activities[group?.activePutCode]?.putCode.value">
                <div class="col-md-9 col-sm-9 col-xs-7">
                    <!--Title-->
                    <h3 class="workspace-title">                                
                       <span *ngIf="group?.activities[group?.activePutCode]?.fundingTitle?.title?.value">{{group?.activities[group?.activePutCode]?.fundingTitle?.title?.value}}</span>                               
                    </h3>
                    <div class="clear-fix left">
                        <!--Organization-->
                        <span *ngIf="group?.activities[group?.activePutCode]?.fundingName?.value">{{group?.activities[group?.activePutCode]?.fundingName?.value}}</span> (<span *ngIf="group?.activities[group?.activePutCode]?.city.value">{{group?.activities[group?.activePutCode]?.city?.value}}</span><span *ngIf="group?.activities[group?.activePutCode]?.region.value">, {{group?.activities[group?.activePutCode]?.region?.value}}</span>
                        <span *ngIf="group?.activities[group?.activePutCode]?.countryForDisplay">, {{group?.activities[group?.activePutCode]?.countryForDisplay}}</span>)
                    </div>  
                    <div class="info-detail">
                        <!-- Funding date -->
                        <span class="funding-date" *ngIf="group?.activities[group?.activePutCode]?.startDate">
                            <span *ngIf="group?.activities[group?.activePutCode]?.startDate.year">{{group?.activities[group?.activePutCode]?.startDate?.year}}</span><span *ngIf="group?.activities[group?.activePutCode]?.startDate?.month">-{{group?.activities[group?.activePutCode]?.startDate?.month}}</span><span *ngIf="group?.activities[group?.activePutCode]?.startDate?.day">-{{group?.activities[group?.activePutCode]?.startDate?.day}}</span> 
                            <span><@orcid.msg 'workspace_affiliations.dateSeparator'/></span>
                            <span [hidden]="group?.activities[group?.activePutCode]?.endDate && group?.activities[group?.activePutCode]?.endDate?.year"><@orcid.msg 'workspace_affiliations.present'/></span>
                            <span *ngIf="group?.activities[group?.activePutCode]?.endDate">
                                <span *ngIf="group?.activities[group?.activePutCode]?.endDate?.year">{{group?.activities[group?.activePutCode]?.endDate?.year}}</span><span *ngIf="group?.activities[group?.activePutCode]?.endDate?.month">-{{group?.activities[group?.activePutCode]?.endDate?.month}}</span><span *ngIf="group?.activities[group?.activePutCode]?.endDate?.day">-{{group?.activities[group?.activePutCode]?.endDate?.day}}</span>
                            </span>
                        </span>                            
                        <!-- Funding type -->
                        <span *ngIf="(group?.activities[group?.activePutCode]?.startDate || group?.activities[group?.activePutCode]?.endDate) && group?.activities[group?.activePutCode]?.fundingType?.value">|</span> <span class="capitalize">{{group?.activities[group?.activePutCode]?.fundingTypeForDisplay}}</span>
                    </div>                            
                </div>
                <div class="col-md-3 col-sm-3 col-xs-5 workspace-toolbar">
                    <ul class="workspace-private-toolbar" *ngIf="!(editSources[group.groupId] == true)">
                        <!-- Show/Hide Details -->
                        <li class="works-details" *ngIf="!(editSources[group.groupId] == true)">                                        
                            <a (click)="showDetailsMouseClick(group,$event);" (mouseenter)="showTooltip(group.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group.groupId+'-showHideDetails')">
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
                                <@orcid.privacyToggle2Ng2  angularModel="group?.activities[group?.activePutCode]?.visibility.visibility"
                                elementId="group.activePutCode"
                                questionClick="toggleClickPrivacyHelp(group.activePutCode)"
                                clickedClassCheck="{'popover-help-container-show':privacyHelp[group?.activities[group?.activePutCode]?.putCode.value]==true}" 
                                publicClick="setGroupPrivacy(group, 'PUBLIC', $event)" 
                                limitedClick="setGroupPrivacy(group, 'LIMITED', $event)" 
                                privateClick="setGroupPrivacy(group, 'PRIVATE', $event)" />
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
            <div class="row" *ngIf="group.activePutCode == group?.activities[group?.activePutCode]?.putCode.value">
                <div class="col-md-12 col-sm-12 bottomBuffer">
                    <ul class="id-details">
                        <li class="url-work"> 
                            <ul class="id-details"> 
                                <li *ngFor='let extID of group?.activities[group?.activePutCode]?.affiliationExternalIdentifiers;let i = index;trackBy:trackByIndex | orderBy:["-relationship.value", "externalIdentifierType.value"]' class="url-popover">
                                    <span *ngIf="group?.activities[group?.activePutCode]?.affiliationExternalIdentifiers[0]?.externalIdentifierId?.value?.length > 0">
                                        <ext-id-popover-ng2 [extID]="extID" [putCode]="group?.activities[group?.activePutCode]?.putCode.value+i" activityType="affiliation"></ext-id-popover-ng2>
                                    </span>
                                </li>
                            </ul>
                        </li>
                        <li *ngIf="group?.activities[group?.activePutCode]?.url?.value" class="url-popover">
                            <@orcid.msg 'common.url' />: <a href="{{group?.activities[group?.activePutCode]?.url?.value | urlProtocol}}" [ngClass]="{'truncate-anchor' : moreInfo[group.groupId] == false || moreInfo[group.groupId] == undefined}" target="funding.url.value">{{group?.activities[group?.activePutCode]?.url?.value}}</a>
                        </li>
                    </ul>
                </div>
            </div>
            <!-- more info -->
            <div class="more-info" *ngIf="moreInfo[group.groupId] && group.activePutCode == group?.activities[group?.activePutCode]?.putCode.value">
                <span class="dotted-bar"></span>    
                <div class="row">        
                    <!-- Funding subtype -->
                    <div class="col-md-6" *ngIf="group?.activities[group?.activePutCode]?.organizationDefinedFundingSubType?.subtype?.value" >
                        <div class="bottomBuffer">                    
                            <strong><@orcid.msg 'manual_funding_form_contents.organization_defined_type.label'/></strong>
                            <div>{{group?.activities[group?.activePutCode]?.organizationDefinedFundingSubType?.subtype?.value}}</div>
                        </div>        
                    </div> 
                    <!-- Funding translated title -->
                    <div class="col-md-6" *ngIf="group?.activities[group?.activePutCode]?.fundingTitle?.translatedTitle?.content" >
                        <div class="bottomBuffer">                
                            <strong><@orcid.msg
                                'manual_funding_form_contents.label_translated_title'/></strong> <span><i>({{group?.activities[group?.activePutCode]?.fundingTitle?.translatedTitle?.languageName}})</i></span>
                                    <div>{{group?.activities[group?.activePutCode]?.fundingTitle?.translatedTitle?.content}}</div>

                        </div>        
                    </div>
                    <!-- Funding Amount -->
                    <div class="col-md-6" *ngIf="group?.activities[group?.activePutCode]?.amount?.value" >
                        <div class="bottomBuffer">                
                            <strong><@orcid.msg 'manual_funding_form_contents.label_amount'/></strong>
                            <div>{{group?.activities[group?.activePutCode]?.currencyCode?.value}} {{group?.activities[group?.activePutCode]?.amount?.value}}</div>                
                        </div>
                    </div>
                    
                    <!-- Contribuitors -->
                    <div class="col-md-6" *ngIf="group?.activities[group?.activePutCode]?.contributors.length > 0" >
                        <div class="bottomBuffer">
                            <strong><@orcid.msg 'manual_funding_form_contents.label_contributors'/></strong>
                            <div *ngFor="let contributor of group?.activities[group?.activePutCode]?.contributors">
                                {{contributor?.creditName?.value}} <span>{{contributor | contributorFilter}}</span>
                            </div>        
                        </div>
                    </div>
                    <!-- Description -->
                    <div class="col-md-6" *ngIf="group?.activities[group?.activePutCode]?.description.value" >
                        <div class="bottomBuffer">                
                            <strong><@orcid.msg 'manual_funding_form_contents.label_description'/></strong>
                            <div>{{group?.activities[group?.activePutCode]?.description?.value}}</div>                
                        </div>
                    </div>
                    <!-- Created Date -->
                    <div class="col-md-6">
                        <strong><@orcid.msg 'groups.common.created'/></strong>
                        <div>{{group?.activities[group?.activePutCode]?.createdDate | ajaxFormDateToISO8601}}</div>
                    </div>
                </div>
            </div>
            <!-- active row source display -->
            <div class="row source-line" *ngIf="group?.activePutCode == group?.activities[group?.activePutCode]?.putCode?.value">
                <div class="col-md-7 col-sm-7 col-xs-12" *ngIf="editSources[group.groupId] == true">                              
                    {{(group?.activities[group?.activePutCode]?.sourceName == null || group?.activities[group?.activePutCode]?.sourceName == '') ? group?.activities[group?.activePutCode]?.source : group?.activities[group?.activePutCode]?.sourceName}}
                </div>                          
                <div class="col-md-3 col-sm-3 col-xs-6" *ngIf="editSources[group.groupId] == true">

                    <span class="glyphicon glyphicon-check" *ngIf="group?.activities[group?.activePutCode]?.putCode.value == group?.defaultPutCode"></span><span *ngIf="group?.activities[group?.activePutCode]?.putCode.value == group?.defaultPutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                    <#if !(isPublicProfile??)>
                        <div *ngIf="editSources[group.groupId]">
                            <a (click)="makeDefault(group, group?.activities[group?.activePutCode]?.putCode.value);" *ngIf="group?.activities[group?.activePutCode]?.putCode.value != group?.defaultPutCode" class="">
                                <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                            </a>
                        </div>
                    </#if>
                </div>
                <div class="col-md-2 col-sm-2  col-xs-6 trash-source" *ngIf="editSources[group.groupId]">
                    <#if !(isPublicProfile??)>
                        <ul class="sources-actions">
                            <li>
                                <a *ngIf="userIsSource(group?.activities[group?.activePutCode])" (click)="openEditFunding(group?.activities[group?.activePutCode]?.putCode.value)" (mouseenter)="showTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-editFundingToolTipSources')" (mouseleave)="hideTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-editFundingToolTipSources')">
                                    <span class="glyphicon glyphicon-pencil"></span>
                                </a>
                            </li>
                            <li>
                                <a (click)="deleteFunding(group?.activities[group?.activePutCode])"  (mouseenter)="showTooltip(group.groupId+'-deleteActiveSource')" (mouseleave)="hideTooltip(group.groupId+'-deleteActiveSource')">
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
            <div *ngIf="group?.activePutCode != group?.activities[group?.activePutCode]?.putCode.value" class="row source-line">
                <div class="col-md-7 col-sm-7 col-xs-12">
                        <a (click)="group.activePutCode = group?.activities[group?.activePutCode]?.putCode.value;">                                
                        {{(group?.activities[group?.activePutCode]?.sourceName == null || group?.activities[group?.activePutCode]?.sourceName == '') ? group?.activities[group?.activePutCode]?.source : group?.activities[group?.activePutCode]?.sourceName}}
                    </a>
                </div>                        
                <div class="col-md-3 col-sm-3 col-xs-6">
                     <#if !(isPublicProfile??)>
                        <span class="glyphicon glyphicon-check" *ngIf="group?.activities[group?.activePutCode]?.putCode.value == group?.defaultPutCode"></span><span *ngIf="group?.activities[group?.activePutCode]?.putCode.value == group?.defaultPutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                        <a (click)="makeDefault(group, group?.activities[group?.activePutCode]?.putCode.value);" *ngIf="group?.activities[group?.activePutCode]?.putCode.value != group.defaultPutCode">
                           <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                        </a>
                    </#if>
                </div>


                <div class="col-md-2 col-sm-2 col-xs-6 trash-source">
                    <#if !(isPublicProfile??)>
                        <ul class="sources-actions">
                            <li> 
                                <a *ngIf="userIsSource(group?.activities[group?.activePutCode])" (click)="openEditFunding(group?.activities[group?.activePutCode]?.putCode.value)" (mouseenter)="showTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-editFundingToolTipSources')" (mouseleave)="hideTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-editFundingToolTipSources')">
                                    <span class="glyphicon glyphicon-pencil"></span>
                                </a>
                            </li>
                            <li>
                                <a (click)="deleteFunding(group?.activities[group?.activePutCode])" (mouseenter)="showTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-deleteInactiveSource')" (mouseleave)="hideTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-deleteInactiveSource')">
                                    <span class="glyphicon glyphicon-trash"></span>
                                </a>
                                <div class="popover popover-tooltip top delete-inactiveSource-popover" *ngIf="showElement[group?.activities[group?.activePutCode]?.putCode.value+'-deleteInactiveSource']">
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

            <div class="row source-line" *ngIf="!(editSources[group.groupId] == true)">
                <div class="col-md-7 col-sm-7 col-xs-12">
                      <@orcid.msg 'groups.common.source'/>: {{(group?.activities[group?.activePutCode]?.sourceName == null || group?.activities[group?.activePutCode]?.sourceName == '') ? group?.activities[group?.activePutCode]?.source : group?.activities[group?.activePutCode]?.sourceName}}
                </div>                          
                <div class="col-md-3 col-sm-3 col-xs-6" *ngIf="group.activePutCode == group?.activities[group?.activePutCode]?.putCode.value">
                    <span class="glyphicon glyphicon-check"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span> <span *ngIf="!(group.activitiesCount == 1)">(</span><a (click)="showSources(group)" *ngIf="!(group.activitiesCount == 1)" (mouseenter)="showTooltip(group.groupId+'-sources')" (mouseleave)="hideTooltip(group.groupId+'-sources')"><@orcid.msg 'groups.common.of'/> {{group.activitiesCount}}</a><span *ngIf="!(group.activitiesCount == 1)">)</span>
                    
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

                                <a *ngIf="userIsSource(group?.activities[group?.activePutCode])" (click)="openEditFunding(group?.activities[group?.activePutCode]?.putCode.value)" (mouseenter)="showTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-editFundingToolTipSources')" (mouseleave)="hideTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-editFundingToolTipSources')">
                                    <span class="glyphicon glyphicon-pencil"></span>
                                </a>
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
                               <a id="delete-funding_{{group?.activities[group?.activePutCode]?.putCode.value}}" (click)="deleteFunding(group?.activities[group?.activePutCode])" (mouseenter)="showTooltip(group.groupId+'-deleteSource')" (mouseleave)="hideTooltip(group.groupId+'-deleteSource')">
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
    </ul>
</div>