<div class="work-list-container">
    <ul class="sources-edit-list">
        <!--Edit sources-->
        <li *ngIf="editSources[group.groupId]" class="source-header" [ngClass]="{'source-active' : editSources[group.groupId] == true}" [(ngModel)]="group.researchResources">
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
                                <#if !(isPublicProfile??)>
                                <li *ngIf="bulkEditShow">
                                    <input type="checkbox" name="bulkEditSelectAll" [(ngModel)]="bulkEditMap[group.activePutCode]" class="bulk-edit-input-header">
                                </li>
                                </#if>                
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
                                <#if !(isPublicProfile??)>
                                <li>
                                    <@orcid.privacyToggle2Ng2 angularModel="group.activeVisibility"
                                    elementId="group.groupId" 
                                        questionClick="toggleClickPrivacyHelp(group.activePutCode)"
                                        clickedClassCheck="{'popover-help-container-show':privacyHelp[group.activePutCode]==true}"
                                        publicClick="researchResourceService.setGroupPrivacy(group, 'PUBLIC', $event)"
                                        limitedClick="researchResourceService.setGroupPrivacy(group, 'LIMITED', $event)"
                                        privateClick="researchResourceService.setGroupPrivacy(group, 'PRIVATE', $event)"/>
                                </li>s
                                </#if>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </li>
        <!--End edit sources-->
        <!---Research resource info-->
        <li *ngFor="let researchResource of group.researchResources; let index = index; let first = first; let last = last;">
            <div *ngIf="group.activePutCode == researchResource?.putCode || editSources[group.groupId] == true">
                <div class="row" *ngIf="group.activePutCode == researchResource?.putCode">
                    <div class="col-md-9 col-sm-9 col-xs-7">
                        <!--Proposal-->
                        <!--Title-->
                        <h3 class="workspace-title">
                            <span>{{researchResource?.proposal?.title?.title?.content}}</span>
                        </h3>
                        <div class="info-detail">
                            <!--Hosts-->
                            <div *ngIf="researchResource?.proposal?.hosts?.organization">
                                <div *ngFor="let host of researchResource?.proposal?.hosts?.organization;let i = index;trackBy:trackByIndex">
                                    <span>{{host?.name}}</span>
                                    <span *ngIf="host?.address"> (</span>
                                    <span *ngIf="host?.address?.city">{{host?.address?.city}}</span><span *ngIf="host?.address?.region">, </span><span>{{host?.address?.region}}</span><span *ngIf="host?.address?.country">, </span><span>{{host?.address?.country}}</span>
                                    <span *ngIf="host?.address">)</span>
                                </div>
                            </div>
                            <!--Dates-->
                            <div class="info-date">
                                <!--Start date-->                     
                                <span class="affiliation-date" *ngIf="group?.defaultActivity?.proposal?.startDate">
                                    <span *ngIf="researchResource?.proposal?.startDate?.year.value">{{researchResource?.proposal?.startDate?.year?.value}}</span>
                                    <span *ngIf="researchResource?.proposal?.startDate?.month?.value">-{{researchResource?.proposal?.startDate?.month?.value}}</span>
                                    <span *ngIf="researchResource?.proposal?.startDate?.day?.value">-{{researchResource?.proposal?.startDate?.day?.value}}</span>
                                    <span>&nbsp;<@orcid.msg 'workspace_affiliations.dateSeparator'/>&nbsp;</span>
                                    <span [hidden]="researchResource?.proposal?.endDate && researchResource?.proposal?.endDate.year?.value"><@orcid.msg 'workspace_affiliations.present'/></span>
                                    <span *ngIf="researchResource?.proposal?.endDate">
                                    <span *ngIf="researchResource?.proposal?.endDate?.year?.value">{{researchResource?.proposal?.endDate?.year?.value}}</span><span *ngIf="researchResource?.proposal?.endDate?.month?.value">-{{researchResource?.proposal?.endDate?.month?.value}}</span><span *ngIf="researchResource?.proposal?.endDate?.day?.value">-{{researchResource?.proposal?.endDate?.day?.value}}</span>
                                    </span>
                                </span>
                                <!--End date-->
                                <span class="affiliation-date" *ngIf="!group?.defaultActivity?.proposal?.startDate && group?.defaultActivity?.proposal?.endDate">
                                    <span *ngIf="researchResource?.proposal?.endDate?.year?.value">{{researchResource?.proposal?.endDate?.year?.value}}</span>
                                    <span *ngIf="researchResource?.proposal?.endDate?.month?.value">-{{researchResource?.proposal?.endDate?.month?.value}}</span>
                                    <span *ngIf="researchResource?.proposal?.endDate?.day?.value">-{{researchResource?.proposal?.endDate?.day?.value}}</span>
                                </span>
                            </div><!--info-date-->
                        </div><!--info-detail-->
                    </div><!--col-md-9 -->
                    <div class="col-md-3 col-sm-3 col-xs-5 workspace-toolbar">
                        <ul class="workspace-private-toolbar" *ngIf="!editSources[group.groupId]"> 

                            <!--Bulk edit checkbox-->                               
                            <#if !(isPublicProfile??)>
                            <li *ngIf="bulkEditShow" class="bulk-checkbox-item">
                                <input type="checkbox" name="bulkEditSelectAll" [(ngModel)]="bulkEditMap[researchResource.putCode]" class="bulk-edit-input ng-pristine ng-valid pull-right">       
                            </li>
                            </#if> 
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
                            <#if !(isPublicProfile??)>
                            <li>
                                <@orcid.privacyToggle2Ng2 angularModel="researchResource.visibility"
                                elementId="group.groupId" questionClick="toggleClickPrivacyHelp(group.highestVis())" clickedClassCheck="{'popover-help-container-show':privacyHelp[researchResource.putCode.value]==true}" publicClick="setGroupPrivacy(group, 'PUBLIC', $event)" limitedClick="setGroupPrivacy(group, 'LIMITED', $event)" privateClick="setGroupPrivacy(group, 'PRIVATE', $event)" />
                            </li>
                            </#if>
                        </ul>
                        <!--Inconsistent visibility warning-->  
                        <#if !(isPublicProfile??)>
                        <div *ngIf="!researchResourceService.consistentVis(group) && !editSources[group.groupId]" class="vis-issue">
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
                <!--Identifiers-->
                <div class="row" *ngIf="group.activePutCode == researchResource.putCode">
                    <div class="col-md-12 col-sm-12 bottomBuffer">
                        <ul class="id-details clearfix">
                            <li class="url-work clearfix">
                                <ul class="id-details clearfix">
                                    <li *ngFor='let extID of researchResource?.proposal?.externalIdentifiers?.externalIdentifier;let i = index;trackBy:trackByIndex | orderBy:["-relationship.value", "type.value"]' class="url-popover">
                                        <span *ngIf="researchResource?.proposal?.externalIdentifiers?.externalIdentifier[0].value?.length > 0">
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
                            <div class="col-md-12" *ngIf="researchResource?.proposal?.hosts?.organization">
                                <div *ngFor="let host of researchResource?.proposal?.hosts?.organization;let i = index;trackBy:trackByIndex">
                                    <span>{{host?.name}}</span>
                                    <span *ngIf="host?.address"> (</span>
                                    <span *ngIf="host?.address?.city">{{host?.address?.city}}</span><span *ngIf="host?.address?.region">, </span><span>{{host?.address?.region}}</span><span *ngIf="host?.address?.country">, </span><span>{{host?.address?.country}}</span>
                                    <span *ngIf="host?.address">)</span>
                                    <!--Org ids-->
                                    <div class="org-ids" *ngIf="host?.disambiguatedOrganization?.disambiguatedOrganizationIdentifier">
                                        <div class="col-md-12">   
                                            <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                                            <org-identifier-popover-ng2 [value]="host?.disambiguatedOrganization?.disambiguatedOrganizationIdentifier" [putCode]="researchResource.putCode+i" [type]="host?.disambiguatedOrganization?.disambiguationSource"></org-identifier-popover-ng2>
                                        </div>
                                        <div class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">              
                                            <div *ngIf="host?.disambiguatedOrganization?.externalIdentifiers">
                                                <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{host?.disambiguatedOrganization?.disambiguationSource}}</strong><br>
                                                <ul class="reset">
                                                    <li *ngFor="let addlExtId of host?.disambiguatedOrganization?.externalIdentifiers?.externalIdentifier">
                                                        {{addlExtId?.disambiguationSource}}:  {{addlExtId?.disambiguatedOrganizationIdentifier}}</li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div><!--org-ids-->
                                </div>
                            </div>
                            <!--Translated title-->       
                            <div class="col-md-6" *ngIf="researchResourceService.details[researchResource.putCode].proposal?.translatedTitle?.content" >
                                <div class="bottomBuffer">
                                    <strong><@orcid.msg
                                        'manual_work_form_contents.labeltranslatedtitle'/></strong> <span><i>({{researchResourceService.details[researchResource.putCode].proposal.translatedTitle.languageName}})</i></span>
                                    <div>{{researchResourceService.details[researchResource.putCode].proposal.translatedTitle.content}}</div>                
                                </div>
                            </div>
                            <div class="col-md-6" *ngIf="researchResourceService.details[researchResource.putCode].proposal?.url?.value" >
                                <div class="bottomBuffer">
                                    <strong>
                                        <@orcid.msg
                                        'common.url'/>
                                    </strong>
                                    <div>
                                        <a href="{{researchResourceService.details[researchResource.putCode].proposal.url.value | urlProtocol}}" target="url.value">{{researchResourceService.details[researchResource.putCode].proposal.url.value}}</a>
                                    </div>              
                                </div>
                            </div>          
                        </div>                                         
                        <div class="row bottomBuffer">         
                            <div class="col-md-6">
                                <div class="bottomBuffer">
                                    <strong><@orcid.msg 'groups.common.created'/></strong><br />
                                    <div>{{researchResourceService.details[researchResource.putCode].createdDate.value | date:'yyyy-MM-dd'}}</div>
                                </div>      
                            </div>    
                        </div>
                        <div class="row bottomBuffer">         
                            <div class="col-md-12" *ngIf="researchResourceService.details[researchResource.putCode].resourceItems?.length > 0" >
                                <table class="table table-bordered settings-table normal-width">
                                    <thead>
                                        <tr>
                                            <th>Resource name</th><th>Type</th><th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr *ngFor="let resourceItem of researchResourceService.details[researchResource.putCode].resourceItems; let index = index; let first = first; let last = last;">
                                            <td>{{resourceItem.resourceName}}
                                                <!--Resource details-->
                                                <div *ngIf="showResourceItemDetails[researchResource.putCode+index]" class="research-resource-details">
                                                    <!--Ext ids-->
                                                    <div *ngIf="resourceItem?.externalIdentifiers?.externalIdentifier.length > 0">
                                                        <ul class="id-details clearfix">
                                                            <li *ngFor='let extID of resourceItem?.externalIdentifiers?.externalIdentifier;let i = index;trackBy:trackByIndex | orderBy:["-relationship.value", "type.value"]' class="url-popover">
                                                                <span *ngIf="resourceItem?.externalIdentifiers?.externalIdentifier[0].value?.length > 0">
                                                                    <ext-id-popover-ng2 [extID]="extID" [putCode]="researchResource.putCode+'resourceItem'+index" [activityType]="'researchResource'"></ext-id-popover-ng2>
                                                                </span>
                                                             </li>
                                                        </ul> 
                                                    </div>
                                                    <!--Hosts-->
                                                    <div *ngIf="resourceItem?.hosts?.organization.length > 0">
                                                        <div *ngFor="let host of resourceItem?.hosts?.organization">
                                                            <span>{{host?.name}}</span>
                                                            <span *ngIf="host?.address"> (</span>
                                                            <span *ngIf="host?.address?.city">{{host?.address?.city}}</span><span *ngIf="host?.address?.region">, </span><span>{{host?.address?.region}}</span><span *ngIf="host?.address?.country">, </span><span>{{host?.address?.country}}</span>
                                                            <span *ngIf="host?.address">)</span>
                                                            <!--Org ids-->
                                                            <div class="org-ids" *ngIf="host?.disambiguatedOrganization?.disambiguatedOrganizationIdentifier">
                                                                <div class="col-md-12">   
                                                                    <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                                                                    <org-identifier-popover-ng2 [value]="host?.disambiguatedOrganization?.disambiguatedOrganizationIdentifier" [putCode]="researchResource.putCode+i" [type]="host?.disambiguatedOrganization?.disambiguationSource"></org-identifier-popover-ng2>
                                                                </div>
                                                                <div class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">              
                                                                    <div *ngIf="host?.disambiguatedOrganization?.externalIdentifiers">
                                                                        <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{host?.disambiguatedOrganization?.disambiguationSource}}</strong><br>
                                                                        <ul class="reset">
                                                                            <li *ngFor="let addlExtId of host?.disambiguatedOrganization?.externalIdentifiers?.externalIdentifier">
                                                                                {{addlExtId?.disambiguationSource}}:  {{addlExtId?.disambiguatedOrganizationIdentifier}}</li>
                                                                        </ul>
                                                                    </div>
                                                                </div>
                                                            </div><!--org-ids-->
                                                        </div>
                                                    </div>
                                                    <div *ngIf="resourceItem?.url?.value">
                                                        <strong>
                                                            <@orcid.msg
                                                            'common.url'/>
                                                        </strong>
                                                        <div>
                                                            <a href="{{resourceItem.url.value | urlProtocol}}" target="url.value">{{resourceItem.url.value}}</a>
                                                        </div>              
                                                    </div>
                                                </div>
                                            </td>
                                            <td>{{resourceItem.resourceType}}</td>
                                            <!--Show details-->
                                            <td>
                                                <div class="col-md-4 col-sm-4 col-xs-4">
                                                    <span class="pull-right"> 
                                                        <a (click)="toggleResourceItemDetails(researchResource.putCode+index,$event)" *ngIf="!showResourceItemDetails[researchResource.putCode+index]">
                                                        <span class="glyphicons expand"></span>
                                                        <span class="hidden-xs"><@orcid.msg 'common.details.show_details_lc' /></span>
                                                        </a> 
                                                        <a (click)="toggleResourceItemDetails(researchResource.putCode+index,$event);" *ngIf="showResourceItemDetails[researchResource.putCode+index]">
                                                            <span class="glyphicons collapse_top"></span>
                                                            <span class="hidden-xs"><@orcid.msg 'common.details.hide_details_lc' /></span>
                                                        </a>
                                                    </span>
                                                </div>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>                                     
                            </div>   
                        </div>
                    </div>  
                </div>

                <div class="row source-line" *ngIf="group.activePutCode == researchResource.putCode">
                    <!--Edit sources-->
                    <div class="col-md-7 col-sm-7 col-xs-12" *ngIf="editSources[group.groupId]">
                        {{researchResource?.source?.sourceName?.content}}
                    </div>
                    <div class="col-md-3 col-sm-3 col-xs-10" *ngIf="editSources[group.groupId]">
                        <div *ngIf="editSources[group.groupId]">
                            <span class="glyphicon glyphicon-check" *ngIf="researchResource.putCode == group.defaultActivity.putCode.value"></span><span *ngIf="researchResource.putCode == group.defaultActivity.putCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                            
                            <#if !(isPublicProfile??)>
                            <a (click)="makeDefault(group, researchResource.putCode)" *ngIf="researchResource.putCode != group.defaultActivity.putCode.value">
                                <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                            </a>
                            </#if>
                        </div>
                    </div>
                    <div class="col-md-2 col-sm-2 trash-source" *ngIf="editSources[group.groupId]">
                        <div *ngIf="editSources[group.groupId]">
                            <#if !(isPublicProfile??)>
                            <ul class="sources-actions">
                                <li>
                                    <a 
                                        (click)="deleteResearchResourceConfirm(researchResource)"  
                                        title="<@orcid.msg 'freemarker.btnDelete' /> {{researchResource?.proposal?.title?.title?.content}}" 
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
                            </#if>
                        </div>
                    </div>
                    <!--Edit sources-->
                </div>
                <div *ngIf="group.activePutCode != researchResource.putCode" class="row source-line">
                    <div class="col-md-7 col-sm-7 col-xs-12">
                        <a (click)="group.activePutCode = researchResource.putCode">                                
                            {{researchResource?.source?.sourceName?.content}}
                        </a>
                    </div>                                        
                    <div class="col-md-3 col-sm-3 col-xs-10">
                        <#if !(isPublicProfile??)>
                        <span class="glyphicon glyphicon-check" *ngIf="researchResource.putCode == group.defaultActivity.putCode.value"></span><span *ngIf="researchResource.putCode == group.defaultActivity.putCode.value"> <@orcid.msg 'groups.common.preferred_source' /></span>
                        <a (click)="makeDefault(group, researchResource.putCode); " *ngIf="researchResource.putCode != group.defaultActivity.putCode.value">
                            <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                        </a>
                        </#if>
                    </div>
                    <!--Action buttons-->
                    <div class="col-md-2 col-sm-2 col-xs-2 trash-source">
                        <#if !(isPublicProfile??)>
                        <ul class="sources-actions">
                            <li>
                                <a (click)="deleteResearchResourceConfirm(researchResource)" (mouseenter)="showTooltip(researchResource.putCode+'-deleteInactiveSource')" (mouseleave)="hideTooltip(researchResource.putCode+'-deleteInactiveSource')">
                                    <span class="glyphicon glyphicon-trash" title="<@orcid.msg 'freemarker.btnDelete'/> {{researchResource?.proposal?.title?.title?.content}}"></span>
                                </a>

                                <div class="popover popover-tooltip top delete-inactiveSource-popover" *ngIf="showElement[researchResource.putCode+'-deleteInactiveSource'] == true">
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
                <div class="row source-line" *ngIf="!editSources[group.groupId]">                        
                    <div class="col-md-7 col-sm-7 col-xs-12">
                        <@orcid.msg 'groups.common.source'/>: {{researchResource?.source?.sourceName?.content }}
                    </div>
                    
                    <div class="col-md-3 col-sm-3 col-xs-9">
                        <span class="glyphicon glyphicon-check"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span> <span *ngIf="group?.researchResources?.length != 1"> (</span><a (click)="showSources(group, $event)" *ngIf="group?.researchResources?.length != 1" (mouseenter)="showTooltip(group.groupId+'-sources')" (mouseleave)="hideTooltip(group.groupId+'-sources')"><@orcid.msg 'groups.common.of'/> {{group.researchResources.length}}</a><span *ngIf="group?.researchResources?.length != 1">)</span>

                        <div class="popover popover-tooltip top sources-popover" *ngIf="showElement[group.groupId+'-sources']">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <@orcid.msg 'groups.common.sources.show_other_sources' />
                            </div>
                        </div>
                    </div>

                    <div class="col-md-2 col-sm-2 col-xs-3" *ngIf="group.activePutCode == researchResource.putCode">
                        <ul class="sources-options" >
                            <#if !(isPublicProfile??)>
                            <li *ngIf="!(editSources[group.groupId] || group?.researchResources?.length == 1)">
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
                                <a (click)="deleteResearchResourceConfirm(group.defaultActivity)" (mouseenter)="showTooltip(group.groupId+'-deleteSource')" (mouseleave)="hideTooltip(group.groupId+'-deleteSource')">
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
            </div>
        </li>
    </ul>
</div>