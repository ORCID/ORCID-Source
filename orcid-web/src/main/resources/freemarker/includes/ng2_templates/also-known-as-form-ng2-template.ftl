<script type="text/ng-template" id="also-known-as-form-ng2-template">  
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
            <div ng-include="'bulk-edit'"></div>                    
        </div>              
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12 padding-right-reset">
                <span class="right"><@orcid.msg 'groups.common.edit_individual_privacy' /></span>   
            </div>
        </div>      

        <div class="row">
            <div class="col-md-12 col-xs-12 col-sm-12" style="position: static">
                <div class="fixed-area" scroll>
                    <div class="scroll-area">
                       <div class="row aka-row" *ngFor="let otherName of formData.otherNames; let index = index; let first = first; let last = last;" >                                                            
                            <div class="col-md-6 col-sm-6 col-xs-12">
                                <div class="aka" *ngIf="otherName">   
                                    <input type="text" [(ngModel)]="otherName.content" *ngIf="otherName.source == orcidId" focus-me="newInput" />  
                                    <span *ngIf="otherName.source != orcidId && otherName.source != null">{{otherName.content}}</span>                                       
                                </div>                                      
                                <div class="source" *ngIf="otherName.sourceName || otherName.sourceName == null">
                                    <@orcid.msg 'manage_bio_settings.source'/>: <span *ngIf="otherName.sourceName">{{otherName.sourceName}}</span><span *ngIf="otherName.sourceName == null">{{orcidId}}</span>
                                </div>
                            </div>                          
                            <div class="col-md-6 col-sm-6 col-xs-12" style="position: static">                                                                                                                          
                                <ul class="record-settings pull-right">
                                    <li>
                                        <div class="glyphicon glyphicon-arrow-up circle" (click)="first || swapUp(index)" (mouseenter)="commonSrvc.showTooltip('tooltip-aka-move-up-'+index, $event, 37, -33, 44)" (mouseleave)="commonSrvc.hideTooltip('tooltip-aka-move-up-'+index)"></div>                                         
                                        <@orcid.tooltipNg2 elementId="'tooltip-aka-move-up-'+index" message="common.modals.move_up"/>
                                    </li>
                                    <li>                                                                                        
                                        <div class="glyphicon glyphicon-arrow-down circle" (click)="last || swapDown(index)" (mouseenter)="commonSrvc.showTooltip('tooltip-aka-move-down-'+index, $event, 37, -2, 53)" (mouseleave)="commonSrvc.hideTooltip('tooltip-aka-move-down-'+index)"></div>
                                        <@orcid.tooltipNg2 elementId="'tooltip-aka-move-down-'+index" message="common.modals.move_down" />                                            
                                    </li>
                                    <li>
                                        <div class="glyphicon glyphicon-trash" (click)="deleteOtherName(otherName)" (mouseenter)="commonSrvc.showTooltip('tooltip-aka-delete-'+index, $event, 37, 50, 39)" (mouseleave)="commonSrvc.hideTooltip('tooltip-aka-delete-'+index)"></div>
                                        <@orcid.tooltipNg2 elementId="'tooltip-aka-delete-'+index" message="common.modals.delete" />
                                    </li>
                                    <li>
                                        <privacy-toggle-ng2 
                                        [dataPrivacyObj]="otherName" 
                                        (privacyUpdate)="privacyChange($event)"
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
                    <a (click)="addNew()"><span class="glyphicon glyphicon-plus pull-left">
                        <div class="popover popover-tooltip-add top">
                            <div class="arrow"></div>
                            <div class="popover-content">
                                <span><@orcid.msg 'common.modals.add' /></span>
                            </div>
                        </div> 
                    </span></a>                         
                    <button class="btn btn-primary pull-right" (click)="setFormData( true )"><@spring.message "freemarker.btnsavechanges"/></button>                           
                    <a class="cancel-option pull-right" (click)="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
                </div>                  
            </div>
        </div>
    </div>    
</script>