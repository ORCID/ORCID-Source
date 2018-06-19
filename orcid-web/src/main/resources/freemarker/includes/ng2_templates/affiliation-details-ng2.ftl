<div class="row">                
    <div class="col-md-9 col-sm-9 col-xs-7">
        <h3 class="workspace-title">
            <span>{{group?.activities[group?.activePutCode]?.affiliationName.value}}</span>:
            <span>{{group?.activities[group?.activePutCode]?.city.value}}</span><span *ngIf="group?.activities[group?.activePutCode]?.region.value">, </span><span>{{group?.activities[group?.activePutCode]?.region.value}}</span>, <span>{{group?.activities[group?.activePutCode]?.countryForDisplay}}</span>                                               
        </h3>
        <div class="info-detail">
            <div class="info-date">                     
                <span class="affiliation-date" *ngIf="group?.activities[group?.activePutCode]?.startDate">
                    <span *ngIf="group?.activities[group?.activePutCode]?.startDate.year">{{group?.activities[group?.activePutCode]?.startDate.year}}</span><span *ngIf="group?.activities[group?.activePutCode]?.startDate.month">-{{group?.activities[group?.activePutCode]?.startDate.month}}</span><span *ngIf="group?.activities[group?.activePutCode]?.startDate.day">-{{group?.activities[group?.activePutCode]?.startDate.day}}</span>
                    <span><@orcid.msg 'workspace_affiliations.dateSeparator'/></span>
                    <span [hidden]="group?.activities[group?.activePutCode]?.endDate && group?.activities[group?.activePutCode]?.endDate.year"><@orcid.msg 'workspace_affiliations.present'/></span>
                    <span *ngIf="group?.activities[group?.activePutCode]?.endDate">
                        <span *ngIf="group?.activities[group?.activePutCode]?.endDate.year">{{group?.activities[group?.activePutCode]?.endDate.year}}</span><span *ngIf="group?.activities[group?.activePutCode]?.endDate.month">-{{group?.activities[group?.activePutCode]?.endDate.month}}</span><span *ngIf="group?.activities[group?.activePutCode]?.endDate.day">-{{group?.activities[group?.activePutCode]?.endDate.day}}</span>
                    </span>
                </span>
                <span class="affiliation-date" *ngIf="!group?.activities[group?.activePutCode]?.startDate && group?.activities[group?.activePutCode]?.endDate">
                     <span  *ngIf="group?.activities[group?.activePutCode]?.endDate.year">{{group?.activities[group?.activePutCode]?.endDate.year}}</span><span *ngIf="group?.activities[group?.activePutCode]?.endDate.month">-{{group?.activities[group?.activePutCode]?.endDate.month}}</span><span *ngIf="group?.activities[group?.activePutCode]?.endDate.day">-{{group?.activities[group?.activePutCode]?.endDate.day}}</span>
                </span>
                <span *ngIf="(group?.activities[group?.activePutCode]?.startDate || group?.activities[group?.activePutCode]?.endDate) && (group?.activities[group?.activePutCode]?.roleTitle.value || group?.activities[group?.activePutCode]?.departmentName.value)"> | </span> <span *ngIf="group?.activities[group?.activePutCode]?.roleTitle.value">{{group?.activities[group?.activePutCode]?.roleTitle.value}}</span>        
                <span *ngIf="group?.activities[group?.activePutCode]?.departmentName.value">
                <span *ngIf="group?.activities[group?.activePutCode]?.roleTitle.value && !printView">&nbsp;</span>(<span>{{group?.activities[group?.activePutCode]?.departmentName.value}}</span>)
                </span>
            </div><!--info-date-->
        </div><!--info-detail-->
    </div><!--col-md-9 -->
    <div class="col-md-3 col-sm-3 col-xs-5 padding-left-fix">          
        <div class="workspace-toolbar">         
            <ul class="workspace-private-toolbar"> 
                    <li *ngIf="orgIdsFeatureEnabled" class="works-details">
                        <a (click)="showDetailsMouseClick(group,$event)" (mouseenter)="showTooltip(group?.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group?.groupId+'-showHideDetails')">
                            <span [ngClass]="(moreInfo[group?.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                            </span>
                        </a>
                        <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group?.groupId+'-showHideDetails']">
                             <div class="arrow"></div>
                            <div class="popover-content">   
                                <span *ngIf="moreInfo[group?.groupId] == false || moreInfo[group?.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>   
                                <span *ngIf="moreInfo[group?.groupId]"><@orcid.msg 'common.details.hide_details'/></span>
                            </div>
                        </div>
                    </li>
                <#if !(isPublicProfile??)> 
                    <li>
                        <@orcid.privacyToggle2Ng2  angularModel="group?.activities[group?.activePutCode]?.visibility.visibility"
                            elementId="group?.activities[group?.activePutCode]?.putCode.value"
                            questionClick="toggleClickPrivacyHelp(group?.activities[group?.activePutCode]?.putCode.value)"
                            clickedClassCheck="{'popover-help-container-show':privacyHelp[group?.activities[group?.activePutCode]?.putCode.value]==true}" 
                            publicClick="setPrivacy(group?.activities[group?.activePutCode], 'PUBLIC', $event)" 
                            limitedClick="setPrivacy(group?.activities[group?.activePutCode], 'LIMITED', $event)" 
                            privateClick="setPrivacy(group?.activities[group?.activePutCode], 'PRIVATE', $event)" />
                    </li>
                </#if>
            </ul>
        </div><!--workspace-toolbar-->
    </div><!--col-md-3-->  
</div><!--row1-->
<div class="row" *ngIf="group?.activePutCode == group?.activities[group?.activePutCode]?.putCode.value">
    <div class="col-md-12 col-sm-12 bottomBuffer">
        <ul class="id-details">
            <li class="url-work">
                <ul class="id-details">
                    <li *ngFor='let extID of group?.activities[group?.activePutCode]?.affiliationExternalIdentifiers;let i = index;trackBy:trackByIndex | orderBy:["-relationship.value", "type.value"]' class="url-popover">
                        <span *ngIf="group?.activities[group?.activePutCode]?.affiliationExternalIdentifiers[0]?.value?.value?.length > 0">
                            <ext-id-popover-ng2 [extID]="extID" [putCode]="group?.activities[group?.activePutCode]?.putCode.value+i" [activityType]="'affiliation'"></ext-id-popover-ng2>
                        </span>
                    </li>
                </ul>                                   
            </li>
        </ul>
    </div>
</div><!--row2--> 
<div *ngIf="orgIdsFeatureEnabled">
    <div class="more-info content" *ngIf="moreInfo[group?.groupId]">
        <div class="row bottomBuffer">
            <div class="col-md-12"></div>
        </div>
        <span class="dotted-bar"></span>    
        <div class="row">
            <div class="org-ids" *ngIf="group?.activities[group?.activePutCode]?.orgDisambiguatedId?.value">
                <div class="col-md-12">   
                    <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                    <org-identifier-popover-ng2 [value]="group?.activities[group?.activePutCode]?.disambiguatedAffiliationSourceId.value" [putCode]="group?.activities[group?.activePutCode]?.putCode.value" [type]="group?.activities[group?.activePutCode]?.disambiguationSource.value"></org-identifier-popover-ng2>
                </div>
                <div class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">
                    <span *ngIf="group?.activities[group?.activePutCode]?.orgDisambiguatedName">{{group?.activities[group?.activePutCode]?.orgDisambiguatedName}}</span><span *ngIf="group?.activities[group?.activePutCode]?.orgDisambiguatedCity || group?.activities[group?.activePutCode]?.orgDisambiguatedRegion || group?.activities[group?.activePutCode]?.orgDisambiguatedCountry">: </span><span *ngIf="group?.activities[group?.activePutCode]?.orgDisambiguatedCity">{{group?.activities[group?.activePutCode]?.orgDisambiguatedCity}}</span><span *ngIf="group?.activities[group?.activePutCode]?.orgDisambiguatedCity && group?.activities[group?.activePutCode]?.orgDisambiguatedRegion">, </span><span *ngIf="group?.activities[group?.activePutCode]?.orgDisambiguatedRegion">{{group?.activities[group?.activePutCode]?.orgDisambiguatedRegion}}</span><span *ngIf="group?.activities[group?.activePutCode]?.orgDisambiguatedCountry && (group?.activities[group?.activePutCode]?.orgDisambiguatedCity || group?.activities[group?.activePutCode]?.orgDisambiguatedRegion)">, </span><span *ngIf="group?.activities[group?.activePutCode]?.orgDisambiguatedCountry">{{group?.activities[group?.activePutCode]?.orgDisambiguatedCountry}}</span>
                    <span *ngIf="group?.activities[group?.activePutCode]?.orgDisambiguatedUrl"><br>
                    <a href="{{group?.activities[group?.activePutCode]?.orgDisambiguatedUrl}}" target="orgDisambiguatedUrl"><span>{{group?.activities[group?.activePutCode]?.orgDisambiguatedUrl}}</span></a>
                    </span>                                            
                    <div *ngIf="group?.activities[group?.activePutCode]?.orgDisambiguatedExternalIdentifiers">
                        <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{group?.activities[group?.activePutCode]?.disambiguationSource.value}}</strong><br>
                        <ul class="reset">
                            <li *ngFor="let orgDisambiguatedExternalIdentifier of group?.activities[group?.activePutCode]?.orgDisambiguatedExternalIdentifiers">
                                {{orgDisambiguatedExternalIdentifier.identifierType}}:  <span *ngIf="orgDisambiguatedExternalIdentifier.preferred">{{orgDisambiguatedExternalIdentifier.preferred}} <@orcid.msg 'workspace_affiliations.external_ids_preferred'/>, </span> <span *ngIf="orgDisambiguatedExternalIdentifier.all"><span *ngFor="let orgDisambiguatedExternalIdentifierAll of orgDisambiguatedExternalIdentifier.all">{{orgDisambiguatedExternalIdentifierAll}}{{$last ? '' : ', '}}</span></span></li>
                        </ul>
                    </div>
                </div>
            </div><!--org-ids-->
            <div class="col-md-6" *ngIf="group?.activities[group?.activePutCode]?.url.value">
                <div class="bottomBuffer">
                    <strong><@orcid.msg 'common.url'/></strong><br> 
                    <a href="{{group?.activities[group?.activePutCode]?.url.value}}" target="affiliation.url.value">{{group?.activities[group?.activePutCode]?.url.value}}</a>
                </div>
            </div>  
            <div class="col-md-12">
                <div class="bottomBuffer">
                    <strong><@orcid.msg 'groups.common.created'/></strong><br> 
                    <span>{{group?.activities[group?.activePutCode]?.createdDate | ajaxFormDateToISO8601}}</span>
                </div>
            </div>  
        </div><!--row3-->
    </div><!--more-info-->
</div><!--orgIdsFeatureEnabled-->
<div class="row source-line">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <div class="sources-container-header">          
            <div class="row">
                <div class="col-md-7 col-sm-7 col-xs-12">
                    <@orcid.msg 'groups.common.source'/>: {{(group?.activities[group?.activePutCode]?.sourceName == null || group?.activities[group?.activePutCode]?.sourceName == '') ? group?.activities[group?.activePutCode]?.source : group?.activities[group?.activePutCode]?.sourceName}}    
                </div>                            
                <div *ngIf="!orgIdsFeatureEnabled" class="col-md-3 col-sm-3 col-xs-6">
                    <@orcid.msg 'groups.common.created'/>: <span>{{group?.activities[group?.activePutCode]?.createdDate | ajaxFormDateToISO8601}}</span>
                </div>                                                   
                <div class="col-md-2 col-sm-2 col-xs-6 pull-right">
                    <ul class="sources-options">
                        <#if !(isPublicProfile??)>
                        <li *ngIf="group?.activities[group?.activePutCode]?.source == '${effectiveUserOrcid}'">
                            <a (click)="openEditAffiliation(group?.activities[group?.activePutCode])" (mouseenter)="showTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-edit')" (mouseleave)="hideTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-edit')">
                                <span class="glyphicon glyphicon-pencil"></span>
                            </a>
                            <div class="popover popover-tooltip top edit-source-popover" *ngIf="showElement[group?.activities[group?.activePutCode]?.putCode.value+'-edit']"> 
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <span ><@orcid.msg 'groups.common.edit_my'/></span>
                                </div>                
                            </div>  
                        </li>   
                        <li>
                            <a id="delete-affiliation_{{group?.activities[group?.activePutCode]?.putCode.value}}" href="" (click)="deleteAffiliation(group?.activities[group?.activePutCode])" (mouseenter)="showTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-delete')" (mouseleave)="hideTooltip(group?.activities[group?.activePutCode]?.putCode.value+'-delete')" class="glyphicon glyphicon-trash"></a>
                            <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group?.activities[group?.activePutCode]?.putCode.value+'-delete']"> 
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
    </div>
</div>