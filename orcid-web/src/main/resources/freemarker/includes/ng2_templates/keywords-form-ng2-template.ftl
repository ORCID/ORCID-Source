<script type="text/ng-template" id="keywords-form-ng2-template">
    <div class="edit-record 
        <#if RequestParameters['bulkEdit']??>edit-record-bulk-edit</#if> 
        edit-keyword"
    >
        <!-- Title -->
        <div class="row">           
            <div class="col-md-12 col-sm-12 col-xs-12"> 
                <h1 class="lightbox-title pull-left">
                    <@orcid.msg 'manage_bio_settings.editKeywords'/>        
                </h1>                   
            </div>          
        </div>
        <div class="row bottomBuffer">
            <!--                      
            <div ng-include="'bulk-edit'"></div>
            -->             
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
                        <div class="row aka-row" *ngFor="let keyword of formData.keywords; let index = index; let first = first; let last = last;">      
                            <div class="col-md-6">
                                <div class="aka" *ngIf="keyword">                                       
                                    <input type="text" [(ngModel)]="keyword.content" *ngIf="keyword.source == orcidId" focus-me="newInput" />
                                    <span *ngIf="keyword.source != orcidId">{{keyword.content}}</span>                                     
                                </div>
                                <div class="source" *ngIf="keyword.sourceName || keyword.sourceName == null"><@orcid.msg 'manage_bio_settings.source'/>: <span *ngIf="keyword.sourceName">{{keyword.sourceName}}</span><span *ngIf="keyword.sourceName == null">{{orcidId}}</span></div>      
                            </div>
                            
                            <div class="col-md-6" style="position: static">
                                <ul class="record-settings pull-right">
                                    <li>                            
                                        <div class="glyphicon glyphicon-arrow-up circle" (click)="first || swapUp(index)" (mouseenter)="commonSrvc.showTooltip('tooltip-keyword-move-up-'+index, $event, 37, -33, 44)" (mouseleave)="commonSrvc.hideTooltip('tooltip-keyword-move-up-'+index)"></div>
                                        <@orcid.tooltipNg2 elementId="'tooltip-keyword-move-up-'+index" message="common.modals.move_up"/>
                                    </li>
                                    <li>                                                                                        
                                        <div class="glyphicon glyphicon-arrow-down circle" (click)="last || swapDown(index)" (mouseenter)="commonSrvc.showTooltip('tooltip-keyword-move-down-'+index, $event, 37, -2, 53)" (mouseleave)="commonSrvc.hideTooltip('tooltip-keyword-move-down-'+index)"></div>
                                        <@orcid.tooltipNg2 elementId="'tooltip-keyword-move-down-'+index" message="common.modals.move_down" />                                            
                                    </li>
                                    <li>                                        
                                        <div class="glyphicon glyphicon-trash" (click)="deleteKeyword(keyword)" (mouseenter)="commonSrvc.showTooltip('tooltip-keyword-delete-'+index, $event, 37, 50, 39)" (mouseleave)="commonSrvc.hideTooltip('tooltip-keyword-delete-'+index)"></div>
                                        <@orcid.tooltipNg2 elementId="'tooltip-keyword-delete-'+index" message="common.modals.delete" />                                          
                                    </li>
                                    <li>
                                        <privacy-toggle-ng2 
                                        [dataPrivacyObj]="keyword" 
                                        (privacyUpdate)="privacyChange($event)"
                                        elementId="keywords-privacy-toggle" 
                                        privacyNodeName="visibility" 
                                        ></privacy-toggle-ng2> 
                                    </li>
                                </ul>
                                <span class="created-date pull-right" *ngIf="keyword.createdDate" [ngClass]="{'hidden-xs' : keyword.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{keyword.createdDate.year + '-' + keyword.createdDate.month + '-' + keyword.createdDate.day}}</span>
                                <span class="created-date pull-left" *ngIf="keyword.createdDate" [ngClass]="{'visible-xs' : keyword.createdDate}"><@orcid.msg 'manage_bio_settings.created'/>: {{keyword.createdDate.year + '-' + keyword.createdDate.month + '-' + keyword.createdDate.day}}</span>
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
                    <button class="btn btn-primary pull-right" (click)="setForm( true )"><@spring.message "freemarker.btnsavechanges"/></button>
                    <a class="cancel-option pull-right" (click)="closeEditModal()"><@spring.message "freemarker.btncancel"/></a>
                </div>
            </div>
        </div>
    </div>
</script>