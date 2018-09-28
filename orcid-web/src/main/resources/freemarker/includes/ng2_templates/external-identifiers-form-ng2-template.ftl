<div id="modalExtIdsForm" class="modal">
    <div class="popover-ng2-bck" (click)="cancelEditModal('externalIdentifiers', 'modalExtIdsForm')"></div>
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
                            <!--Edit ext ids-->
                            <div class="edit-record edit-record-bulk-edit edit-external-identifiers">
                                <!-- Title -->
                                <div class="row">           
                                    <div class="col-md-12 col-sm-12 col-xs-12"> 
                                        <h1 class="lightbox-title pull-left">
                                            <@orcid.msg 'manage_bio_settings.editExternalIdentifiers'/>
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
                                                        <li class="publicActive publicInActive" [ngClass]="{publicInActive: bioModel != 'PUBLIC'}"><a (click)="setBulkGroupPrivacy('PUBLIC', 'externalIdentifiers')" name="privacy-toggle-3-public" id=""></a></li>
                                                        <li class="limitedActive limitedInActive" [ngClass]="{limitedInActive: bioModel != 'LIMITED'}"><a (click)="setBulkGroupPrivacy('LIMITED', 'externalIdentifiers')" name="privacy-toggle-3-limited" id=""></a></li>
                                                        <li class="privateActive privateInActive" [ngClass]="{privateInActive: bioModel != 'PRIVATE'}"><a (click)="setBulkGroupPrivacy('PRIVATE', 'externalIdentifiers')" name="privacy-toggle-3-private" id=""></a></li>
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
                                            <div class="scroll-area" id="externalIdentifiers">       
                                                <div class="row aka-row external-identifiers" *ngFor="let externalIdentifier of formData['externalIdentifiers'].externalIdentifiers; let index = index; let first = first; let last = last;">
                                                    <div class="col-md-6">
                                                        <div class="aka">                                       
                                                            <p>
                                                                <span *ngIf="!externalIdentifier.url">{{externalIdentifier.commonName}} {{externalIdentifier.reference}}</span>
                                                                <span *ngIf="externalIdentifier.url"><a href="{{externalIdentifier.url}}" target="externalIdentifier.commonName">{{externalIdentifier.commonName}} {{externalIdentifier.reference}}</a></span>
                                                            </p> 
                                                        </div>
                                                        <div class="source"><@orcid.msg 'manage_bio_settings.source'/>: <span *ngIf="externalIdentifier.sourceName">{{externalIdentifier.sourceName}}</span><span *ngIf="externalIdentifier.sourceName == null">{{orcidId}}</span></div>                                
                                                    </div>
                                                    
                                                    <div class="col-md-6" style="position: static">
                                                        <ul class="record-settings pull-right">
                                                            <li>                                            
                                                                <div class="glyphicon glyphicon-arrow-up circle" (click)="swapUp(index, 'externalIdentifiers')" (mouseenter)="commonSrvc.showTooltip('tooltip-external-identifiers-move-up-'+index, $event, 37, -33, 44)" (mouseleave)="commonSrvc.hideTooltip('tooltip-external-identifiers-move-up-'+index)"></div>
                                                                <@orcid.tooltipNg2 elementId="'tooltip-external-identifiers-move-up-'+index" message="common.modals.move_up"/>
                                                            </li>
                                                            <li>                                                                                        
                                                                <div class="glyphicon glyphicon-arrow-down circle" (click)="swapDown(index, 'externalIdentifiers')" (mouseenter)="commonSrvc.showTooltip('tooltip-external-identifiers-move-down-'+index, $event, 37, -2, 53)" (mouseleave)="commonSrvc.hideTooltip('tooltip-external-identifiers-move-down-'+index)"></div>
                                                                <@orcid.tooltipNg2 elementId="'tooltip-external-identifiers-move-down-'+index" message="common.modals.move_down" />
                                                            </li>
                                                            <li>                                        
                                                                <div id="delete-ext-id" class="glyphicon glyphicon-trash" (click)="deleteSectionItem(externalIdentifier, 'externalIdentifiers', index, 'tooltip-external-identifiers-delete-'+index)" (mouseenter)="commonSrvc.showTooltip('tooltip-external-identifiers-delete-'+index, $event, 37, 50, 39)" (mouseleave)="commonSrvc.hideTooltip('tooltip-external-identifiers-delete-'+index)"></div>
                                                                <@orcid.tooltipNg2 elementId="'tooltip-external-identifiers-delete-'+index" message="common.modals.delete" />
                                                            </li>
                                                            <li>
                                                                <privacy-toggle-ng2 
                                                                [dataPrivacyObj]="externalIdentifier" 
                                                                (privacyUpdate)="privacyChange($event, 'externalIdentifiers')"
                                                                elementId="external-identifiers-privacy-toggle" 
                                                                privacyNodeName="visibility" 
                                                                ></privacy-toggle-ng2> 
                                                            </li>
                                                        </ul>
                                                        <span class="created-date pull-right" *ngIf="externalIdentifier.createdDate" [ngClass]="{'hidden-xs' : externalIdentifier.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{externalIdentifier.createdDate.year + '-' + externalIdentifier.createdDate.month + '-' + externalIdentifier.createdDate.day}}</span>
                                                        <span class="created-date pull-left" *ngIf="externalIdentifier.createdDate" [ngClass]="{'visible-xs' : externalIdentifier.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{externalIdentifier.createdDate.year + '-' + externalIdentifier.createdDate.month + '-' + externalIdentifier.createdDate.day}}</span>
                                                    </div>                              
                                                    <div *ngIf="externalIdentifier?.errors?.length > 0" class="col-md-12">                                 
                                                        <div *ngFor="let error of externalIdentifier.errors">
                                                            <span class="red">{{error}}</span>
                                                        </div>
                                                    </div>                                  
                                                </div>                                                                                              
                                            </div>
                                        </div>
                                        
                                        <div class="record-buttons">                
                                            <button class="btn btn-primary pull-right" (click)="setFormData(true, 'externalIdentifiers', 'modalExtIdsForm')"><@spring.message "freemarker.btnsavechanges"/></button>
                                            <a class="cancel-option pull-right" (click)="cancelEditModal('externalIdentifiers', 'modalExtIdsForm')"><@spring.message "freemarker.btncancel"/></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!--End edit ext ids-->
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>