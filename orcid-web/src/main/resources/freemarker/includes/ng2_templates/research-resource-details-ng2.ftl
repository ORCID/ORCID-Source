<div class="work-list-container">
    <ul class="sources-edit-list">
        <!--Edit sources-->
        <li *ngIf="editSources[group.groupId]" class="source-header" [ngClass]="{'source-active' : editSources[group.groupId] == true}">
            <div class="sources-header">
                <div class="row">
                    <div class="col-md-7 col-sm-7 col-xs-6">
                        <@orcid.msg 'groups.common.sources' /> <span class="hide-sources" (click)="hideSources(group)"><@orcid.msg 'groups.common.close_sources' /></span>
                    </div>
                    
                    <div class="col-md-2 col-sm-2 hidden-xs">
                        <@orcid.msgCapFirst 'groups.common.preferred' />
                    </div>
                    
                    <div class="col-md-3 col-sm-3 col-xs-6 right padding-left-fix">
                        <div class="workspace-toolbar">
                            <ul class="workspace-private-toolbar">              
                                <li class="works-details">
                                    <a (click)="showDetailsMouseClick(group,$event)" (mouseenter)="showTooltip(group?.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group?.groupId+'-showHideDetails')">
                                        <span [ngClass]="(moreInfo[group?.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                        </span>
                                    </a>
                                    <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group.groupId+'-showHideDetails']">
                                        <div class="arrow"></div>
                                        <div class="popover-content">   
                                            <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>   
                                            <span *ngIf="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details'/></span>
                                        </div>
                                    </div>
                                </li>
                                <li *ngIf="!isPublicPage">
                                    <@orcid.privacyToggle2Ng2 angularModel="group.activeVisibility"
                                    elementId="group.activePutCode" 
                                        questionClick="toggleClickPrivacyHelp(group.activePutCode)"
                                        clickedClassCheck="{'popover-help-container-show':privacyHelp[group.activePutCode]==true}"
                                        publicClick="researchResourceService.setGroupPrivacy(group, 'PUBLIC', $event)"
                                        limitedClick="researchResourceService.setGroupPrivacy(group, 'LIMITED', $event)"
                                        privateClick="researchResourceService.setGroupPrivacy(group, 'PRIVATE', $event)"/>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </li>
        <!--End edit sources-->
        <!---Research resource info-->
        <ng-container *ngFor="let researchResource of group.researchResources; let index = index; let first = first; let last = last;">
            <li *ngIf="group.activePutCode == researchResource?.putCode || editSources[group.groupId] == true">
                <div class="row" *ngIf="group.activePutCode == researchResource?.putCode">
                    <div class="col-md-9 col-sm-9 col-xs-7">
                        <!--Proposal-->
                        <!--Title-->
                        <h3 class="workspace-title">
                            <span>{{researchResource?.title}}</span>
                        </h3>
                        <div class="info-detail">
                            <!--Hosts-->
                            <div *ngIf="researchResource?.hosts && !moreInfo[group?.groupId]">
                                <div *ngFor="let host of researchResource?.hosts">
                                    <span>{{host?.name}}</span>
                                    <span *ngIf="host?.city || host?.region  || host?.country"> (</span>
                                    <span *ngIf="host?.city">{{host?.city}}</span><span *ngIf="host?.region">, </span><span>{{host?.region}}</span><span *ngIf="host?.country">, </span><span>{{host?.country}}</span>
                                    <span *ngIf="host?.city || host?.region  || host?.country">)</span>
                                </div>                      
                            </div>
                            <!--Dates-->
                            <div class="info-date">
                                <!--Start date-->                     
                                <span class="affiliation-date" *ngIf="researchResource?.startDate">
                                    <span *ngIf="researchResource?.startDate?.year">{{researchResource?.startDate?.year}}</span>
                                    <span *ngIf="researchResource?.startDate?.month">-{{researchResource?.startDate?.month}}</span>
                                    <span *ngIf="researchResource?.startDate?.day">-{{researchResource?.startDate?.day}}</span>
                                    <span>&nbsp;<@orcid.msg 'workspace_affiliations.dateSeparator'/>&nbsp;</span>
                                    <span [hidden]="researchResource?.endDate && researchResource?.endDate?.year"><@orcid.msg 'workspace_affiliations.present'/></span>
                                    <span *ngIf="researchResource?.endDate">
                                    <span *ngIf="researchResource?.endDate?.year">{{researchResource?.endDate?.year}}</span><span *ngIf="researchResource?.endDate?.month">-{{researchResource?.endDate?.month}}</span><span *ngIf="researchResource?.endDate?.day">-{{researchResource?.endDate?.day}}</span>
                                    </span>
                                </span>
                                <!--End date-->
                                <span class="affiliation-date" *ngIf="!researchResource?.startDate && researchResource?.endDate">
                                    <span *ngIf="researchResource?.endDate?.year">{{researchResource?.endDate?.year}}</span>
                                    <span *ngIf="researchResource?.endDate?.month">-{{researchResource?.endDate?.month}}</span>
                                    <span *ngIf="researchResource?.endDate?.day">-{{researchResource?.endDate?.day}}</span>
                                </span>
                            </div><!--info-date-->
                        </div><!--info-detail-->
                    </div><!--col-md-9 -->
                    <div class="col-md-3 col-sm-3 col-xs-5 workspace-toolbar">
                        <ul class="workspace-private-toolbar" *ngIf="!editSources[group.groupId]"> 
                            <!--Show details toggle-->
                            <li class="works-details" *ngIf="!editSources[group.groupId]">
                                <a (click)="showDetailsMouseClick(group,$event)" (mouseenter)="showTooltip(group?.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group?.groupId+'-showHideDetails')">
                                    <span [ngClass]="(moreInfo[group?.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                    </span>
                                </a>
                                <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group.groupId+'-showHideDetails']">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details' /></span>   
                                        <span *ngIf="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details' /></span>
                                    </div>
                                </div>
                            </li>
                            <!--Visibility selector-->
                            <li *ngIf="!isPublicPage">
                                <@orcid.privacyToggle2Ng2 angularModel="researchResource.visibility.visibility"
                                elementId="group.activePutCode" questionClick="toggleClickPrivacyHelp(group.highestVis())" clickedClassCheck="{'popover-help-container-show':privacyHelp[researchResource.putCode.value]==true}" publicClick="setGroupPrivacy(group, 'PUBLIC', $event)" limitedClick="setGroupPrivacy(group, 'LIMITED', $event)" privateClick="setGroupPrivacy(group, 'PRIVATE', $event)" />
                            </li>
                        </ul>
                        <!--Inconsistent visibility warning-->  
                        <div *ngIf="!isPublicPage && !researchResourceService.consistentVis(group) && !editSources[group.groupId]" class="vis-issue">
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
                    </div>
                </div>
                <!--Identifiers-->
                <div class="row" *ngIf="group.activePutCode == researchResource.putCode">
                    <div class="col-md-12 col-sm-12 bottomBuffer">
                        <ul class="id-details clearfix">
                            <li class="url-work clearfix">
                                <ul class="id-details clearfix">
                                    <li *ngFor='let extID of researchResource?.externalIdentifiers;let i = index;trackBy:trackByIndex | orderBy:["-relationship.value", "type.value"]' class="url-popover">
                                        <span *ngIf="researchResource?.externalIdentifiers[0]?.externalIdentifierId?.value?.length > 0">
                                            <ext-id-popover-ng2 [extID]="extID" [putCode]="researchResource.putCode+i" [activityType]="'researchResource'"></ext-id-popover-ng2>
                                        </span>
                                     </li>
                                </ul>                                   
                            </li>
                        </ul>
                    </div>
                </div>
                <!--More info-->
                <div class="more-info" *ngIf="moreInfo[group?.groupId] && group.activePutCode == researchResource.putCode">
                    <div id="ajax-loader" *ngIf="researchResourceService.details[researchResource.putCode] == undefined">
                        <span id="ajax-loader"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x4 green"></i></span>
                    </div> 
                    <div class="content" *ngIf="researchResourceService.details[researchResource.putCode] != undefined">
                        <span class="dotted-bar"></span>
                        <div class="row">
                            <!--Hosts-->
                            <div class="col-md-12" *ngIf="researchResource?.hosts">
                                <div *ngFor="let host of researchResource?.hosts;let i = index;trackBy:trackByIndex">
                                    <span>{{host?.name}}</span>
                                    <span *ngIf="host?.city || host?.region  || host?.country"> (</span>
                                    <span *ngIf="host?.city">{{host?.city}}</span><span *ngIf="host?.region">, </span><span>{{host?.region}}</span><span *ngIf="host?.country">, </span><span>{{host?.country}}</span>
                                    <span *ngIf="host?.city || host?.region  || host?.country">)</span>
                                    <!--Org ids-->
                                    <div class="org-ids" *ngIf="host?.orgDisambiguatedId">
                                        <div class="col-md-12">   
                                            <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                                            <org-identifier-popover-ng2 [value]="host?.orgDisambiguatedId" [putCode]="researchResource.putCode+i" [type]="host?.disambiguationSource"></org-identifier-popover-ng2>
                                        </div>
                                    </div><!--org-ids-->
                                </div>
                            </div>
                            <!--Translated title-->       
                            <div class="col-md-6" *ngIf="researchResourceService.details[researchResource.putCode].translatedTitle" >
                                <div class="bottomBuffer">
                                    <strong><@orcid.msg
                                        'manual_work_form_contents.labeltranslatedtitle'/></strong> <span><i>({{researchResourceService.details[researchResource.putCode].translatedTitleLanguageCode}})</i></span>
                                    <div>{{researchResourceService.details[researchResource.putCode].translatedTitle}}</div>                
                                </div>
                            </div>
                            <div class="col-md-6" *ngIf="researchResourceService.details[researchResource.putCode].url" >
                                <div class="bottomBuffer">
                                    <strong>
                                        <@orcid.msg
                                        'common.url'/>
                                    </strong>
                                    <div>
                                        <a href="{{researchResourceService.details[researchResource.putCode].url | urlProtocol}}" target="proposal.url">{{researchResourceService.details[researchResource.putCode].url}}</a>
                                    </div>              
                                </div>
                            </div>          
                        </div>                                         
                        <!--Added/last modified dates-->                
                        <div class="row bottomBuffer">
                            <div class="col-md-6">
                                <div class="bottomBuffer">
                                    <strong><@orcid.msg 'groups.common.added'/></strong><br> 
                                    <span>{{researchResourceService.details[researchResource.putCode].createdDate | ajaxFormDateToISO8601}}</span>
                                </div>    
                            </div>
                            <div class="col-md-6">
                                <div class="bottomBuffer">
                                    <strong><@orcid.msg 'groups.common.last_modified'/></strong><br> 
                                    <span>{{researchResourceService.details[researchResource.putCode].lastModifiedDate | ajaxFormDateToISO8601}}</span>
                                </div>    
                            </div>      
                        </div><!--Added/last modified dates--> 
                        <div class="row bottomBuffer">                                    
                                <div class="research-resource-list-container col-md-12" *ngIf="researchResourceService.details[researchResource.putCode].items?.length > 0">
                                    <ul class="sources-edit-list">
                                        <li class="source-active">
                                            <!-- Header -->
                                            <div class="sources-header">
                                                <div class="row">
                                                    <div class="col-md-5 col-sm-5 col-xs-5">
                                                        <@orcid.msg 'manage.research_resources.resource_item' />
                                                    </div>
                                                    <div class="col-md-2 col-sm-2 col-xs-2">
                                                        <@orcid.msg 'peer_review.type' />
                                                    </div>
                                                    <div class="col-md-2 col-sm-2 col-xs-2 pull-right">
                                                        <span><@orcid.msg 'peer_review.actions' /></span>
                                                    </div>
                                                </div>
                                            </div>
                                            <!-- End of Header -->
                                        </li>
                                        <li *ngFor="let resourceItem of researchResourceService.details[researchResource.putCode].items; let index = index; let first = first; let last = last;">
                                            <!-- Active row -->
                                            <div class="row source-line-peer-review">
                                                <!--Resource item name-->
                                                <div class="col-md-5 col-sm-5 col-xs-5">{{resourceItem.resourceName}}</div>
                                                <!--Resource item type-->
                                                <div class="col-md-2 col-sm-2 col-xs-2">{{resourceItem.resourceType}}</div>
                                                <!--Action buttons-->
                                                <div class="col-md-2 col-sm-2 col-xs-2 pull-right">                              
                                                    <span> 
                                                        <a (click)="toggleResourceItemDetails(researchResource.putCode + 'resourceItem' + index,$event)" *ngIf="!showResourceItemDetails[researchResource.putCode+'resourceItem'+index]">
                                                        <span class="glyphicons expand"></span>
                                                        <span class="hidden-xs"><@orcid.msg 'common.details.show_details_lc' /></span>
                                                        </a> 
                                                        <a (click)="toggleResourceItemDetails(researchResource.putCode + 'resourceItem' + index,$event);" *ngIf="showResourceItemDetails[researchResource.putCode+'resourceItem'+index]">
                                                            <span class="glyphicons collapse_top"></span>
                                                            <span class="hidden-xs"><@orcid.msg 'common.details.hide_details_lc' /></span>
                                                        </a>
                                                    </span>
                                                </div>
                                            </div>                      
                                             
                                            <!-- Details row -->
                                            <div class="row" *ngIf="showResourceItemDetails[researchResource.putCode+'resourceItem'+index]" >
                                                <!--Ext ids-->
                                                <div class="col-md-12 info-detail" *ngIf="resourceItem?.externalIdentifiers?.length > 0">
                                                    <ul class="id-details clearfix">
                                                        <li *ngFor='let extID of resourceItem?.externalIdentifiers;let i = index;trackBy:trackByIndex | orderBy:["-relationship.value", "type.value"]' class="url-popover">
                                                            <span *ngIf="resourceItem?.externalIdentifiers[0]?.externalIdentifierId?.value?.length > 0">
                                                                <ext-id-popover-ng2 [extID]="extID" [putCode]="researchResource.putCode+'resourceItem'+index" [activityType]="'researchResource'"></ext-id-popover-ng2>
                                                            </span>
                                                         </li>
                                                    </ul> 
                                                </div>

                                                <!--Hosts-->
                                                <div class="col-md-12 info-detail" *ngIf="resourceItem?.hosts?.length > 0">
                                                    <div *ngFor="let host of resourceItem?.hosts">
                                                        <span>{{host?.name}}</span>
                                                        <span *ngIf="host?.city || host?.region  || host?.country"> (</span>
                                                        <span *ngIf="host?.city">{{host?.city}}</span><span *ngIf="host?.region">, </span><span>{{host?.region}}</span><span *ngIf="host?.country">, </span><span>{{host?.country}}</span>
                                                        <span *ngIf="host?.city || host?.region  || host?.country">)</span>
                                                        <!--Org ids-->
                                                        <div class="org-ids" *ngIf="host?.orgDisambiguatedId
">
                                                            <div class="col-md-12">   
                                                                <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                                                                <org-identifier-popover-ng2 [value]="host?.orgDisambiguatedId" [putCode]="researchResource.putCode+i" [type]="host?.disambiguationSource"></org-identifier-popover-ng2>
                                                            </div>
                                                        </div><!--org-ids-->
                                                    </div>
                                                </div>
                                                <!--Url-->
                                                <div class="col-md-12 info-detail" *ngIf="resourceItem?.url">
                                                    <strong>
                                                        <@orcid.msg
                                                        'common.url'/>
                                                    </strong>
                                                    <div>
                                                        <a href="{{resourceItem.url | urlProtocol}}" target="resourceItem.value">{{resourceItem.url}}</a>
                                                    </div>              
                                                </div> 
                                            </div>
                                        </li>
                                    </ul>
                                </div>                                       
                        </div>
                    </div>  
                </div>
                <!--SOURCE-->
                <div class="row source-line" *ngIf="group.activePutCode == researchResource.putCode">
                    <!--Edit sources-->
                    <div class="col-md-7 col-sm-7 col-xs-12" *ngIf="editSources[group.groupId]">
                        {{researchResource?.sourceName}}
                    </div>
                    <div class="col-md-3 col-sm-3 col-xs-10" *ngIf="editSources[group.groupId]">
                        <span class="glyphicon glyphicon-star" *ngIf="researchResource.putCode == group.defaultResearchResource.putCode"></span><span *ngIf="researchResource.putCode == group.defaultResearchResource.putCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                        <a (click)="makeDefault(group, researchResource, researchResource.putCode)" *ngIf="researchResource.putCode != group.defaultResearchResource.putCode && !isPublicPage">
                            <span class="glyphicon glyphicon-star-empty"></span> <@orcid.msg 'groups.common.make_preferred' />
                        </a>
                    </div>
                    <div class="col-md-2 col-sm-2 trash-source" *ngIf="editSources[group.groupId]">
                        <div *ngIf="editSources[group.groupId]">
                            <ul *ngIf="!isPublicPage" class="sources-actions">
                                <li>
                                    <a 
                                        (click)="deleteResearchResourceConfirm(researchResource)"  
                                        title="<@orcid.msg 'freemarker.btnDelete' /> {{researchResource?.title?.title?.content}}" 
                                        (mouseenter)="showTooltip(researchResource.putCode+'-deleteActiveSource')" 
                                        (mouseleave)="hideTooltip(researchResource.putCode+'-deleteActiveSource')">
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </a>

                                    <div class="popover popover-tooltip top delete-activeSource-popover" *ngIf="showElement[researchResource.putCode+'-deleteActiveSource']">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <@orcid.msg 'groups.common.delete_this_source' />
                                        </div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <!--Edit sources-->
                </div>
                <div *ngIf="group.activePutCode != researchResource.putCode" class="row source-line">
                    <div class="col-md-7 col-sm-7 col-xs-12">
                        <a (click)="group.activePutCode = researchResource.putCode">                                
                            {{researchResource?.sourceName}}
                            <#--  OBO  -->
                            <ng-container *ngIf="(researchResource.assertionOriginClientId && researchResource.assertionOriginClientId !== researchResource.sourceClientId) ||
                            (researchResource.source.assertionOriginOrcid && researchResource.source.assertionOriginOrcid !== researchResource.source.sourceOrcid)">
                            <i>${springMacroRequestContext.getMessage("public_profile.onBehalfOf")}</i> {{researchResource.assertionOriginName || researchResource.assertionOriginOrcid}}
                            </ng-container>
                        </a>
                    </div>                                        
                    <div class="col-md-3 col-sm-3 col-xs-10">
                        <span class="glyphicon glyphicon-star" *ngIf="researchResource.putCode == group.defaultResearchResource.putCode.value"></span><span *ngIf="researchResource.putCode == group.defaultResearchResource.putCode.value"> <@orcid.msg 'groups.common.preferred_source' /></span>
                        <a (click)="makeDefault(group, researchResource, researchResource.putCode); " *ngIf="researchResource.putCode != group.defaultResearchResource.putCode.value && !isPublicPage">
                            <span class="glyphicon glyphicon-star-empty"></span> <@orcid.msg 'groups.common.make_preferred' />
                        </a>
                    </div>
                    <!--Action buttons-->
                    <div class="col-md-2 col-sm-2 col-xs-2 trash-source">
                        <ul *ngIf="!isPublicPage" class="sources-actions">
                            <li>
                                <a (click)="deleteResearchResourceConfirm(researchResource)" (mouseenter)="showTooltip(researchResource.putCode+'-deleteInactiveSource')" (mouseleave)="hideTooltip(researchResource.putCode+'-deleteInactiveSource')">
                                    <span class="glyphicon glyphicon-trash" title="<@orcid.msg 'freemarker.btnDelete'/> {{researchResource?.title?.title?.content}}"></span>
                                </a>

                                <div class="popover popover-tooltip top delete-inactiveSource-popover" *ngIf="showElement[researchResource.putCode+'-deleteInactiveSource'] == true">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                       <@orcid.msg 'groups.common.delete_this_source' />
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div> 
                <div class="row source-line" *ngIf="!editSources[group.groupId]">                        
                    <div class="col-md-7 col-sm-7 col-xs-12">
                        <b><@orcid.msg 'groups.common.source'/>:</b> {{researchResource?.sourceName }}
                        <#--  OBO  -->
                        <ng-container *ngIf="(researchResource.assertionOriginClientId && researchResource.assertionOriginClientId !== researchResource.sourceClientId) ||
                        (researchResource.source.assertionOriginOrcid && researchResource.source.assertionOriginOrcid !== researchResource.source.sourceOrcid)">
                        <i>${springMacroRequestContext.getMessage("public_profile.onBehalfOf")}</i> {{researchResource.assertionOriginName || researchResource.assertionOriginOrcid}}
                        </ng-container>
                    </div>                   
                    <div class="col-md-3 col-sm-3 col-xs-9">
                        <span class="glyphicon glyphicon-star"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span> <span *ngIf="group?.researchResources?.length != 1"> (</span><a (click)="showSources(group, $event)" *ngIf="group?.researchResources?.length != 1" (mouseenter)="showTooltip(group.groupId+'-sources')" (mouseleave)="hideTooltip(group.groupId+'-sources')"><@orcid.msg 'groups.common.of'/> {{group.researchResources.length}}</a><span *ngIf="group?.researchResources?.length != 1">)</span>

                        <div class="popover popover-tooltip top sources-popover" *ngIf="showElement[group.groupId+'-sources']">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <@orcid.msg 'groups.common.sources.show_other_sources' />
                            </div>
                        </div>
                    </div>

                    <div class="col-md-2 col-sm-2 col-xs-3" *ngIf="group.activePutCode == researchResource.putCode">
                        <ul class="sources-options" >
                            <li *ngIf="!isPublicPage && !(editSources[group.groupId] || group?.researchResources?.length == 1)">
                                <a (click)="showSources(group, $event)" (mouseenter)="showTooltip(group.groupId+'-deleteGroup')" (mouseleave)="hideTooltip(group.groupId+'-deleteGroup')">
                                    <span class="glyphicon glyphicon-trash"></span>
                                </a>
                                <div class="popover popover-tooltip top delete-group-popover" *ngIf="showElement[group.groupId+'-deleteGroup']">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                       <@orcid.msg 'groups.common.delete_this_source' />
                                    </div>
                                </div>
                            </li>

                            <li *ngIf="group?.researchResources?.length == 1">
                                <a (click)="deleteResearchResourceConfirm(group.defaultResearchResource)" (mouseenter)="showTooltip(group.groupId+'-deleteSource')" (mouseleave)="hideTooltip(group.groupId+'-deleteSource')">
                                    <span class="glyphicon glyphicon-trash"></span>
                                </a>
                                <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group.groupId+'-deleteSource']">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <@orcid.msg 'groups.common.delete_this_source' />
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </li>
        </ng-container>
    </ul>
</div>