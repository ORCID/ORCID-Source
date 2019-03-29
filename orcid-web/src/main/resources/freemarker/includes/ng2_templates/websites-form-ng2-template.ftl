<div id="modalWebsitesForm" class="modal">
    <div class="popover-ng2-bck" (click)="cancelEditModal('websites', 'modalWebsitesForm')"></div>
    <div
        class="popover-ng2-content"
        id="colorbox" 
        role="dialog" 
        style="transition: width 2s, height 2s;"
        tabindex="-1" 
        [ngStyle]="{
        'height': this.elementHeight + 'px',
        'left': 'calc(50% - ' + this.elementWidth/2 + 'px)',
        'top': 'calc(50% - ' + this.elementHeight/2 + 'px)',
        'width': this.elementWidth + 'px'
        }"
    >
        <div id="cboxWrapper" 
            [ngStyle]="{
            'height': this.elementHeight + 'px',
            'width': this.elementWidth + 'px'
            }"
        >
            <div>
                <div id="cboxTopLeft" style="float: left;"></div>
                <div id="cboxTopCenter" style="float: left;"
                    [ngStyle]="{
                    'width': this.elementWidth + 'px'
                    }"
                ></div>
                <div id="cboxTopRight" style="float: left;"></div>
            </div>
            <div style="clear: left;">
                <div id="cboxMiddleLeft" style="float: left;"
                    [ngStyle]="{
                    'height': this.elementHeight + 'px'
                    }"
                ></div>
                <div id="cboxContent" style="float: left;"
                    [ngStyle]="{
                        'height': this.elementHeight + 'px',
                        'width': this.elementWidth + 'px'
                    }"
                >
                    <div id="cboxLoadedContent" style=" overflow: auto;"
                        [ngStyle]="{
                        'height': this.elementHeight + 'px',
                        'width': this.elementWidth + 'px'
                        }"
                    >
                        <div class="lightbox-container">
                            <!--Edit websites-->
                            <div class="edit-record edit-record-bulk-edit edit-websites">
                                <!-- Title -->
                                <div class="row">           
                                    <div class="col-md-12 col-sm-12 col-xs-12"> 
                                        <h1 class="lightbox-title pull-left">
                                            <@orcid.msg 'manage_bio_settings.editWebsites'/>
                                        </h1>
                                    </div>          
                                </div>
                                <div class="row bottomBuffer">                          
                                    <!-- Move this to component - Begin of bulk component-->
                                    <div class="row bulk-edit-modal">
                                        <div class="pull-right bio-edit-modal">             
                                            <span class="right"><@orcid.msg 'groups.common.bulk_edit_privacy'/></span>
                                            <div class="bulk-privacy-bar">
                                                <div [ngClass]="{'relative' : modal == false}" id="privacy-bar">
                                                    <ul class="privacyToggle" (mouseenter)="commonSrvc.showPrivacyHelp(bulkEdit +'-privacy', $event, 145)" (mouseleave)="commonSrvc.hideTooltip(bulkEdit +'-privacy')">
                                                        <li class="publicActive publicInActive" [ngClass]="{publicInActive: bioModel != 'PUBLIC'}"><a (click)="setBulkGroupPrivacy('PUBLIC', 'websites')" name="privacy-toggle-3-public" id=""></a></li>
                                                        <li class="limitedActive limitedInActive" [ngClass]="{limitedInActive: bioModel != 'LIMITED'}"><a (click)="setBulkGroupPrivacy('LIMITED', 'websites')" name="privacy-toggle-3-limited" id=""></a></li>
                                                        <li class="privateActive privateInActive" [ngClass]="{privateInActive: bioModel != 'PRIVATE'}"><a (click)="setBulkGroupPrivacy('PRIVATE', 'websites')" name="privacy-toggle-3-private" id=""></a></li>
                                                    </ul>
                                                </div>
                                                <div class="popover-help-container" style="top: 110px; left: 492px;">
                                                    <div class="popover bottom privacy-myorcid3" [ngClass]="commonSrvc.shownElement[bulkEdit +'-privacy'] == true ? 'block' : ''">
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                            <strong><@orcid.msg 'privacyToggle.help.who_can_see'/> </strong>
                                                            <ul class="privacyHelp">
                                                                <li class="public" style="color: #009900;"><@orcid.msg 'privacyToggle.help.everyone'/></li>
                                                                <li class="limited" style="color: #ffb027;"><@orcid.msg 'privacyToggle.help.trusted_parties'/></li>
                                                                <li class="private" style="color: #990000;"><@orcid.msg 'privacyToggle.help.only_me'/></li>
                                                            </ul>
                                                            <a href="<@orcid.msg 'common.kb_uri_default'/>360006897614" target="privacyToggle.help.more_information"><@orcid.msg 'privacyToggle.help.more_information'/></a>
                                                        </div>                
                                                    </div>                              
                                                </div>

                                            </div>
                                            <div class="bulk-help popover-help-container">
                                                <i class="glyphicon glyphicon-question-sign"></i>
                                                <div id="bulk-help" class="popover bottom">
                                                    <div class="arrow"></div>
                                                    <div class="popover-content">
                                                        <p><@orcid.msg 'groups.common.bulk_edit_privacy_help'/></p>
                                                    </div>
                                               </div>
                                            </div>
                                        </div>          
                                    </div>
                                    <!-- End of bulk edit -->                     
                                </div>              
                                <div class="row">
                                    <div class="col-md-12 col-sm-12 col-xs-12 padding-right-reset">
                                        <span class="right"><@orcid.msg 'groups.common.edit_individual_privacy' /></span>   
                                    </div>
                                </div>      
                                <div class="row">
                                    <div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
                                        <div class="fixed-area" scroll>             
                                            <div class="scroll-area" id="websites">       
                                                <div class="row aka-row websites" *ngFor="let website of formData['websites'].websites; let index = index; let first = first; let last = last;">
                                                    <div class="col-md-6">
                                                        <div class="aka">                                       
                                                            <input type="text" [(ngModel)]="website.urlName" *ngIf="website.source == orcidId" [focusMe]="newInput" [ngClass]="{'focusInput' : !website.urlName}"placeholder="${springMacroRequestContext.getMessage('manual_work_form_contents.labeldescription')}" />
                                                            <input class="website-value" type="text" [(ngModel)]="website.url.value" *ngIf="website.source == orcidId" placeholder="${springMacroRequestContext.getMessage('common.url')}" />
                                                            <a href="{{website.url.value}}" target="website.urlName" rel="me nofollow" *ngIf="website.source != orcidId" >{{website.urlName != null? website.urlName : website.url.value}}</a>
                                                        </div>
                                                        <div class="source" *ngIf="website.sourceName || website.sourceName == null">
                                                            <b><@orcid.msg 'manage_bio_settings.source'/>: </b> <span *ngIf="website.sourceName">{{website.sourceName}}
                                                            

                                                             <#--  OBO  -->
                                                            <ng-container *ngIf="(website.assertionOriginClientId && website.assertionOriginClientId !== website.source) ||
                                                            (website.assertionOriginOrcid && website.assertionOriginOrcid !== website.source.source)">
                                                            <i>${springMacroRequestContext.getMessage("public_profile.onBehalfOf")}</i> {{website.assertionOriginName || website.assertionOriginOrcid}}
                                                            </ng-container>
                                                            
                                                            </span><span *ngIf="website.sourceName == null">{{orcidId}}</span>
                                                        </div>                                                                            
                                                    </div>
                                                    
                                                    <div class="col-md-6" style="position: static">
                                                        <ul class="record-settings pull-right">
                                                            <li>                                            
                                                                <div class="glyphicon glyphicon-arrow-up circle" (click)="swapUp(index, 'websites')" (mouseenter)="commonSrvc.showTooltip('tooltip-websites-move-up-'+index, $event, 37, -33, 44)" (mouseleave)="commonSrvc.hideTooltip('tooltip-websites-move-up-'+index)"></div>
                                                                <@orcid.tooltipNg2 elementId="'tooltip-websites-move-up-'+index" message="common.modals.move_up"/>
                                                            </li>
                                                            <li>                                                                                        
                                                                <div class="glyphicon glyphicon-arrow-down circle" (click)="swapDown(index, 'websites')" (mouseenter)="commonSrvc.showTooltip('tooltip-websites-move-down-'+index, $event, 37, -2, 53)" (mouseleave)="commonSrvc.hideTooltip('tooltip-websites-move-down-'+index)"></div>
                                                                <@orcid.tooltipNg2 elementId="'tooltip-websites-move-down-'+index" message="common.modals.move_down" />
                                                            </li>
                                                            <li>                                        
                                                                <div id="delete-website" class="glyphicon glyphicon-trash" (click)="deleteSectionItem(website, 'websites', index, 'tooltip-websites-delete-'+index)" (mouseenter)="commonSrvc.showTooltip('tooltip-websites-delete-'+index, $event, 37, 50, 39)" (mouseleave)="commonSrvc.hideTooltip('tooltip-websites-delete-'+index)"></div>
                                                                <@orcid.tooltipNg2 elementId="'tooltip-websites-delete-'+index" message="common.modals.delete" />
                                                            </li>
                                                            <li>
                                                                <privacy-toggle-ng2 
                                                                [dataPrivacyObj]="website" 
                                                                (privacyUpdate)="privacyChange($event, 'websites')"
                                                                elementId="websites-privacy-toggle" 
                                                                privacyNodeName="visibility" 
                                                                ></privacy-toggle-ng2> 
                                                            </li>
                                                        </ul>
                                                        <span class="created-date pull-right" *ngIf="website.createdDate" [ngClass]="{'hidden-xs' : website.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{website.createdDate.year + '-' + website.createdDate.month + '-' + website.createdDate.day}}</span>
                                                        <span class="created-date pull-left" *ngIf="website.createdDate" [ngClass]="{'visible-xs' : website.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{website.createdDate.year + '-' + website.createdDate.month + '-' + website.createdDate.day}}</span>
                                                    </div>                              
                                                    <div *ngIf="website?.errors?.length > 0" class="col-md-12">                                 
                                                        <div *ngFor="let error of website.errors">
                                                            <span class="red">{{error}}</span>
                                                        </div>
                                                    </div>                                  
                                                </div>                                                                                              
                                            </div>
                                        </div>
                                        
                                        <div class="record-buttons">                        
                                            <a (click)="addSectionItem('websites')" id="add-website"><span class="glyphicon glyphicon-plus pull-left">
                                                <div class="popover popover-tooltip-add top">
                                                    <div class="arrow"></div>
                                                    <div class="popover-content">
                                                        <span><@orcid.msg 'common.modals.add' /></span>
                                                    </div>
                                                </div>
                                            </span></a>                         
                                            <button class="btn btn-primary pull-right" (click)="setFormData(true, 'websites', 'modalWebsitesForm')"><@spring.message "freemarker.btnsavechanges"/></button>
                                            <a class="cancel-option pull-right" (click)="cancelEditModal('websites', 'modalWebsitesForm')"><@spring.message "freemarker.btncancel"/></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!--End edit websites-->
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>