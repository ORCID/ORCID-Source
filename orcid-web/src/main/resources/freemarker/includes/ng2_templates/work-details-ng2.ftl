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
                                    elementId="group.activePutCode" 
                                        questionClick="toggleClickPrivacyHelp(group.activePutCode)"
                                        clickedClassCheck="{'popover-help-container-show':privacyHelp[group.activePutCode]==true}"
                                        publicClick="worksService.setGroupPrivacy(group.activePutCode, 'PUBLIC', $event)"
                                        limitedClick="worksService.setGroupPrivacy(group.activePutCode, 'LIMITED', $event)"
                                        privateClick="worksService.setGroupPrivacy(group.activePutCode, 'PRIVATE', $event)"/>
                                </li>
                                </#if>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </li>
        <!--End edit sources-->
        <!---Work info-->
        <li *ngFor="let work of group.works; let index = index; let first = first; let last = last;"><!--  orcid-put-code="{{work.putCode.value}}"*** -->
            <div *ngIf="group.activePutCode == work.putCode.value || editSources[group.groupId] == true">

                <div class="row" *ngIf="group.activePutCode == work.putCode?.value">
                    <div class="col-md-9 col-sm-9 col-xs-7">
                        <h3 class="workspace-title">
                            <span>{{work.title.value}}</span>
                            <span class="journaltitle" *ngIf="work.journalTitle?.value">{{work.journalTitle.value}}</span>                                
                        </h3>                                                        
                        <div class="info-detail">
                            <span *ngIf="work.publicationDate?.year">{{work.publicationDate.year}}</span><span *ngIf="work.publicationDate?.month">-{{work.publicationDate.month}}</span><span *ngIf="work.publicationDate?.day">-</span><span *ngIf="work.publicationDate?.day">{{work.publicationDate.day}}</span><span *ngIf="work.publicationDate?.year"> | </span> <span class="capitalize">{{work.workType.value}}</span>
                        </div>
                    </div>


                    <div class="col-md-3 col-sm-3 col-xs-5 workspace-toolbar">
                        <ul class="workspace-private-toolbar" *ngIf="!editSources[group.groupId]"> 

                            <!--Bulk edit checkbox-->                               
                            <#if !(isPublicProfile??)>
                            <li *ngIf="bulkEditShow" class="bulk-checkbox-item">
                                <input type="checkbox" name="bulkEditSelectAll" [(ngModel)]="bulkEditMap[work.putCode.value]" class="bulk-edit-input ng-pristine ng-valid pull-right">       
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
                                <@orcid.privacyToggle2Ng2 angularModel="work.visibility.visibility"
                                elementId="group.activePutCode" questionClick="toggleClickPrivacyHelp(group.highestVis())" clickedClassCheck="{'popover-help-container-show':privacyHelp[work.putCode.value]==true}" publicClick="setGroupPrivacy(work.putCode.value, 'PUBLIC', $event)" limitedClick="setGroupPrivacy(work.putCode.value, 'LIMITED', $event)" privateClick="setGroupPrivacy(work.putCode.value, 'PRIVATE', $event)" />
                            </li>
                            </#if>
                        </ul>
                        <!--Inconsistent visibility warning-->  
                        <#if !(isPublicProfile??)>
                        <div *ngIf="!worksService.consistentVis(group) && !editSources[group.groupId]" class="vis-issue">
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
                <div class="row" *ngIf="group.activePutCode == work.putCode.value">
                    <div class="col-md-12 col-sm-12 bottomBuffer">
                        <ul class="id-details clearfix">
                            <li class="url-work clearfix">
                                <ul class="id-details clearfix">
                                    <li *ngFor='let extID of work?.workExternalIdentifiers;let i = index;trackBy:trackByIndex | orderBy:["relationship.value", "type.value"]' class="url-popover">
                                        <span *ngIf="work?.workExternalIdentifiers[0]?.externalIdentifierId?.value?.length > 0">
                                            <ext-id-popover-ng2 [extID]="extID" [putCode]="work.putCode.value+i" [activityType]="'work'"></ext-id-popover-ng2>
                                        </span>
                                     </li>
                                </ul>                                   
                            </li>
                            <li *ngIf="work.url?.value" class="url-popover url-work">
                                <@orcid.msg 'common.url' />: <a href="{{work.url.value | urlProtocol}}" (mouseenter)="showURLPopOver(work.putCode.value)" (mouseleave)="hideURLPopOver(work.putCode.value)" [ngClass]="{'truncate-anchor' : moreInfo[group.groupId] == false || moreInfo[group.groupId] == undefined}" target="work.url.value">{{work.url.value}}</a>
                                <div class="popover-pos">          
                                    <div class="popover-help-container">
                                        <div class="popover bottom" [ngClass]="{'block' : displayURLPopOver[work.putCode.value] == true}">
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

                <div class="more-info" *ngIf="moreInfo[group?.groupId] && group.activePutCode == work.putCode.value">
                    <div id="ajax-loader" *ngIf="worksService.details[work.putCode.value] == undefined">
                        <span id="ajax-loader"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x4 green"></i></span>
                    </div>
                    
                    <div class="content" *ngIf="worksService.details[work.putCode.value] != undefined">  
                        
                        <span class="dotted-bar"></span>
                        <div class="row">       
                            <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].translatedTitle?.content" >
                                <div class="bottomBuffer">
                                    <strong><@orcid.msg
                                        'manual_work_form_contents.labeltranslatedtitle'/></strong> <span><i>({{worksService.details[work.putCode.value].translatedTitle.languageName}})</i></span>
                                    <div>{{worksService.details[work.putCode.value].translatedTitle.content}}</div>                
                                </div>
                            </div>
                            <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].languageCode?.value" >
                                <div class="bottomBuffer">                  
                                    <strong><@orcid.msg
                                        'manual_work_form_contents.labellanguage'/></strong>
                                    <div>{{worksService.details[work.putCode.value].languageName.value}}</div>                  
                                </div>
                            </div>
                            <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].subtitle?.value" >
                                <div class="bottomBuffer">
                                    <strong> <@orcid.msg 'manual_work_form_contents.labelsubtitle'/> </strong>
                                    <div>{{worksService.details[work.putCode.value].subtitle.value}}</div>
                                </div>
                            </div>
                            
                            <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].url?.value" >

                                <div class="bottomBuffer">
                                    <strong>
                                        <@orcid.msg
                                        'common.url'/>
                                    </strong>
                                    <div>
                                        <a href="{{worksService.details[work.putCode.value].url.value | urlProtocol}}" target="url.value">{{worksService.details[work.putCode.value].url.value}}</a>
                                    </div>              
                                </div>
                            </div>          
                        </div>                  
                        <div class="row bottomBuffer" *ngIf="worksService.details[work.putCode.value].citation?.citation?.value" >
                            <div class="col-md-12">             
                                <strong><@orcid.msg 'manual_work_form_contents.labelcitation'/></strong> <span> (<span *ngIf="worksService.details[work.putCode.value].citation?.citationType?.value" ><i>{{worksService.details[work.putCode.value].citation.citationType.value}}</i></span>) 
                                </span>
                                <span *ngIf="showBibtex[work.putCode.value] && worksService.details[work.putCode.value].citation?.citationType?.value == 'bibtex'">
                                    <a class="toggle-tag-option" (click)="bibtexShowToggle(work.putCode.value)">
                                        [<@orcid.msg 'work.switch_view'/>]
                                    </a>
                                </span>
                                <span *ngIf="(showBibtex[work.putCode.value] == null || showBibtex[work.putCode.value] == false) && worksService.details[work.putCode.value].citation?.citationType?.value == 'bibtex'">
                                    <a class="toggle-tag-option" (click)="bibtexShowToggle(work.putCode.value)">
                                        [<@orcid.msg 'work.switch_view'/>]
                                    </a>
                                </span>
                                
                            </div>
                            <div class="col-md-12">
                            
                                <div *ngIf="worksService.details[work.putCode.value].citation?.citationType?.value != 'bibtex'">
                                    <span>
                                        {{worksService.details[work.putCode.value].citation.citation.value}}
                                    </span>
                                </div>
                                
                                <div *ngIf="(showBibtex[work.putCode.value] == null || showBibtex[work.putCode.value] == false) && worksService.details[work.putCode.value].citation?.citationType?.value == 'bibtex'" 
                                     class="col-md-offset-1 col-md-11 col-sm-offset-1 col-sm-11 col-xs-12 citation-raw">{{worksService.details[work.putCode.value].citation.citation.value}}
                                </div>
                                                    
                                <div class="row" *ngIf="showBibtex[work.putCode.value] && (worksService.bibtexJson[work.putCode.value]==null || worksService.bibtexJson[work.putCode.value].length==0)">
                                    <div class="col-md-offset-1 col-md-6"><@orcid.msg 'work.unavailable_in_html'/></div>
                                </div>
                                
                                
                                <div class="row" *ngFor='let bibJSON of worksService.bibtexJson[work.putCode.value]; let index = index; let first = first; let last = last;'>    
                                    <div *ngIf="showBibtex[work.putCode.value]">
                                        <div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-1 col-xs-offset-1 col-xs-4">{{bibJSON.entryType}}</div>
                                        <div class="col-md-8 col-sm-9 col-xs-offset-1 col-xs-6">{{bibJSON.citationKey}}</div>                               
                                        <div *ngFor="let bibEntry of bibJSON.entryTags">
                                            <div *ngFor="let tag of bibEntry | keys">
                                            <div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-1 col-xs-offset-1 col-xs-4">{{tag.key}}</div>
                                            <div class="col-md-8 col-sm-9 col-xs-offset-1 col-xs-6">{{tag.value | latex}}</div>
                                            </div>
                                        </div>   
                                    </div>                 
                                </div>                      
                            </div>
                        </div>
                        <div class="row bottomBuffer" *ngIf="worksService.details[work.putCode.value].shortDescription?.value"
                            >
                            <div class="col-md-12">
                                <strong> <@orcid.msg
                                    'manual_work_form_contents.labeldescription'/> </strong>
                                <div style="white-space: pre-wrap;">{{worksService.details[work.putCode.value].shortDescription.value}}</div>
                            </div>
                        </div>
                        
                        <div class="row bottomBuffer">
                            <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].countryCode?.value" >

                                <div class="bottomBuffer">
                                    <strong><@orcid.msg
                                        'manual_work_form_contents.labelcountry'/></strong>
                                    <div>{{worksService.details[work.putCode.value].countryName.value}}</div>
                                </div>
                            </div>          
                            <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].contributors?.length > 0" >
                                <div class="bottomBuffer">          
                                    <strong> Contributor </strong>
                                    <div *ngFor="let contributor of worksService.details[work.putCode.value].contributors; let index = index; let first = first; let last = last;">
                                        {{contributor.creditName?.value}} <span>{{contributor | contributorFilter}}</span>
                                    </div>
                                </div>                                      
                            </div>
                            <div class="col-md-6">
                                <div class="bottomBuffer">
                                    <strong><@orcid.msg 'groups.common.created'/></strong><br />
                                    <div>{{worksService.details[work.putCode.value].createdDate | ajaxFormDateToISO8601}}</div>
                                </div>      
                            </div>    
                        </div>
                        
                    </div>  
                </div>

                <div class="row source-line" *ngIf="group.activePutCode == work.putCode.value">
                    <!--Edit sources-->
                    <div class="col-md-7 col-sm-7 col-xs-12" *ngIf="editSources[group.groupId]">
                        {{(work.sourceName == null || work.sourceName == '') ? work.source : work.sourceName }}
                    </div>
                    <div class="col-md-3 col-sm-3 col-xs-10" *ngIf="editSources[group.groupId]">
                        <div *ngIf="editSources[group.groupId]">
                            <span class="glyphicon glyphicon-check" *ngIf="work.putCode.value == group.defaultWork.putCode.value"></span><span *ngIf="work.putCode.value == group.defaultWork.putCode.value"> <@orcid.msg 'groups.common.preferred_source' /></span>
                            
                            <#if !(isPublicProfile??)>
                            <a (click)="makeDefault(group, work.putCode.value)" *ngIf="work.putCode.value != group.defaultWork.putCode.value">
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
                                    <@orcid.editWorkIconNg2
                                        activity="work"
                                        click="openEditWork(work.putCode.value)"
                                        toolTipSuffix="editToolTipSource"
                                        toolTipClass="popover popover-tooltip top edit-activeSource-popover"
                                     />
                                    
                                </li>
                                <li>
                                    <a 
                                        (click)="deleteWorkConfirm(work.putCode.value, false)"  
                                        title="<@orcid.msg 'freemarker.btnDelete' /> {{work.title.value}}" 
                                        (mouseenter)="showTooltip(work.putCode.value+'-deleteActiveSource')" 
                                        (mouseleave)="hideTooltip(work.putCode.value+'-deleteActiveSource')">
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </a>

                                    <div class="popover popover-tooltip top delete-activeSource-popover" *ngIf="showElement[work.putCode.value+'-deleteActiveSource']">
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
                <div *ngIf="group.activePutCode != work.putCode.value" class="row source-line">
                    <div class="col-md-7 col-sm-7 col-xs-12">
                        <a (click)="group.activePutCode = work.putCode.value">                                
                            {{(work.sourceName == null || work.sourceName == '') ? work.source : work.sourceName }}
                        </a>
                    </div>                                        
                    <div class="col-md-3 col-sm-3 col-xs-10">
                        <#if !(isPublicProfile??)>
                        <span class="glyphicon glyphicon-check" *ngIf="work.putCode.value == group.defaultWork.putCode.value"></span><span *ngIf="work.putCode.value == group.defaultWork.putCode.value"> <@orcid.msg 'groups.common.preferred_source' /></span>
                        <a (click)="makeDefault(group, work.putCode.value); " *ngIf="work.putCode.value != group.defaultWork.putCode.value">
                            <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                        </a>
                        </#if>
                    </div>
                    <!--Action buttons-->
                    <div class="col-md-2 col-sm-2 col-xs-2 trash-source">
                        <#if !(isPublicProfile??)>
                        <ul class="sources-actions">
                            <li> 
                                <@orcid.editWorkIconNg2
                                    activity="work"
                                    click="openEditWork(work.putCode.value)"
                                    toolTipSuffix="editToolTipSourceActions"
                                    toolTipClass="popover popover-tooltip top edit-inactiveSource-popover"
                                 />
                            </li>
                            <li>
                                <a (click)="deleteWorkConfirm(work.putCode.value, false)" (mouseenter)="showTooltip(work.putCode.value+'-deleteInactiveSource')" (mouseleave)="hideTooltip(work.putCode.value+'-deleteInactiveSource')">
                                    <span class="glyphicon glyphicon-trash" title="<@orcid.msg 'freemarker.btnDelete'/> {{work.title.value}}"></span>
                                </a>

                                <div class="popover popover-tooltip top delete-inactiveSource-popover" *ngIf="showElement[work.putCode.value+'-deleteInactiveSource'] == true">
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
                        <@orcid.msg 'groups.common.source'/>: {{(work.sourceName == null || work.sourceName == '') ? work.source : work.sourceName }}
                    </div>
                    
                    <div class="col-md-3 col-sm-3 col-xs-9">
                        <span class="glyphicon glyphicon-check"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span> <span *ngIf="group?.works?.length != 1"> (</span><a (click)="showSources(group, $event)" *ngIf="group?.works?.length != 1" (mouseenter)="showTooltip(group.groupId+'-sources')" (mouseleave)="hideTooltip(group.groupId+'-sources')"><@orcid.msg 'groups.common.of'/> {{group.works.length}}</a><span *ngIf="group?.works?.length != 1">)</span>

                        <div class="popover popover-tooltip top sources-popover" *ngIf="showElement[group.groupId+'-sources']">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <@orcid.msg 'groups.common.sources.show_other_sources' />
                            </div>
                        </div>
                    </div>

                    <div class="col-md-2 col-sm-2 col-xs-3" *ngIf="group.activePutCode == work.putCode.value">
                        <ul class="sources-options" >
                            <#if !(isPublicProfile??)>
                            <li>
                                <@orcid.editWorkIconNg2
                                    activity="work"
                                    click="openEditWork(work.putCode.value)"
                                    toolTipSuffix="editToolTip"
                                    toolTipClass="popover popover-tooltip top edit-source-popover"
                                />
                            </li>

                            <li *ngIf="!(editSources[group.groupId] || group?.works?.length == 1)">
                                <a (click)="showSources(group,$event)" (mouseenter)="showTooltip(group.groupId+'-deleteGroup')" (mouseleave)="hideTooltip(group.groupId+'-deleteGroup')">
                                    <span class="glyphicon glyphicon-trash"></span>
                                </a>
                                <div class="popover popover-tooltip top delete-group-popover" *ngIf="showElement[group.groupId+'-deleteGroup']">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                       <@orcid.msg 'groups.common.delete_this_source' />
                                    </div>
                                </div>
                            </li>

                            <li *ngIf="group?.works?.length == 1">
                                <a (click)="deleteWorkConfirm(group.activePutCode, false)" (mouseenter)="showTooltip(group.groupId+'-deleteSource')" (mouseleave)="hideTooltip(group.groupId+'-deleteSource')">
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