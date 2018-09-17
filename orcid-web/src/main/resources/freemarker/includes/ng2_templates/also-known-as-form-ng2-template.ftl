<div id="modalAlsoKnownAsForm" class="modal">
    <div class="popover-ng2-bck" (click)="cancelEditModal('otherNames', 'modalAlsoKnownAsForm')"></div>
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
                            <!-- Other Names -->    
                            <div class="edit-record edit-record-bulk-edit edit-aka">
                                <!-- Title -->
                                <div class="row">           
                                    <div class="col-md-12 col-sm-12 col-xs-12"> 
                                        <h1 class="lightbox-title pull-left">
                                            <@orcid.msg 'manage_bio_settings.editOtherNames'/>
                                        </h1>
                                    </div>          
                                </div>
                                    
                                <div class="row bottomBuffer">                          
                                    <div class="row bulk-edit-modal">
                                        <div class="pull-right bio-edit-modal">             
                                            <span class="right"><@orcid.msg 'groups.common.bulk_edit_privacy'/></span>
                                            <div class="bulk-privacy-bar">
                                                <div [ngClass]="{'relative' : modal == false}" id="privacy-bar">
                                                    <ul class="privacyToggle" (mouseenter)="commonSrvc.showPrivacyHelp(bulkEdit +'-privacy', $event, 145)" (mouseleave)="commonSrvc.hideTooltip(bulkEdit +'-privacy')">
                                                        <li class="publicActive publicInActive" [ngClass]="{publicInActive: bioModel != 'PUBLIC'}"><a (click)="setBulkGroupPrivacy('PUBLIC', 'otherNames')" name="privacy-toggle-3-public" id=""></a></li>
                                                        <li class="limitedActive limitedInActive" [ngClass]="{limitedInActive: bioModel != 'LIMITED'}"><a (click)="setBulkGroupPrivacy('LIMITED', 'otherNames')" name="privacy-toggle-3-limited" id=""></a></li>
                                                        <li class="privateActive privateInActive" [ngClass]="{privateInActive: bioModel != 'PRIVATE'}"><a (click)="setBulkGroupPrivacy('PRIVATE', 'otherNames')" name="privacy-toggle-3-private" id=""></a></li>
                                                    </ul>
                                                </div>
                                                <div class="popover-help-container" style="top: -75px; left: 512px;">
                                                    <div class="popover top privacy-myorcid3" [ngClass]="commonSrvc.shownElement[bulkEdit +'-privacy'] == true ? 'block' : ''">
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
                                </div>              
                                <div class="row">
                                    <div class="col-md-12 col-sm-12 col-xs-12 padding-right-reset">
                                        <span class="right"><@orcid.msg 'groups.common.edit_individual_privacy' /></span>   
                                    </div>
                                </div>      

                                <div class="row">
                                    <div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
                                        <div class="fixed-area" scroll>
                                            <div id="other-names" class="scroll-area">
                                               <div class="row aka-row" *ngFor="let otherName of formData['otherNames'].otherNames; let index = index; let first = first; let last = last;" >                                                            
                                                    <div class="col-md-6 col-sm-6 col-xs-12">
                                                        <div class="aka" *ngIf="otherName">   
                                                            <input class="other-name-content" type="text" [(ngModel)]="otherName.content" *ngIf="otherName.source == orcidId" [focusMe]="newInput" [ngClass]="{'focusInput' : !otherName.content}" />  
                                                            <span *ngIf="otherName.source != orcidId && otherName.source != null">{{otherName.content}}</span>                                       
                                                        </div>                                      
                                                        <div class="source" *ngIf="otherName.sourceName || otherName.sourceName == null">
                                                            <@orcid.msg 'manage_bio_settings.source'/>: <span *ngIf="otherName.sourceName">{{otherName.sourceName}}</span><span *ngIf="otherName.sourceName == null">{{orcidId}}</span>
                                                        </div>
                                                    </div>                          
                                                    <div class="col-md-6 col-sm-6 col-xs-12" style="position: static">                                                                                                                          
                                                        <ul class="record-settings pull-right">
                                                            <li>
                                                                <div class="glyphicon glyphicon-arrow-up circle" (click)="first || swapUp(index, 'otherNames')" (mouseenter)="commonSrvc.showTooltip('tooltip-aka-move-up-'+index, $event, 37, -33, 44)" (mouseleave)="commonSrvc.hideTooltip('tooltip-aka-move-up-'+index)"></div>                                         
                                                                <@orcid.tooltipNg2 elementId="'tooltip-aka-move-up-'+index" message="common.modals.move_up"/>
                                                            </li>
                                                            <li>                                                                                        
                                                                <div class="glyphicon glyphicon-arrow-down circle" (click)="last || swapDown(index, 'otherNames')" (mouseenter)="commonSrvc.showTooltip('tooltip-aka-move-down-'+index, $event, 37, -2, 53)" (mouseleave)="commonSrvc.hideTooltip('tooltip-aka-move-down-'+index)"></div>
                                                                <@orcid.tooltipNg2 elementId="'tooltip-aka-move-down-'+index" message="common.modals.move_down" />                                            
                                                            </li>
                                                            <li>
                                                                <div class="glyphicon glyphicon-trash delete-other-name" (click)="deleteSectionItem(otherName, 'otherNames', index, 'tooltip-aka-delete-'+index)" (mouseenter)="commonSrvc.showTooltip('tooltip-aka-delete-'+index, $event, 37, 50, 39)" (mouseleave)="commonSrvc.hideTooltip('tooltip-aka-delete-'+index)"></div>
                                                                <@orcid.tooltipNg2 elementId="'tooltip-aka-delete-'+index" message="common.modals.delete" />
                                                            </li>
                                                            <li>
                                                                <privacy-toggle-ng2 
                                                                [dataPrivacyObj]="otherName" 
                                                                (privacyUpdate)="privacyChange($event, 'otherNames')"
                                                                elementId="also-known-as-privacy-toggle" 
                                                                privacyNodeName="visibility" 
                                                                ></privacy-toggle-ng2> 
                                                            </li>
                                                        </ul>
                                                        <span class="created-date pull-right" *ngIf="otherName.createdDate" [ngClass]="{'hidden-xs' : otherName.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{otherName.createdDate.year + '-' + otherName.createdDate.month + '-' + otherName.createdDate.day}}</span>
                                                        <span class="created-date pull-left" *ngIf="otherName.createdDate" [ngClass]="{'visible-xs' : otherName.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{otherName.createdDate.year + '-' + otherName.createdDate.month + '-' + otherName.createdDate.day}}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="record-buttons">
                                            <a (click)="addSectionItem('otherNames')" id="add-other-name"><span class="glyphicon glyphicon-plus pull-left">
                                                <div class="popover popover-tooltip-add top">
                                                    <div class="arrow"></div>
                                                    <div class="popover-content">
                                                        <span><@orcid.msg 'common.modals.add' /></span>
                                                    </div>
                                                </div> 
                                            </span></a>                         
                                            <button class="btn btn-primary pull-right" (click)="setFormData( true, 'otherNames', 'modalAlsoKnownAsForm' )"><@spring.message "freemarker.btnsavechanges"/></button>                           
                                            <a class="cancel-option pull-right" (click)="cancelEditModal('otherNames', 'modalAlsoKnownAsForm')"><@spring.message "freemarker.btncancel"/></a>
                                        </div>                  
                                    </div>
                                </div>
                            </div>
                            <!--End edit also known as-->
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>     