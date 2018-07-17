<div class="row">                
    <div class="col-md-9 col-sm-9 col-xs-7">
        <!--Proposal-->
        <!--Title-->
        <h3 class="workspace-title">
            <span>{{group?.defaultActivity?.proposal?.title?.title?.content}}</span>
        </h3>
        <div class="info-detail">
            <!--Hosts-->
            <div *ngIf="group?.defaultActivity?.proposal?.hosts?.organization">
                <div *ngFor="let host of group?.defaultActivity?.proposal?.hosts?.organization;let i = index;trackBy:trackByIndex">
                    <span>{{host?.name}}</span>
                    <span *ngIf="host?.address"> (</span>
                    <span *ngIf="host?.address?.city">{{host?.address?.city}}</span><span *ngIf="host?.address?.region">, </span><span>{{host?.address?.region}}</span><span *ngIf="host?.address?.country">, </span><span>{{host?.address?.country}}</span>
                    <span *ngIf="host?.address">)</span>
                </div>
            </div>
            <!--Dates-->
            <div class="info-date">                     
                <span class="affiliation-date" *ngIf="group?.defaultActivity?.proposal?.startDate">
                    <span *ngIf="group?.defaultActivity?.proposal?.startDate?.year.value">{{group?.defaultActivity?.proposal?.startDate?.year?.value}}</span><span *ngIf="group?.defaultActivity?.proposal?.startDate?.month?.value">-{{group?.defaultActivity?.proposal?.startDate?.month?.value}}</span><span *ngIf="group?.defaultActivity?.proposal?.startDate?.day?.value">-{{group?.defaultActivity?.proposal?.startDate?.day?.value}}</span>
                    <span>&nbsp;<@orcid.msg 'workspace_affiliations.dateSeparator'/>&nbsp;</span>
                    <span [hidden]="group?.defaultActivity?.proposal?.endDate && group?.defaultActivity?.proposal?.endDate.year?.value"><@orcid.msg 'workspace_affiliations.present'/></span>
                    <span *ngIf="group?.defaultActivity?.proposal?.endDate">
                        <span *ngIf="group?.defaultActivity?.proposal?.endDate?.year?.value">{{group?.defaultActivity?.proposal?.endDate?.year?.value}}</span><span *ngIf="group?.defaultActivity?.proposal?.endDate?.month?.value">-{{group?.defaultActivity?.proposal?.endDate?.month?.value}}</span><span *ngIf="group?.defaultActivity?.proposal?.endDate?.day?.value">-{{group?.defaultActivity?.proposal?.endDate?.day?.value}}</span>
                    </span>
                </span>
                <span class="affiliation-date" *ngIf="!group?.defaultActivity?.proposal?.startDate && group?.defaultActivity?.proposal?.endDate">
                     <span *ngIf="group?.defaultActivity?.proposal?.endDate?.year?.value">{{group?.defaultActivity?.proposal?.endDate?.year?.value}}</span><span *ngIf="group?.defaultActivity?.proposal?.endDate?.month?.value">-{{group?.defaultActivity?.proposal?.endDate?.month?.value}}</span><span *ngIf="group?.defaultActivity?.proposal?.endDate?.day?.value">-{{group?.defaultActivity?.proposal?.endDate?.day?.value}}</span>
                </span>
            </div><!--info-date-->
        </div><!--info-detail-->
    </div><!--col-md-9 --> 
    <div class="col-md-3 col-sm-3 col-xs-5 padding-left-fix">          
        <div class="workspace-toolbar">         
            <ul class="workspace-private-toolbar"> 
                    <li class="works-details">
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
                        <@orcid.privacyToggle2Ng2  angularModel="group?.defaultActivity?.visibility"
                            elementId="group?.defaultActivity?.putCode"
                            questionClick="toggleClickPrivacyHelp(group?.defaultActivity?.putCode)"
                            clickedClassCheck="{'popover-help-container-show':privacyHelp[group?.defaultActivity?.putCode]==true}" 
                            publicClick="setPrivacy(group?.defaultActivity, 'PUBLIC', $event)" 
                            limitedClick="setPrivacy(group?.defaultActivity, 'LIMITED', $event)" 
                            privateClick="setPrivacy(group?.defaultActivity, 'PRIVATE', $event)" />
                    </li>
                </#if>
            </ul>
        </div><!--workspace-toolbar-->
    </div><!--col-md-3-->   
</div><!--ROW 1-->
<!--MORE DETAILS-->
<!--SOURCE-->
<div class="row source-line">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <div class="sources-container-header">          
            <div class="row">
                <div class="col-md-7 col-sm-7 col-xs-12">
                    <@orcid.msg 'groups.common.source'/>: {{(group?.defaultActivity?.source?.sourceName?.content == null || group?.defaultActivity?.source?.sourceName?.content == '') ? group?.defaultActivity?.source?.sourceName?.content : group?.defaultActivity?.source?.sourceName?.content}}    
                </div>                                                                             
                <div class="col-md-2 col-sm-2 col-xs-6 pull-right">
                    <ul class="sources-options">
                        <#if !(isPublicProfile??)> 
                        <li>
                            <a id="delete-affiliation_{{group?.defaultActivity?.putCode?.value}}" href="" (click)="deleteAffiliation(group?.defaultActivity)" (mouseenter)="showTooltip(group?.defaultActivity?.putCode.value+'-delete')" (mouseleave)="hideTooltip(group?.defaultActivity?.putCode.value+'-delete')" class="glyphicon glyphicon-trash"></a>
                            <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group?.defaultActivity?.putCode.value+'-delete']"> 
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
</div><!--End source line--> 