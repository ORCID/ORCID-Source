<div class="row">                
    <div class="col-md-9 col-sm-9 col-xs-7">
        <h3 class="workspace-title">
            <span>{{group?.defaultAffiliation?.affiliationName?.value}}</span>:
            <span>{{group?.defaultAffiliation?.city.value}}</span><span *ngIf="group?.defaultAffiliation?.region?.value">, </span><span>{{group?.defaultAffiliation?.region?.value}}</span><span *ngIf="group?.defaultAffiliation?.countryForDisplay">, </span><span>{{group?.defaultAffiliation?.countryForDisplay}}</span>                                               
        </h3>
        <div class="info-detail">
            <div class="info-date">                     
                <span class="affiliation-date" *ngIf="group?.defaultAffiliation?.startDate">
                    <span *ngIf="group?.defaultAffiliation?.startDate.year">{{group?.defaultAffiliation?.startDate.year}}</span><span *ngIf="group?.defaultAffiliation?.startDate.month">-{{group?.defaultAffiliation?.startDate.month}}</span><span *ngIf="group?.defaultAffiliation?.startDate.day">-{{group?.defaultAffiliation?.startDate.day}}</span>
                    <span>&nbsp;<@orcid.msg 'workspace_affiliations.dateSeparator'/>&nbsp;</span>
                    <span [hidden]="group?.defaultAffiliation?.endDate && group?.defaultAffiliation?.endDate.year"><@orcid.msg 'workspace_affiliations.present'/></span>
                    <span *ngIf="group?.defaultAffiliation?.endDate">
                        <span *ngIf="group?.defaultAffiliation?.endDate.year">{{group?.defaultAffiliation?.endDate.year}}</span><span *ngIf="group?.defaultAffiliation?.endDate.month">-{{group?.defaultAffiliation?.endDate.month}}</span><span *ngIf="group?.defaultAffiliation?.endDate.day">-{{group?.defaultAffiliation?.endDate.day}}</span>
                    </span>
                </span>
                <span class="affiliation-date" *ngIf="!group?.defaultAffiliation?.startDate && group?.defaultAffiliation?.endDate">
                     <span *ngIf="group?.defaultAffiliation?.endDate.year">{{group?.defaultAffiliation?.endDate.year}}</span><span *ngIf="group?.defaultAffiliation?.endDate.month">-{{group?.defaultAffiliation?.endDate.month}}</span><span *ngIf="group?.defaultAffiliation?.endDate.day">-{{group?.defaultAffiliation?.endDate.day}}</span>
                </span>
                <span *ngIf="(group?.defaultAffiliation?.startDate || group?.defaultAffiliation?.endDate) && (group?.defaultAffiliation?.roleTitle?.value || group?.defaultAffiliation?.departmentName?.value)"> | </span> <span *ngIf="group?.defaultAffiliation?.roleTitle?.value">{{group?.defaultAffiliation?.roleTitle?.value}}</span>        
                <span *ngIf="group?.defaultAffiliation?.departmentName?.value">
                <span *ngIf="group?.defaultAffiliation?.roleTitle?.value && !printView">&nbsp;</span>(<span>{{group?.defaultAffiliation?.departmentName.value}}</span>)
                </span>
            </div><!--info-date-->
        </div><!--info-detail-->
    </div><!--col-md-9 -->
    <div class="col-md-3 col-sm-3 col-xs-5 padding-left-fix">          
        <div class="workspace-toolbar">         
            <ul class="workspace-private-toolbar"> 
                    <li *ngIf="orgIdsFeatureEnabled" class="works-details">
                        <a (click)="showDetailsMouseClick(group,$event)" (mouseenter)="showTooltip(group?.activePutCode+'-showHideDetails')" (mouseleave)="hideTooltip(group?.activePutCode+'-showHideDetails')">
                            <span [ngClass]="(moreInfo[group?.activePutCode] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                            </span>
                        </a>
                        <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group?.activePutCode+'-showHideDetails']">
                             <div class="arrow"></div>
                            <div class="popover-content">   
                                <span *ngIf="moreInfo[group?.activePutCode] == false || moreInfo[group?.activePutCode] == null"><@orcid.msg 'common.details.show_details'/></span>   
                                <span *ngIf="moreInfo[group?.activePutCode]"><@orcid.msg 'common.details.hide_details'/></span>
                            </div>
                        </div>
                    </li>
                <#if !(isPublicProfile??)> 
                    <li>
                        <@orcid.privacyToggle2Ng2  angularModel="group?.defaultAffiliation?.visibility.visibility"
                            elementId="group?.defaultAffiliation?.putCode.value"
                            questionClick="toggleClickPrivacyHelp(group?.defaultAffiliation?.putCode.value)"
                            clickedClassCheck="{'popover-help-container-show':privacyHelp[group?.defaultAffiliation?.putCode.value]==true}" 
                            publicClick="setPrivacy(group?.defaultAffiliation, 'PUBLIC', $event)" 
                            limitedClick="setPrivacy(group?.defaultAffiliation, 'LIMITED', $event)" 
                            privateClick="setPrivacy(group?.defaultAffiliation, 'PRIVATE', $event)" />
                    </li>
                </#if>
            </ul>
        </div><!--workspace-toolbar-->
    </div><!--col-md-3-->  
</div><!--row1-->
<div class="row" *ngIf="group?.activePutCode == group?.defaultAffiliation?.putCode?.value">
    <div class="col-md-12 col-sm-12 bottomBuffer">
        <ul *ngIf='group?.defaultAffiliation?.affiliationExternalIdentifiers' class="id-details">
            <li class="url-work">
                <ul class="id-details">
                    <li *ngFor='let extID of group?.defaultAffiliation?.affiliationExternalIdentifiers;let i = index;trackBy:trackByIndex | orderBy:["-relationship.value", "externalIdentifierType.value"]' class="url-popover">
                        <span *ngIf="group?.defaultAffiliation?.affiliationExternalIdentifiers[0]?.externalIdentifierId?.value?.length > 0">
                            <ext-id-popover-ng2 [extID]="extID" [putCode]="group?.defaultAffiliation?.putCode?.value+i" [activityType]="'affiliation'"></ext-id-popover-ng2>
                        </span>
                    </li>
                </ul>                                   
            </li>
        </ul>
    </div>
</div><!--row2--> 
<div *ngIf="orgIdsFeatureEnabled">
    <div class="more-info content" *ngIf="moreInfo[group?.activePutCode]">
        <div class="row bottomBuffer">
            <div class="col-md-12"></div>
        </div>
        <span class="dotted-bar"></span>    
        <div class="row">
            <div class="org-ids" *ngIf="group?.defaultAffiliation?.orgDisambiguatedId?.value">
                <div class="col-md-12">   
                    <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                    <org-identifier-popover-ng2 [value]="group?.defaultAffiliation?.disambiguatedAffiliationSourceId?.value" [putCode]="group?.defaultAffiliation?.putCode?.value" [type]="group?.defaultAffiliation?.disambiguationSource?.value"></org-identifier-popover-ng2>
                </div>
                <div class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">
                    <span *ngIf="group?.defaultAffiliation?.orgDisambiguatedName">{{group?.defaultAffiliation?.orgDisambiguatedName}}</span><span *ngIf="group?.defaultAffiliation?.orgDisambiguatedCity || group?.defaultAffiliation?.orgDisambiguatedRegion || group?.defaultAffiliation?.orgDisambiguatedCountry">: </span><span *ngIf="group?.defaultAffiliation?.orgDisambiguatedCity">{{group?.defaultAffiliation?.orgDisambiguatedCity}}</span><span *ngIf="group?.defaultAffiliation?.orgDisambiguatedCity && group?.defaultAffiliation?.orgDisambiguatedRegion">, </span><span *ngIf="group?.defaultAffiliation?.orgDisambiguatedRegion">{{group?.defaultAffiliation?.orgDisambiguatedRegion}}</span><span *ngIf="group?.defaultAffiliation?.orgDisambiguatedCountry && (group?.defaultAffiliation?.orgDisambiguatedCity || group?.defaultAffiliation?.orgDisambiguatedRegion)">, </span><span *ngIf="group?.defaultAffiliation?.orgDisambiguatedCountry">{{group?.defaultAffiliation?.orgDisambiguatedCountry}}</span>
                    <span *ngIf="group?.defaultAffiliation?.orgDisambiguatedUrl"><br>
                    <a href="{{group?.defaultAffiliation?.orgDisambiguatedUrl}}" target="orgDisambiguatedUrl"><span>{{group?.defaultAffiliation?.orgDisambiguatedUrl}}</span></a>
                    </span>                                            
                    <div *ngIf="group?.defaultAffiliation?.orgDisambiguatedExternalIdentifiers">
                        <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{group?.defaultAffiliation?.disambiguationSource.value}}</strong><br>
                        <ul class="reset">
                            <li *ngFor="let orgDisambiguatedExternalIdentifier of group?.defaultAffiliation?.orgDisambiguatedExternalIdentifiers">
                                {{orgDisambiguatedExternalIdentifier.identifierType}}: <span *ngIf="orgDisambiguatedExternalIdentifier.preferred">{{orgDisambiguatedExternalIdentifier.preferred}} <@orcid.msg 'workspace_affiliations.external_ids_preferred'/></span> 
                                <!-- Put the ',' only if there is more than one ext id or if the only one is not the same as the preferred one -->
                                <span *ngIf="orgDisambiguatedExternalIdentifier.all && (orgDisambiguatedExternalIdentifier.all.length > 1 || (orgDisambiguatedExternalIdentifier.preferred && (orgDisambiguatedExternalIdentifier.all[0] != orgDisambiguatedExternalIdentifier.preferred)))">,</span>   
                                <span *ngIf="orgDisambiguatedExternalIdentifier.all">
                                    <span *ngFor="let orgDisambiguatedExternalIdentifierAll of orgDisambiguatedExternalIdentifier.all;let last = last">
                                        <span *ngIf="orgDisambiguatedExternalIdentifierAll != orgDisambiguatedExternalIdentifier.preferred">{{orgDisambiguatedExternalIdentifierAll}}{{last ? '' : ', '}}</span>                                        
                                    </span>
                                </span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div><!--org-ids-->
            <div class="col-md-6" *ngIf="group?.defaultAffiliation?.url?.value">
                <div class="bottomBuffer">
                    <strong><@orcid.msg 'common.url'/></strong><br> 
                    <a href="{{group?.defaultAffiliation?.url?.value}}" target="affiliation.url.value">{{group?.defaultAffiliation?.url.value}}</a>
                </div>
            </div>  
            <div class="col-md-12">
                <div class="bottomBuffer">
                    <strong><@orcid.msg 'groups.common.created'/></strong><br> 
                    <span>{{group?.defaultAffiliation?.createdDate | ajaxFormDateToISO8601}}</span>
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
                    <@orcid.msg 'groups.common.source'/>: {{(group?.defaultAffiliation?.sourceName == null || group?.defaultAffiliation?.sourceName == '') ? group?.defaultAffiliation?.source : group?.defaultAffiliation?.sourceName}}    
                </div>                            
                <div *ngIf="!orgIdsFeatureEnabled" class="col-md-3 col-sm-3 col-xs-6">
                    <@orcid.msg 'groups.common.created'/>: <span>{{group?.defaultAffiliation?.createdDate | ajaxFormDateToISO8601}}</span>
                </div>                                                   
                <div class="col-md-2 col-sm-2 col-xs-6 pull-right">
                    <ul class="sources-options">
                        <#if !(isPublicProfile??)>
                        <li *ngIf="group?.defaultAffiliation?.source == '${effectiveUserOrcid}'">
                            <a (click)="openEditAffiliation(group?.defaultAffiliation)" (mouseenter)="showTooltip(group?.defaultAffiliation?.putCode.value+'-edit')" (mouseleave)="hideTooltip(group?.defaultAffiliation?.putCode.value+'-edit')">
                                <span class="glyphicon glyphicon-pencil"></span>
                            </a>
                            <div class="popover popover-tooltip top edit-source-popover" *ngIf="showElement[group?.defaultAffiliation?.putCode.value+'-edit']"> 
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <span ><@orcid.msg 'groups.common.edit_my'/></span>
                                </div>                
                            </div>  
                        </li>   
                        <li>
                            <a id="delete-affiliation_{{group?.defaultAffiliation?.putCode.value}}" href="" (click)="deleteAffiliation(group?.defaultAffiliation)" (mouseenter)="showTooltip(group?.defaultAffiliation?.putCode.value+'-delete')" (mouseleave)="hideTooltip(group?.defaultAffiliation?.putCode.value+'-delete')" class="glyphicon glyphicon-trash"></a>
                            <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group?.defaultAffiliation?.putCode.value+'-delete']"> 
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